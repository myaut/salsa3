package com.tuneit.salsa3.ast;

public class WhileStatement extends ASTStatement {
	private ASTNode condition;
	
	public WhileStatement(ASTNode condition) {
		this.condition = condition;
	}
	
	public ASTNode getCondition() {
		return condition;
	}
	
	@Override
	public String toString() {
		return "While [" + condition + "]";
	}
}
