package com.tuneit.salsa3.ast;

public class Constant extends ASTNode {
	private String constName;
	
	public Constant(String constName) {
		this.constName = constName;
	}
	
	public String getConstName() {
		return constName;
	}
	
	public ASTNode clone() {
		return new Constant(constName);
	}

	@Override
	public String toString() {
		return "Constant [constName=" + constName + "]";
	}
}
