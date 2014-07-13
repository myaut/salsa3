package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class InstanceOf extends ASTNode {
	private ASTNode expression;
	private String className;
	
	public InstanceOf(String className, ASTNode expression) {
		super();
		this.className = className;
		this.expression = expression;
	}

	public ASTNode getExpression() {
		return expression;
	}

	public String getClassName() {
		return className;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(InstanceOf.class);
		plan.addStringParam(0, "className", false);
		plan.addNodeParam(1, "expression", false);
	}
}
