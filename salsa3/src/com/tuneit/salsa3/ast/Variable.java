package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.*;

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
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Variable.class);
		plan.addStringParam(0, "varName", false);
	}	
}
