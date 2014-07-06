package com.tuneit.salsa3.ast;

public class DoWhileStatement extends ASTStatement {
	private ASTNode condition;
	
	public DoWhileStatement() {
		this.condition = null;
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
}
