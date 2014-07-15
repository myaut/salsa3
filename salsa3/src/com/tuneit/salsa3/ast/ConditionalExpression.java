package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class ConditionalExpression extends ASTNode {	
	private ASTNode condition;
	private ASTNode expressionTrue;
	private ASTNode expressionFalse;
	
	public ConditionalExpression(ASTNode condition, ASTNode expressionTrue,
			ASTNode expressionFalse) {
		super();
		this.condition = condition;
		this.expressionTrue = expressionTrue;
		this.expressionFalse = expressionFalse;
	}
	
	public ConditionalExpression(ASTNode condition) {
		super();
		this.condition = condition;
		this.expressionTrue = null;
		this.expressionFalse = null;
	}

	public ASTNode getExpressionTrue() {
		return expressionTrue;
	}

	public void setExpressionTrue(ASTNode expressionTrue) {
		this.expressionTrue = expressionTrue;
	}

	public ASTNode getExpressionFalse() {
		return expressionFalse;
	}

	public void setExpressionFalse(ASTNode expressionFalse) {
		this.expressionFalse = expressionFalse;
	}

	public ASTNode getCondition() {
		return condition;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ConditionalExpression.class);
		plan.addNodeParam(0, "condition", false);
		plan.addNodeParam(1, "expressionTrue", false);
		plan.addNodeParam(2, "expressionFalse", false);
	}
}
