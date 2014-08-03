package com.tuneit.salsa3;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

import com.tuneit.salsa3.model.*;

import static com.tuneit.salsa3.ast.ClassDeclaration.*;

public class SourceManager {
	private static final String PERSISTENCE_UNIT_NAME = "salsaPU";
	private static SourceManager _instance = null;
	
	private static Logger log = Logger.getLogger(SourceManager.class.getName());
	
	private EntityManager em;
		
	public SourceManager() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		
		em = emf.createEntityManager();
	}
	
	public static SourceManager getInstance() {
		if(_instance == null) {
			_instance = new SourceManager();
		}
		
		return _instance;
	}
	
	private static class SelectQuery {		
		private StringBuilder sbSelect;
		private StringBuilder sbWhere;
		private Map<String, Object> queryParams;
		private boolean isMerged;
		
		public SelectQuery() {
			this.sbSelect = new StringBuilder();
			this.sbWhere = new StringBuilder();
			this.queryParams = new HashMap<String, Object>();
			this.isMerged = false;
		}
		
		public void start(String select) {
			sbSelect.append(select);
			sbSelect.append(' ');
		}
		
		public void join(String joinClause) {
			sbSelect.append(joinClause);
			sbSelect.append(' ');
		}
		
		public void where(String whereClause) {
			sbWhere.append(whereClause);
			sbWhere.append(' ');
		}
		
		public void setParameter(String param, Object value) {
			queryParams.put(param, value);
		}
		
		public void setLikeParameter(String param, String value) {
			value = value.replaceAll("%", "\\%").replace('*', '%');
			queryParams.put(param, value);
		}
		
		public void addClassSourceJoins(Repository repository, Source source) {
			if(source != null) {
				where("AND sr.source = :source ");
				setParameter("source", source);
			}
			else if(repository != null) {
				join("LEFT JOIN sr.source s ");
				where("AND s.repository = :repository ");
				setParameter("repository", repository);
			}
			else {
				throw new NullPointerException("Both repository and source are null");
			}
		}
		
		public Query createQuery(EntityManager em) {
			merge();
			
			String queryStr = sbSelect.toString();			
			log.fine(queryStr);
			
			Query query = em.createQuery(queryStr);
			for(Entry<String, Object> param : queryParams.entrySet()) {
				query.setParameter(param.getKey(), param.getValue());
			}
			
			return query;
		}
		
		private void merge() {
			if(isMerged) {
				throw new IllegalStateException("Query is already merged!");
			}
			
			String where = sbWhere.toString();
			int whereOffset = 0;
			
			if(where.startsWith("AND")) {
				whereOffset = 3;
			}
			else if(where.startsWith("OR")) {
				whereOffset = 2;
			}
			
			sbSelect.append("WHERE ");
			sbSelect.append(where.substring(whereOffset));
			
			isMerged = true;
		}
		
		public String toString() {
			merge();
			return sbSelect.toString();
		}
	}
	
	public static class SelectClassQuery extends SelectQuery {
		public void start() {
			start("SELECT c FROM ClassDeclaration c");
		}
		
		public void addClassSourceJoins(Repository repository, Source source) {
			join("LEFT JOIN c.sourceReference sr");
			
			super.addClassSourceJoins(repository, source);
		}
	}
	
	public static class SelectSnippetQuery extends SelectQuery {
		public void start() {
			start("SELECT ss FROM SourceSnippet ss");
		}
		
		public void addClassSourceJoins(Repository repository, Source source) {
			join("LEFT JOIN ss.sourceReference sr");
			
			super.addClassSourceJoins(repository, source);
		}
		
		public void filterClassMembers() {
			where("AND NOT EXISTS(SELECT cm FROM ClassMember cm WHERE cm.code = ss)");
		}
	}
	
	public static class SelectSourceReferenceQuery extends SelectQuery {
		public void start() {
			start("SELECT sr FROM SourceReference sr");
		}
		
		public void addClassSourceJoins(Repository repository, Source source) {
			super.addClassSourceJoins(repository, source);
		}
	}
		
	public static class SubClassReference {
		private SuperClassReference reference;
		private ClassDeclaration subClass;
		
		public SubClassReference(SuperClassReference superClass,
				ClassDeclaration subClass) {
			super();
			
			this.reference = superClass;
			this.subClass = subClass;
		}

		public SuperClassReference getReference() {
			return reference;
		}

		public ClassDeclaration getSubClass() {
			return subClass;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ClassDeclaration> findClasses(Repository repository, Source source, 
			String classNameMask, String superClassName, Type[] classTypeFilter) {
		SelectClassQuery classSelector = new SelectClassQuery();
		
		classSelector.start();
		
		classSelector.addClassSourceJoins(repository, source);
		
		if(classNameMask != null) {
			classSelector.where("AND c.className LIKE :classNameMask ");
			classSelector.setLikeParameter("classNameMask", classNameMask);
		}
		
		if(superClassName != null) {
			classSelector.join("LEFT JOIN c.superClasses sc ");
			classSelector.where("AND sc.superClassName = :superClassName ");
			classSelector.setParameter("superClassName", superClassName);
		}
		
		if(classTypeFilter.length > 0) {
			classSelector.where("AND c.classType IN :classTypeMask");
			classSelector.setParameter("classTypeMask", Arrays.asList(classTypeFilter));
		}
		
		Query query = classSelector.createQuery(em);
		
		return (List<ClassDeclaration>) query.getResultList();
	}
	
	public ClassDeclaration getClassByName(Repository repository, Source source, String name) {
		SelectClassQuery classSelector = new SelectClassQuery();	
		classSelector.start();
		
		classSelector.addClassSourceJoins(repository, source);
		
		classSelector.where("AND c.className = :className");
		classSelector.setParameter("className", name);
		
		Query query = classSelector.createQuery(em);		
		return (ClassDeclaration) query.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<ClassMember> getClassMembers(ClassDeclaration klass) {
		Query query = em.createQuery("SELECT cm FROM ClassMember cm WHERE cm.klass = :klass");
		query.setParameter("klass", klass);
		
		return (List<ClassMember>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<SubClassReference> getSubClasses(Repository repository, Source source, String name) {
		SelectClassQuery classSelector = new SelectClassQuery();	
		classSelector.start("SELECT new com.tuneit.salsa3.SourceManager.SubClassReference(sc, c) FROM ClassDeclaration c");
		
		classSelector.addClassSourceJoins(repository, source);
		
		classSelector.join("LEFT JOIN c.superClasses sc ");
		
		classSelector.where("AND sc.superClassName = :superClassName");		
		classSelector.setParameter("superClassName", name);
		
		Query query = classSelector.createQuery(em);
		
		return (List<SubClassReference>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<SourceSnippet> getSourceSnippets(Repository repository, Source source, boolean showClassMembers,
					String nameMask, SourceSnippet.Type[] snippetTypeFilter) {
		SelectSnippetQuery snippetSelector = new SelectSnippetQuery();
		snippetSelector.start();
		
		snippetSelector.addClassSourceJoins(repository, source);
		
		if(!showClassMembers) {
			snippetSelector.filterClassMembers();
		}
		
		if(nameMask != null) {
			snippetSelector.where("AND ss.name LIKE :nameMask ");
			snippetSelector.setLikeParameter("nameMask", nameMask);
		}
		
		if(snippetTypeFilter.length > 0) {
			snippetSelector.where("AND ss.type IN :classTypeMask");
			snippetSelector.setParameter("classTypeMask", Arrays.asList(snippetTypeFilter));
		}
		
		Query query = snippetSelector.createQuery(em);
		
		return (List<SourceSnippet>) query.getResultList();
	}
	
	public SourceSnippet getSourceSnippetById(int id) {
		SelectSnippetQuery snippetSelector = new SelectSnippetQuery();
		snippetSelector.start();
		snippetSelector.where("AND ss.id = :id");
		snippetSelector.setParameter("id", id);
		
		Query query = snippetSelector.createQuery(em);
		
		return (SourceSnippet) query.getSingleResult();
	}
	
	public void deleteAllObjectsJPQL(Repository repository, EntityManager em) {
		SelectClassQuery classMemberDeleter = new SelectClassQuery();
		classMemberDeleter.start("DELETE FROM ClassMember cm");
		classMemberDeleter.join("LEFT JOIN cm.klass c");
		classMemberDeleter.addClassSourceJoins(repository, null);
		
		SelectClassQuery superClassDeleter = new SelectClassQuery();
		superClassDeleter.start("DELETE FROM SuperClassReference sr");
		superClassDeleter.join("LEFT JOIN ClassDeclaration c ON sr IN c.superClasses");
		superClassDeleter.addClassSourceJoins(repository, null);
		
		SelectClassQuery classDeleter = new SelectClassQuery();
		classDeleter.start("DELETE FROM ClassDeclaration c");
		classDeleter.addClassSourceJoins(repository, null);
		
		SelectSnippetQuery snippetDeleter = new SelectSnippetQuery();
		snippetDeleter.start("DELETE FROM SourceSnippet ss");
		snippetDeleter.addClassSourceJoins(repository, null);
		
		SelectSourceReferenceQuery sourceReferenceDeleter = new SelectSourceReferenceQuery();
		sourceReferenceDeleter.start("DELETE FROM SourceReference sr");
		sourceReferenceDeleter.addClassSourceJoins(repository, null);
		
		Query classMemberDeleteQuery = classMemberDeleter.createQuery(em);
		classMemberDeleteQuery.executeUpdate();
		
		Query superClassDeleteQuery = superClassDeleter.createQuery(em);
		superClassDeleteQuery.executeUpdate();
		
		Query classDeleteQuery = classDeleter.createQuery(em);
		classDeleteQuery.executeUpdate();
		
		Query snippetDeleteQuery = snippetDeleter.createQuery(em);
		snippetDeleteQuery.executeUpdate();
		
		Query sourceReferenceDeleteQuery = sourceReferenceDeleter.createQuery(em);
		sourceReferenceDeleteQuery.executeUpdate();
	}
	
	public void deleteAllObjects(Repository repository, EntityManager em) {
		Query classMemberDeleteQuery = createDeleteQuery(em, ClassMember.class, 
				repository, "code", "sourceReference", "source", "repository");
		classMemberDeleteQuery.executeUpdate();
		
		Query superClassDeleteQuery = createDeleteQuery(em, SuperClassReference.class, 
				repository, "classDeclaration", "sourceReference", "source", "repository");
		superClassDeleteQuery.executeUpdate();		
		
		Query classDeleteQuery = createDeleteQuery(em, ClassDeclaration.class, 
				repository, "sourceReference", "source", "repository");
		classDeleteQuery.executeUpdate();
		
		Query snippetDeleteQuery = createDeleteQuery(em, SourceSnippet.class, 
				repository, "sourceReference", "source", "repository");
		snippetDeleteQuery.executeUpdate();
		
		Query sourceReferenceDeleteQuery = createDeleteQuery(em, SourceReference.class, 
				repository, "source", "repository");
		sourceReferenceDeleteQuery.executeUpdate();
	}
	
	@SuppressWarnings("rawtypes")
	private <E> Query createDeleteQuery(EntityManager em, Class<E> klass, Repository repository, 
			String... attributes) {
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaDelete<E> criteria = cb.createCriteriaDelete(klass);
				
		Path path = criteria.from(klass);
		
		for(String attribute : attributes) {
			path = path.get(attribute);
		}
		
		Predicate predicate = cb.equal(path, repository);
				
		criteria.where(predicate);
		
		Query query = em.createQuery(criteria);
		log.fine("Delete source query: " + query.toString());
		
		return query;
	}
}
