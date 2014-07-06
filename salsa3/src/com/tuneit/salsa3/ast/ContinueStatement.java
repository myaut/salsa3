package com.tuneit.salsa3.ast;

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
	
	public int getNesting() {
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
}
