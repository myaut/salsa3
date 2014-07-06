package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class BreakStatement extends ASTNode {
	private String label;
	private int breakNesting;
	
	public BreakStatement() {
		this.label = null;
		this.breakNesting = 1;
	}
	
	public BreakStatement(Integer breakNesting) {
		this.label = null;
		this.breakNesting = breakNesting;
	}
	
	public BreakStatement(String label) {
		this.label = label;
		this.breakNesting = 1;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public int getBreakNesting() {
		return this.breakNesting;
	}
	
	@Override
	public String toString() {
		if(this.label != null) {
			return "Break [n=" + this.breakNesting + 
						", label=" + this.label + "]";
		}
		
		return "Break [n=" + this.breakNesting + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(BreakStatement.class);
		plan.addIntegerParam(0, "breakNesting", true).setDefaultValue(1);
		plan.addStringParam(0, "label", true);
	}
}
