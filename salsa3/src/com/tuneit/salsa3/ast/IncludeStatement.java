package com.tuneit.salsa3.ast;

public class IncludeStatement extends ASTNode {
	private ASTNode includePath;	
	private boolean isOnce;
	
	public IncludeStatement(ASTNode includePath, boolean isOnce) {
		super();
		this.includePath = includePath;
		this.isOnce = isOnce;
	}

	public ASTNode getIncludePath() {
		return includePath;
	}

	public boolean isOnce() {
		return isOnce;
	}

	@Override
	public String toString() {
		return "IncludeStatement [includePath=" + includePath + ", isOnce="
				+ isOnce + "]";
	}
}
