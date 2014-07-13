package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class InstanceMember extends ASTNode {
	private ASTNode instance;
	private String memberName;
	
	public InstanceMember(ASTNode instance, String memberName) {
		super();
		this.instance = instance;
		this.memberName = memberName;
	}
	
	public ASTNode clone() {
		return new InstanceMember(instance, memberName);
	}

	public ASTNode getInstance() {
		return instance;
	}

	public String getMemberName() {
		return memberName;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(InstanceMember.class);
		plan.addNodeParam(0, "instance", false);
		plan.addStringParam(1, "memberName", false);
	}
}
