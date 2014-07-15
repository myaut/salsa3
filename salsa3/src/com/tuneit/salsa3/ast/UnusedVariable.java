package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class UnusedVariable extends Variable {
	public UnusedVariable() {
		super("_");
	}
	
	public ASTNode clone() {
		return new UnusedVariable();
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(UnusedVariable.class);
	}
}
