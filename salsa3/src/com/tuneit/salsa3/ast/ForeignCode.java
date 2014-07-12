package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class ForeignCode extends ASTNode {
	private ASTNode code;
	
	public ForeignCode(ASTNode code) {
		super();
		
		this.code = code;
	}
	
	public ASTNode getCode() {
		return code;
	}

	@Override
	public String toString() {
		return "ForeignCode [code=" + code + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ForeignCode.class);
		plan.addNodeParam(0, "code", false);
	}
}
