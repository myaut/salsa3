package com.tuneit.salsa3.ast;

public class Throw extends ASTNode {
	private ASTNode object;
	
	public Throw(ASTNode object) {
		super();
		this.object = object;
	}

	public ASTNode getObject() {
		return object;
	}

	@Override
	public String toString() {
		return "Throw [object=" + object + "]";
	}
}
