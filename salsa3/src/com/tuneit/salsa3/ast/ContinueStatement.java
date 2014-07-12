package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class ContinueStatement extends ASTNode {
	private String label;
	private int continueNesting;
	
	public ContinueStatement() {
		this.label = null;
		this.continueNesting = 1;
	}
	
	public ContinueStatement(int continueNesting) {
		this.label = null;
		this.continueNesting = continueNesting;
	}
	
	public ContinueStatement(String label) {
		this.label = label;
		this.continueNesting = 1;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public int getContinueNesting() {
		return this.continueNesting;
	}
	
	@Override
	public String toString() {
		if(this.label != null) {
			return "Continue [n=" + this.continueNesting + 
							", label=" + this.label + "]";
		}
		
		return "Continue [n=" + this.continueNesting + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ContinueStatement.class);
		plan.addIntegerParam(0, "continueNesting", true).setDefaultValue(1);
		plan.addStringParam(0, "label", true);
	}
}
