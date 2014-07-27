package com.tuneit.salsa3;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task implements Runnable {
	private static AtomicInteger taskIdSequence = new AtomicInteger(1);
	
	/*
	 * Task FSM: 
	 *                                                 handle
	 *         dispatch by                             stop in
	 *         TaskManager            stop()           taskRun()
	 * CREATED -----------> EXECUTING ------> STOPPING --------> STOPPED
	 *                         | successfully complete 
	 *                         | (called by TaskManager)
	 *                         +-------------------------------> FINISHED
	 *                         | throw an exception
	 *                         +-------------------------------> FAILED
	 */
	
	public static enum State {
		TASK_CREATED,
		TASK_EXECUTING,
		TASK_STOPPING,
		TASK_STOPPED,
		TASK_FINISHED,
		TASK_FAILED;
		
		public String toString() {
			return super.toString().substring(5);
		}
	}
	
	private int taskId;
	private String description;
	private State state;
	private Throwable throwable;
	
	public Task() {
		this.taskId = taskIdSequence.getAndIncrement();
		this.description = "Unknown task";
		this.state = State.TASK_CREATED;
		this.throwable = null;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public Throwable getThrowable() {
		return throwable;
	}
	
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
	public synchronized boolean shouldStop() {		
		return state == State.TASK_STOPPING;
	}
	
	private boolean isFinishedNoLock() {		
		return state == State.TASK_STOPPED || 
			   state == State.TASK_FINISHED || 
			   state == State.TASK_FAILED;
	}
	
	public synchronized boolean isFinished() {
		return isFinishedNoLock();
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public synchronized State getState() {
		return state;
	}
	
	private void setStateNoLock(State state) {
		if(this.state == State.TASK_CREATED && state == State.TASK_STOPPING) {
			state = State.TASK_FINISHED;
		}
		
		if(state == State.TASK_FINISHED) {	
			notifyAll();
						
			if(this.state == State.TASK_STOPPING) {
				this.state = State.TASK_STOPPED;
				return;
			}
		}
		
		this.state = state;
	}
	
	public synchronized void setState(State state) {
		setStateNoLock(state);
	}
	
	public synchronized void stop() {
		setStateNoLock(State.TASK_STOPPING);
	}
	
	public synchronized boolean stopAndWait() {
		if(isFinishedNoLock()) {
			/* Task is already stopped */
			return true;
		}
				
		setStateNoLock(State.TASK_STOPPING);
		
		try {
			wait();
		} catch (InterruptedException e) {
			return false;
		}		
		
		return true;
	}
}
