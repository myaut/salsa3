package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class DynamicInstanceMember extends ASTNode {
	private ASTNode instance;
	private ASTNode memberExpression;
	
	public DynamicInstanceMember(ASTNode instance, ASTNode memberExpression) {
		super();
		this.instance = instance;
		this.memberExpression = memberExpression;
	}
	
	public ASTNode clone() {
		return new DynamicInstanceMember(instance, memberExpression);
	}

	public ASTNode getInstance() {
		return instance;
	}

	public ASTNode getMemberExpression() {
		return memberExpression;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(DynamicInstanceMember.class);
		plan.addNodeParam(0, "instance", false);
		plan.addNodeParam(1, "memberExpression", false);
	}
}
