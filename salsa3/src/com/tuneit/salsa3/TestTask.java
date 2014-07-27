package com.tuneit.salsa3;

import java.util.logging.Logger;

public class TestTask extends Task {
	private static Logger log = Logger.getLogger(TestTask.class.getName());
	
	public TestTask() {
		super();
	}

	@Override
	public void run() {
		log.fine("Test task is starting!");
		
		while(!shouldStop()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.fine("Test task is interrupted!");
			}
		}
		
		log.fine("Test task is stopped!");
	}
}
