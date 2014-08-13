package com.tuneit.salsa3;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;

import com.tuneit.salsa3.model.*;

import static com.tuneit.salsa3.ast.ClassDeclaration.*;

/**
 * <strong>Source manager </strong> - main class for managing source objects 
 * They are stored by <tt>SourcePostProcessor</tt>, then may be fetched using SourceManager class
 * Singleton - use <tt>SourceManager.getInstance()</tt> to get an instance
 * 
 * This class is non-safe in multithreading
 * 
 * @author Sergey Klyaus [Sergey.Klyaus@Tune-IT.Ru]
 *
 */
public class SourceManager {
	private static final String PERSISTENCE_UNIT_NAME = "salsaPU";
	private static SourceManager _instance = null;
	
	private static Logger log = Logger.getLogger(SourceManager.class.getName());
	
	private EntityManager em;
		
	private SourceManager() {
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
	
	/**
	 * Find all class declarations related to a repository or a source with various criteria
	 * 
	 * @throws NullPointerException if both repository and source are null
	 * 
	 * @param repository Repository to be searched
	 * @param source Particular source from repository. If set to null, all sources from a repository will be searched
	 * @param classNameMask Mask for class name. May contain asterisks (*) for class name globbing
	 * @param superClassName Precise same of superclass
	 * @param classTypeFilter Array of class types, i.e. for filtering interfaces
	 * 
	 * @return List of found class declarations
	 */
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
	
	/**
	 * Finds a class
	 * 
	 * @throws SourceException if class was not found 
	 * 
	 * @param repository
	 * @param source
	 * @param name Precise name of classes
	 * @return
	 */
	public ClassDeclaration getClassByName(Repository repository, Source source, String name) {
		SelectClassQuery classSelector = new SelectClassQuery();	
		classSelector.start();
		
		classSelector.addClassSourceJoins(repository, source);
		
		classSelector.where("AND c.className = :className");
		classSelector.setParameter("className", name);
		
		Query query = classSelector.createQuery(em);		
		
		try {
			return (ClassDeclaration) query.getSingleResult();
		}
		catch(NoResultException nre) {
			throw new SourceException("Class '" + name + "' was not found!", nre);
		}
	}
	
	/**
	 * @param klass
	 * @return List of class members that are belong to class
	 */
	@SuppressWarnings("unchecked")
	public List<ClassMember> getClassMembers(ClassDeclaration klass) {
		Query query = em.createQuery("SELECT cm FROM ClassMember cm WHERE cm.klass = :klass");
		query.setParameter("klass", klass);
		
		return (List<ClassMember>) query.getResultList();
	}
	
	/**
	 * Finds subclasses of a class. Order of repository parsing is undefined, 
	 * subclasses may be added earlier than super classes, so they are not linked at the moment of
	 * parsing. Because of that, we couln't use <tt>ClassDeclaration</tt> as a criteria.
	 * 
	 * @param repository
	 * @param source 
	 * @param name
	 * @return
	 */
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
	
	/**
	 * Finds all source snippets that belong to repository or single source in it
	 * 
	 * @param repository
	 * @param source
	 * @param showClassMembers set to true if class member snippets should also be selected
	 * @param nameMask Mask for snippet name (i.e. variable, function name). Accepts asterisks (*) for globbing
	 * @param snippetTypeFilter Filter of snippet type (i.e. show only functions)
	 * @return
	 */
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
	
	/**
	 * Deletes all source objects. Do not call this function directly - use <tt>RepositoryManager</tt> instead. 
	 * 
	 * @param repository
	 * @param source
	 * @param em
	 * 
	 * @see RepositoryManager
	 */
	public void deleteAllObjects(Repository repository, Source source, EntityManager em) {
		String lastPathString = null;
		Object criteriaObject = null;
		
		if(source == null) {
			criteriaObject = repository;
			lastPathString = "repository";
		}
		else {
			criteriaObject = source;
		}
		
		Query classMemberDeleteQuery = createDeleteQuery(em, ClassMember.class, 
				criteriaObject, "code", "sourceReference", "source", lastPathString);
		classMemberDeleteQuery.executeUpdate();
		
		Query superClassDeleteQuery = createDeleteQuery(em, SuperClassReference.class, 
				criteriaObject, "classDeclaration", "sourceReference", "source", lastPathString);
		superClassDeleteQuery.executeUpdate();		
		
		Query classDeleteQuery = createDeleteQuery(em, ClassDeclaration.class, 
				criteriaObject, "sourceReference", "source", lastPathString);
		classDeleteQuery.executeUpdate();
		
		Query snippetDeleteQuery = createDeleteQuery(em, SourceSnippet.class, 
				criteriaObject, "sourceReference", "source", lastPathString);
		snippetDeleteQuery.executeUpdate();
		
		Query sourceReferenceDeleteQuery = createDeleteQuery(em, SourceReference.class, 
				criteriaObject, "source", lastPathString);
		sourceReferenceDeleteQuery.executeUpdate();
	}
	
	@SuppressWarnings("rawtypes")
	private <E> Query createDeleteQuery(EntityManager em, Class<E> klass, Object criteriaObject, 
			String... attributes) {
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaDelete<E> criteria = cb.createCriteriaDelete(klass);
				
		Path path = criteria.from(klass);
		
		for(String attribute : attributes) {
			if(attribute == null) {
				continue;
			}
			
			path = path.get(attribute);
		}
		
		Predicate predicate = cb.equal(path, criteriaObject);				
		criteria.where(predicate);
		
		Query query = em.createQuery(criteria);
		log.fine("Delete source query: " + query.toString());
		
		return query;
	}
}
