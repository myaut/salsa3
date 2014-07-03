package com.tuneit.salsa3.ast;

public class AssignWithBinaryOperation extends Assign {
	private BinaryOperation.Type type;
	
	public AssignWithBinaryOperation(BinaryOperation.Type type, ASTNode left, ASTNode right) {
		super(left, right);
		this.type = type;
	}

	public BinaryOperation.Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "AssignWithBinaryOperation [type=" + type + 
					 					", left=" + getLeft() + 
					 					", right=" + getRight() + "]";
	}
}
