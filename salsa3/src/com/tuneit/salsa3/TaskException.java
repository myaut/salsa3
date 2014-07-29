package com.tuneit.salsa3;

public class TaskException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4239657325892503708L;

	public TaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskException(Throwable cause) {
		super(cause);
	}
}
