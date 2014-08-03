package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class DoWhileStatement extends ASTStatement {
	private ASTNode condition;
	
	public DoWhileStatement() {
		this.condition = null;
	}
	
	public DoWhileStatement(ASTNode condition) {
		this.condition = condition;
		
		condition.reuseInExpression(this);
	}
	
	public ASTNode getCondition() {
		return condition;
	}
	
	public void setCondition(ASTNode condition) {
		this.condition = condition;
	}
	
	@Override
	public String toString() {
		return "DoWhile [" + condition + "]";
	}
	
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(DoWhileStatement.class);
		plan.addNodeParam(0, "condition", false);
	}
}
