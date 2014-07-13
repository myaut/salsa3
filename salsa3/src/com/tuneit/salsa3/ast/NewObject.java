package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class NewObject extends ASTNode {
	private String className;
	private ASTNode constructorCall;
	
	public NewObject(String className, ASTNode constructorCall) {
		super();
		this.className = className;
		this.constructorCall = constructorCall;
	}

	public String getClassName() {
		return className;
	}

	public ASTNode getConstructorCall() {
		return constructorCall;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(NewObject.class);
		plan.addStringParam(0, "className", false);
		plan.addNodeParam(1, "constructorCall", false);
	}
}
