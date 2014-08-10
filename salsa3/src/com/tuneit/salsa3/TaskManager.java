package com.tuneit.salsa3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TaskManager {
	private static class ExceptionCatchingThreadFactory implements ThreadFactory {
	    private final ThreadFactory delegate;

	    private ExceptionCatchingThreadFactory(ThreadFactory delegate) {
	        this.delegate = delegate;
	    }

	    public Thread newThread(final Runnable r) {
	        Thread t = delegate.newThread(r);
	        t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	            @Override
	            public void uncaughtException(Thread t, Throwable e) {
	                // Ignore
	            }
	        });
	        return t;
	    }
	}
	
	private static class TaskExecutor extends ThreadPoolExecutor {
		private static Logger log = Logger.getLogger(TaskExecutor.class.getName());		
		
		private ArrayList<Task> tasks;
		
		public TaskExecutor(int corePoolSize, int maximumPoolSize,
				long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
						
			this.tasks = new ArrayList<Task>();
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			Task task = (Task) r;
					
			if(t != null) {
				StringWriter sw = new StringWriter();				
				t.printStackTrace(new PrintWriter(sw));
				
				task.setState(Task.State.TASK_FAILED);
				task.setThrowable(t);
				
				log.severe("Task #" + task.getTaskId() + " is failed");
				log.severe(sw.toString());
			}
			else {
				log.info("Task #" + task.getTaskId() + " is finished");
				task.setState(Task.State.TASK_FINISHED);				
			}
			
			super.afterExecute(r, t);
		}

		@Override
		protected void beforeExecute(Thread t, Runnable r) {			
			Task task = (Task) r;
			task.setState(Task.State.TASK_EXECUTING);

			log.info("Task #" + task.getTaskId() + " is started");
			
			super.beforeExecute(t, r);
		}
		
		public void addTask(Task task) {
			log.info("Task #" + task.getTaskId() + " " + 
					 task.getDescription() + " have been queued");
			
			synchronized(tasks) {
				tasks.add(task);
			}
			
			execute(task);
		}
		
		@SuppressWarnings("unchecked")
		public List<Task> getTasks() {
			/* tasks may be altered when  */
			synchronized(tasks) {
				return (List<Task>) tasks.clone();
			}
		}
	}
	
	private class TaskShutdownThread extends Thread {
		private TaskExecutor executor;
		
		public TaskShutdownThread(TaskExecutor executor) {
			super();
			
			setName("TaskShutdownThread");
			
			this.executor = executor;
		}
		
		public void run() {
			for(Task task : executor.getTasks()) {
				/* First of all, remove queued tasks */
				if(task.getState() == Task.State.TASK_CREATED) {
					task.stop();
					executor.remove(task);
				}
			}
			
			for(Task task : executor.getTasks()) {
				/* TODO: handle if wait is interrupted? */
				task.stopAndWait();				
			}
		}
	}
		
	private BlockingQueue<Runnable> queue;
	private TaskExecutor executor;
	
	private static TaskManager _instance = null;
	private static int CORE_POOL_SIZE = 2;
	private static int MAXIMUM_POOL_SIZE = 4;
	private static long KEEP_ALIVE_TIME = 100;
	private static TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
	
	static {
		int numberOfCores = Runtime.getRuntime().availableProcessors();
		
		CORE_POOL_SIZE = numberOfCores;
		MAXIMUM_POOL_SIZE = numberOfCores * 2;
	}
	
	public TaskManager() {
		this.queue = new LinkedBlockingQueue<Runnable>();
		this.executor = new TaskExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, 
										 TIME_UNIT, queue);
		
		this.executor.setThreadFactory(
				new ExceptionCatchingThreadFactory(this.executor.getThreadFactory()));
		
		Runtime.getRuntime().addShutdownHook(new TaskShutdownThread(this.executor));
	}
	
	public void addTask(Task task) {
		executor.addTask(task);
	}
	
	public void stopTask(int id) {
		Task task = findTask(id);
		
		task.stop();
		
		executor.remove(task);
	}
	
	public List<Task> getTasks() {
		return executor.getTasks();
	}
	
	public Task findTask(int id) {
		List<Task> tasks = executor.getTasks();
		
		for(Task task : tasks) {
			if(task.getTaskId() == id) {
				return task;
			}
		}
		
		throw new IndexOutOfBoundsException("Not found task #" + id);
	}
	
	public static TaskManager getInstance() {
		if(_instance == null) {
			_instance = new TaskManager();
		}
		
		return _instance;
	}
	
	public static int getCorePoolSize() {
		if(_instance != null) {
			return _instance.executor.getCorePoolSize();
		}
		
		return CORE_POOL_SIZE;
	}
	
	public static void setCorePoolSize(int size) {
		CORE_POOL_SIZE = size;
		
		if(_instance != null) {
			_instance.executor.setCorePoolSize(size);
		}
	}
	
	public static int getMaximumPoolSize() {
		if(_instance != null) {
			return _instance.executor.getMaximumPoolSize();
		}
		
		return MAXIMUM_POOL_SIZE;
	}
	
	public static void setMaximumPoolSize(int size) {
		MAXIMUM_POOL_SIZE = size;
		
		if(_instance != null) {
			_instance.executor.setMaximumPoolSize(size);
		}
	}
	
	public static long getKeepAliveTime() {
		if(_instance != null) {
			return _instance.executor.getKeepAliveTime(TIME_UNIT);
		}
		
		return KEEP_ALIVE_TIME;
	}
	
	public static void setKeepAliveTime(long time) {
		KEEP_ALIVE_TIME = time;
		
		if(_instance != null) {
			_instance.executor.setKeepAliveTime(time, TIME_UNIT);
		}
	}
}
