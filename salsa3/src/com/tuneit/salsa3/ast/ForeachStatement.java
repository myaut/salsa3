package com.tuneit.salsa3.ast;

public class ForeachStatement extends ASTStatement {
	private ASTNode iterable;
	private ASTNode key;
	private ASTNode value;
	
	public ForeachStatement(ASTNode iterable) {
		this.iterable = iterable;
		this.key = null;
		this.value = null;
	}

	public ASTNode getKey() {
		return key;
	}

	public void setKey(ASTNode key) {
		this.key = key;
	}

	public ASTNode getValue() {
		return value;
	}

	public void setValue(ASTNode value) {
		this.value = value;
	}

	public ASTNode getIterable() {
		return iterable;
	}

	@Override
	public String toString() {
		return "Foreach [iterable=" + iterable + ", key=" + key
				+ ", value=" + value + "]";
	}
	
	
}

