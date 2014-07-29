package com.tuneit.salsa3;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.tuneit.salsa3.model.Source;
import com.tuneit.salsa3.model.Repository;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

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
		
	public RepositoryManager() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		
		em = emf.createEntityManager();
		repoTransactions = new HashMap<String, SourceWalkTransaction>();
	}
	
	public static RepositoryManager getInstance() {
		if(_instance == null) {
			_instance = new RepositoryManager();
		}
		
		return _instance;
	}
	
	public void createRepository(String repoName, String path, Repository.Language language) {
		Repository repo = new Repository(repoName, path, language);
		
		em.getTransaction().begin();
		em.persist(repo);
		em.getTransaction().commit();
	}
	
	public void deleteRepository(String repoName) {		
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		Repository repo = getRepositoryByName(repoName);
		
		CriteriaDelete<Source> deleteSourcesCriteria = cb.createCriteriaDelete(Source.class);
		Root<Source> e = deleteSourcesCriteria.from(Source.class);
		deleteSourcesCriteria.where(cb.equal(e.get("repository"), repo));
		Query deleteSourcesQuery = em.createQuery(deleteSourcesCriteria);
		
		em.getTransaction().begin();
		
		deleteSourcesQuery.executeUpdate();
		
		em.remove(repo);
		em.getTransaction().commit();
	}
	
	@SuppressWarnings("unchecked")
	public List<Source> getSources(Repository repository) {
		Query query = em.createQuery("SELECT s FROM Source s WHERE s.repository = :repository");
		query.setParameter("repository", repository);
		
		return (List<Source>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public Source getSourceById(Repository repository, Integer id) {
		Query query = em.createQuery("SELECT s FROM Source s WHERE s.repository = :repository AND s.id = :id");
		query.setParameter("repository", repository);
		query.setParameter("id", id);
		
		return (Source) query.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public Source getSourceByPath(Repository repository, String path) {
		Query query = em.createQuery("SELECT s FROM Source s WHERE s.repository = :repository AND s.path = :path");
		query.setParameter("repository", repository);
		query.setParameter("path", path);
		
		return (Source) query.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Repository> getRepositories() {
		Query query = em.createQuery("SELECT r FROM Repository r");
		return (List<Repository>) query.getResultList();
	}
	
	public Repository getRepositoryByName(String repoName) {
		Query query = em.createQuery("SELECT r FROM Repository r WHERE r.repositoryName = :repositoryName");
		query.setParameter("repositoryName", repoName);
		
		return (Repository) query.getSingleResult();
	}
	
	public synchronized void beginSourcesWalk(Repository repository) {
		SourceWalkTransaction transaction = repoTransactions.get(repository.getRepositoryName());
		
		if(transaction == null) {
			EntityTransaction emTransaction = em.getTransaction();
			transaction = new SourceWalkTransaction(emTransaction);
			
			repoTransactions.put(repository.getRepositoryName(), transaction);
		}
		
		transaction.begin();
	}
	
	public synchronized void finishSourcesWalk(Repository repository, boolean doRollback) {
		SourceWalkTransaction transaction = repoTransactions.get(repository.getRepositoryName());
		
		if(transaction == null) {
			throw new IllegalStateException("Repository transaction was not created");
		}
						
		if(transaction.finish(doRollback)) {
			repoTransactions.remove(repository.getRepositoryName());
		}
	}
	
	public synchronized void addSourceFile(Repository repository, String path) {
		if(!repoTransactions.containsKey(repository.getRepositoryName())) {
			throw new IllegalStateException("Repository transaction was not created");
		}
		
		/* TODO: Update if possible */
		
		Source source = new Source(repository, path); 
		em.persist(source);
	}
	
	public synchronized void onSourceParsed(Source source, boolean isParsed, Throwable result) {
		em.getTransaction().begin();
		
		source.setParsed(isParsed);
		if(result != null) {
			source.setParseResult(result.toString());
		}
		
		em.getTransaction().commit();
	}
}
