package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class ReturnStatement extends ASTNode {
	private ASTNode returnExpression;
	
	public ReturnStatement() {
		super();
		this.returnExpression = null;
	}
	
	public ReturnStatement(ASTNode returnExpression) {
		super();
		this.returnExpression = returnExpression;
	}
	
	public ASTNode getReturnExpression() {
		return returnExpression;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ReturnStatement.class);
		plan.addNodeParam(0, "returnExpression", true);
	}
}
