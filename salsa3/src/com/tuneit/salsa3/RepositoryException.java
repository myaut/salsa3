package com.tuneit.salsa3;

public class RepositoryException extends RuntimeException {
	private static final long serialVersionUID = 6302509544609294962L;

	public RepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepositoryException(String message) {
		super(message);
	}
}
