package com.tuneit.salsa3.ast;

public class ArrayIndex extends ASTNode {
	private ASTNode array;
	private ASTNode index;
	
	public ArrayIndex(ASTNode array, ASTNode index) {
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
	
	public ASTNode clone() {
		return new ArrayIndex(array, index);
	}

	@Override
	public String toString() {
		return "ArrayIndex [array=" + array + ", index=" + index + "]";
	}
}
