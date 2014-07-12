package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

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
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(FunctionName.class);
		plan.addStringParam(0, "functionName", false);
	}
}
