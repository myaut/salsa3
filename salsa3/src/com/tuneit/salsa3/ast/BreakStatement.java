package com.tuneit.salsa3.ast;

public class BreakStatement extends ASTNode {
	private String label;
	private int breakNesting;
	
	public BreakStatement() {
		this.label = null;
		this.breakNesting = 1;
	}
	
	public BreakStatement(int breakNesting) {
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
	
	public int getNesting() {
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
}
