package com.tuneit.salsa3.ast;

public class UnaryOperation extends ASTNode implements Cloneable {
	public enum Type {
		/* Bit operations */
		UOP_BIT_NOT,
		
		/* Logical operations */
		UOP_LOGICAL_NOT,
		
		/* Basic arithmetics & shifts */
		UOP_PLUS,
		UOP_MINUS,
		UOP_PRE_DECREMENT,
		UOP_POST_DECREMENT,
		UOP_PRE_INCREMENT,
		UOP_POST_INCREMENT
	};
	
	private ASTNode expr;
	
	private Type type;
	
	public UnaryOperation(Type type, ASTNode expr) {
		super();
		this.expr = expr;
		this.type = type;
		
		expr.reuseInExpression(this);
	}
	
	public ASTNode clone() {
		return new UnaryOperation(type, expr);
	}

	public ASTNode getExpression() {
		return expr;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "UnaryOperation [expr=" + expr + ", type="
				+ type + "]";
	}
}
