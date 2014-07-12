package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.BinaryOperation.Type;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class AssignWithBinaryOperation extends Assign {
	private BinaryOperation.Type type;
	
	public AssignWithBinaryOperation(BinaryOperation.Type type, ASTNode left, ASTNode right) {
		super(left, right);
		this.type = type;
	}

	public BinaryOperation.Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "AssignWithBinaryOperation [type=" + type + 
					 					", left=" + getLeft() + 
					 					", right=" + getRight() + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(AssignWithBinaryOperation.class);
		plan.addEnumParam(0, "type", false, Type.class);
		plan.addNodeParam(1, "left", false);
		plan.addNodeParam(2, "right", false);
	}
}
