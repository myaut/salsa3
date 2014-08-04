package com.tuneit.salsa3;

public class SourceException extends RuntimeException {
	private static final long serialVersionUID = 4173601973288101079L;

	public SourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SourceException(String message) {
		super(message);
	}
}
