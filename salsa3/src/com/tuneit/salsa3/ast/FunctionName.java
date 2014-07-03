package com.tuneit.salsa3.ast;

public class FunctionName extends ASTNode {
	private String functionName;
	
	public FunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	@Override 
	public String toString() {
		return functionName;
	}
}
