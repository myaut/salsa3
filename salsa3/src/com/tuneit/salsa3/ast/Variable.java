package com.tuneit.salsa3.ast;

public class Variable extends ASTNode {
	private String varName;
	
	public Variable(String varName) {
		this.varName = varName;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public ASTNode clone() {
		return new Variable(varName);
	}

	@Override
	public String toString() {
		return "Variable [varName=" + varName + "]";
	}
}
