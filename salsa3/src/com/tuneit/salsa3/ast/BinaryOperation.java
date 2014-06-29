package com.tuneit.salsa3.ast;

public class BinaryOperation extends ASTNode implements Cloneable {
	public enum Type {
		/* Bit operations */
		BOP_BIT_AND,
		BOP_BIT_OR,
		BOP_BIT_XOR,
		
		/* Logical operations */
		BOP_LOGICAL_AND,
		BOP_LOGICAL_OR,
		BOP_LOGICAL_XOR,
		
		/* Basic arithmetics & shifts */
		BOP_ADD,
		BOP_SUB,
		BOP_MULTIPLY,
		BOP_DIVIDE,
		BOP_MODULO,
		BOP_POW,
		
		BOP_SHIFT_LEFT,
		BOP_SHIFT_RIGHT,
		
		/* Comparison operations */
		BOP_EQUALS,
		BOP_NOT_EQUALS,
		BOP_LESS,
		BOP_MORE,
		BOP_EQUALS_OR_LESS,
		BOP_EQUALS_OR_MORE
	};
	
	private ASTNode left;
	private ASTNode right;
	
	private Type type;
	
	public BinaryOperation(Type type, ASTNode left, ASTNode right) {
		super();
		this.left = left;
		this.right = right;
		this.type = type;
	}
	
	public ASTNode clone() {
		return new BinaryOperation(type, left, right);
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "BinaryOperation [left=" + left + ", right=" + right + ", type="
				+ type + "]";
	}
}
