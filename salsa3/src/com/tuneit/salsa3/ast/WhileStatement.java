package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class WhileStatement extends ASTStatement {
	private ASTNode condition;
	
	public WhileStatement(ASTNode condition) {
		this.condition = condition;
		
		condition.reuseInExpression(this);
	}
	
	public ASTNode getCondition() {
		return condition;
	}
	
	@Override
	public String toString() {
		return "While [" + condition + "]";
	}
	
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(WhileStatement.class);
		plan.addNodeParam(0, "condition", false);
	}
}
