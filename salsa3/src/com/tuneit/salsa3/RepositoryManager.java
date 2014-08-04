package com.tuneit.salsa3;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.tuneit.salsa3.model.Source;
import com.tuneit.salsa3.model.Repository;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

/**
 * <strong>Repository manager</strong> - main class for managing source repositories - 
 * provides various operations with repositories and sources that are performed on 
 * database layer.
 * 
 * It is singleton, so it should be created via <tt>RepositoryManager.getInstance()</tt>
 * 
 * RepositoryManager calls that are doing modification are thread safe (due to limitations
 * of transactional mechanism came from JPA).
 * 
 * @author Sergey Klyaus [Sergey.Klyaus@Tune-IT.Ru]
 */
public final class RepositoryManager {
	private static final String PERSISTENCE_UNIT_NAME = "salsaPU";
	private static RepositoryManager _instance = null;
	
	/* FIXME: This architecture doesn't support parallel parsing of multiple repos.
	 * Have to replace transaction -> entity managers here */
	private static class SourceWalkTransaction {
		private AtomicInteger referenceCount;
		private EntityTransaction transaction;
		private boolean doRollback;
		
		public SourceWalkTransaction(EntityTransaction transaction) {
			this.referenceCount = new AtomicInteger(0);
			this.transaction = transaction;
			this.doRollback = false;
		}
		
		public void begin() {
			if(referenceCount.incrementAndGet() == 1) {
				transaction.begin();
			}
		}
		
		public boolean finish(boolean doRollback) {
			synchronized(this) {
				if(doRollback) 
					this.doRollback = doRollback;
			}
			
			if(referenceCount.decrementAndGet() == 0) {
				if(this.doRollback) {
					transaction.rollback();
				}
				else {
					transaction.commit();
				}
				return true;
			}
			
			return false;
		}
	}
	
	private EntityManager em;
	private HashMap<String, SourceWalkTransaction> repoTransactions;
		
