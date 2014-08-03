package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class Throw extends ASTNode {
	private ASTNode object;
	
	public Throw(ASTNode object) {
		super();
		this.object = object;
		
		object.reuseInExpression(this);
	}

	public ASTNode getObject() {
		return object;
	}

	@Override
	public String toString() {
		return "Throw [object=" + object + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Throw.class);
		plan.addNodeParam(0, "object", false);
	}
}
