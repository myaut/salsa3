package com.tuneit.salsa3.ast;

public class ArrayElement extends ASTNode {
	private ASTNode array;
	private ASTNode index;
	
	public ArrayElement(ASTNode array, ASTNode index) {
		super();
		this.array = array;
		this.index = index;
	}

	public ASTNode getArray() {
		return array;
	}

	public ASTNode getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "ArrayElement [array=" + array + ", index=" + index + "]";
	}
}