	private RepositoryManager() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		
		em = emf.createEntityManager();
		repoTransactions = new HashMap<String, SourceWalkTransaction>();
	}
	
	/**
	 * @return Instance of Repository Manager 
	 */
	public static RepositoryManager getInstance() {
		if(_instance == null) {
			_instance = new RepositoryManager();
		}
		
		return _instance;
	}
	
	/**
	 * Creates new repository
	 * 
	 * @param repoName Name of repository (should be unique across all repositories)
	 * @param path Absolute path to a repository sources
	 * @param language Programming language of repository
	 */
	public void createRepository(String repoName, String path, Repository.Language language) {
		Repository repo = new Repository(repoName, path, language);
		
		em.getTransaction().begin();
		em.persist(repo);
		em.getTransaction().commit();
	}
	
	/**
	 * Deletes repository including sources and all sources objects, i.e. class
	 * declarations
	 * 
	 * @throws RepositoryException if no such repository was found
	 * 
	 * @param repoName name of repository to be deleted
	 */
	public void deleteRepository(String repoName) {	
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		Repository repository = getRepositoryByName(repoName);
		SourceManager sm = SourceManager.getInstance();
		
		CriteriaDelete<Source> deleteSourcesCriteria = cb.createCriteriaDelete(Source.class);
		Root<Source> root = deleteSourcesCriteria.from(Source.class);
		deleteSourcesCriteria.where(cb.equal(root.get("repository"), repository));
		Query deleteSourcesQuery = em.createQuery(deleteSourcesCriteria);
		
		em.getTransaction().begin();
		
		try {
			sm.deleteAllObjects(repository, null, em);
			deleteSourcesQuery.executeUpdate();
			em.remove(repository);
		}
		catch(Exception e) {
			em.getTransaction().rollback();
			throw e;
		}
		
		em.getTransaction().commit();
	}
	
	/**
	 * Get list of sources that are belong to repository
	 * 
	 * @param repository repository
	 * @return List of sources
	 */
	@SuppressWarnings("unchecked")
	public List<Source> getSources(Repository repository) {
		Query query = em.createQuery("SELECT s FROM Source s WHERE s.repository = :repository");
		query.setParameter("repository", repository);
		
		return (List<Source>) query.getResultList();
	}
	
	/**
	 * Get single source from a repository by it`s id
	 *  
	 * @throws RepositoryException if source was not found
	 *  
	 * @param repository
	 * @param id
	 * @return Source if was found
	 */
	public Source getSourceById(Repository repository, Integer id) {
		Query query = em.createQuery("SELECT s FROM Source s WHERE s.repository = :repository AND s.id = :id");
		query.setParameter("repository", repository);
		query.setParameter("id", id);
		
		try {
			return (Source) query.getSingleResult();
		}
		catch(NoResultException nre) {
			throw new RepositoryException("Source #" + id + " was not found!", nre);
		}
	}
	
	/**
	 * Get single source from a repository by it`s relative path
	 *  
	 * @throws RepositoryException if source was not found
	 *  
	 * @param repository
	 * @param id
	 * @return Source if was found
	 */
	public Source getSourceByPath(Repository repository, String path) {
		Query query = em.createQuery("SELECT s FROM Source s WHERE s.repository = :repository AND s.path = :path");
		query.setParameter("repository", repository);
		query.setParameter("path", path);
		
		try {
			return (Source) query.getSingleResult();	
		}
		catch(NoResultException nre) {
			throw new RepositoryException("Source '" + path + "' was not found!", nre);
		}
	}
	
	/**
	 * @return List of all repositories
	 */
	@SuppressWarnings("unchecked")
	public List<Repository> getRepositories() {
		Query query = em.createQuery("SELECT r FROM Repository r");
		return (List<Repository>) query.getResultList();
	}
	
	/**
	 * Get single source from a repository by it`s name
	 *  
	 * @throws RepositoryException if no such repository was found
	 * 
	 * @param repoName name of repository
	 * @return Repository if was found
	 */
	public Repository getRepositoryByName(String repoName) {
		Query query = em.createQuery("SELECT r FROM Repository r WHERE r.repositoryName = :repositoryName");
		query.setParameter("repositoryName", repoName);
		
		try {
			return (Repository) query.getSingleResult();
		}
		catch(NoResultException nre) {
			throw new RepositoryException("Repository '" + repoName + "' was not found!", nre);
		}
	}
	
	/**
	 * Starts transaction which would be used while repository is walked by RepositoryWalkTask
	 * May be called multiple times by several threads of RepositoryWalkTask
	 * 
	 * @param repository
	 */
	public synchronized void beginSourcesWalk(Repository repository) {
		SourceWalkTransaction transaction = repoTransactions.get(repository.getRepositoryName());
		
		if(transaction == null) {
			EntityTransaction emTransaction = em.getTransaction();
			transaction = new SourceWalkTransaction(emTransaction);
			
			repoTransactions.put(repository.getRepositoryName(), transaction);
		}
		
		transaction.begin();
	}
	
	/**
	 * Finishes transaction which would be used while repository is walked by RepositoryWalkTask
	 * May be called multiple times, the latter would commit/rollback DB transaction.
	 * Latter call is determined using reference counting
	 * 
	 * @throws IllegalStateException if beginSourcesWalk wasn't called yet
	 * 
	 * @param repository
	 * @param doRollback set this parameter to true, if transaction should be rolled back
	 */
	public synchronized void finishSourcesWalk(Repository repository, boolean doRollback) {
		SourceWalkTransaction transaction = repoTransactions.get(repository.getRepositoryName());
		
		if(transaction == null) {
			throw new IllegalStateException("Repository transaction was not created");
		}
						
		if(transaction.finish(doRollback)) {
			repoTransactions.remove(repository.getRepositoryName());
		}
	}
	
	/**
	 * Add source file to a repository -
	 * 
	 * @param repository repository
	 * @param path relative source's path
	 */
	public synchronized void addSourceFile(Repository repository, String path) {
		if(!repoTransactions.containsKey(repository.getRepositoryName())) {
			throw new IllegalStateException("Repository transaction was not created");
		}
		
		/* TODO: Update if possible */
		
		Source source = new Source(repository, path); 
		em.persist(source);
	}
	
	/**
	 * Update source state after it was parsed
	 * 
	 * @param source source
	 * @param isParsed set to true if source was successfully parsed
	 * @param result set to null if source was successfully parsed or to exception that was thrown 
	 * 		  by parser layer
	 */
	public synchronized void onSourceParsed(Source source, boolean isParsed, Throwable result) {
		em.getTransaction().begin();
		
		try {
			source.setParsed(isParsed);
			if(result != null) {
				source.setParseResult(result.toString());
			}
			
			em.getTransaction().commit();
		}
		catch(Throwable t) {
			em.getTransaction().rollback();
			throw t;
		}
	}
	
	/**
	 * Reset source to "not parsed" and delete related source objects
	 * 
	 * @param source
	 */
	public synchronized void resetSource(Source source) {
		SourceManager sm = SourceManager.getInstance();
		
		em.getTransaction().begin();
		
		try {
			sm.deleteAllObjects(source.getRepository(), source, em);
			
			source.setParsed(false);
			source.setParseResult(null);
		
			em.getTransaction().commit();
		}
		catch(Throwable t) {
			em.getTransaction().rollback();
			throw t;
		}
	}
}
