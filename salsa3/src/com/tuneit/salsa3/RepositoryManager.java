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

public final class RepositoryManager {
	private static final String PERSISTENCE_UNIT_NAME = "salsaPU";
	private static RepositoryManager _instance = null;
	
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
		Repository repo = getRepositoryByName(repoName);
		
		em.getTransaction().begin();
		
		for(Source source : repo.getSources()) {
			/* FIXME: delete associated code, etc. */
			em.remove(source);
		}
		
		em.remove(repo);
		em.getTransaction().commit();
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
}
