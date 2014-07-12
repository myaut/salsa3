package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class IncludeStatement extends ASTNode {
	private ASTNode includePath;	
	private boolean isOnce;
	
	public IncludeStatement(ASTNode includePath, Boolean isOnce) {
		super();
		this.includePath = includePath;
		this.isOnce = isOnce;
	}

	public ASTNode getIncludePath() {
		return includePath;
	}

	public boolean isOnce() {
		return isOnce;
	}
	
	public boolean getIsOnce() {
		return isOnce;
	}

	@Override
	public String toString() {
		return "IncludeStatement [includePath=" + includePath + ", isOnce="
				+ isOnce + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(IncludeStatement.class);
		plan.addNodeParam(0, "includePath", false);
		plan.addBooleanParam(1, "isOnce", false);
	}
}
