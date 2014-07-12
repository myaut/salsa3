package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class Assign extends ASTNode {
	private ASTNode left;
	private ASTNode right;
	
	public Assign(ASTNode left, ASTNode right) {
		super();
		this.left = left;
		this.right = right;
		
		left.reuseInExpression(this);
		right.reuseInExpression(this);
	}

	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}

	@Override
	public String toString() {
		return "Assign [left=" + left + ", right=" + right + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Assign.class);
		plan.addNodeParam(0, "left", false);
		plan.addNodeParam(1, "right", false);
	}
}
