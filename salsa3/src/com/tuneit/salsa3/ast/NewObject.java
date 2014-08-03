package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class NewObject extends ASTNode {
	private ASTNode className;
	private ASTNode constructorCall;
	
	public NewObject(ASTNode className, ASTNode constructorCall) {
		super();
		this.className = className;
		this.constructorCall = constructorCall;
		
		className.reuseInExpression(this);
		constructorCall.reuseInExpression(this);
	}

	public ASTNode getClassName() {
		return className;
	}

	public ASTNode getConstructorCall() {
		return constructorCall;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(NewObject.class);
		plan.addNodeParam(0, "className", false);
		plan.addNodeParam(1, "constructorCall", false);
	}
}
