package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.BinaryOperation.Type;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

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
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(UnaryOperation.class);
		plan.addEnumParam(0, "type", false, Type.class);
		plan.addNodeParam(1, "expression", false);
	}
}
