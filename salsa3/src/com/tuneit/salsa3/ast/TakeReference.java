package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class TakeReference extends ASTNode {
	private ASTNode expression;
	
	public TakeReference(ASTNode expression) {
		this.expression = expression;
	}
	
	public ASTNode getExpression() {
		return expression;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(TakeReference.class);
		plan.addNodeParam(0, "expression", false);
	}
}
