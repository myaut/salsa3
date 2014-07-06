package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class Constant extends ASTNode {
	private String constName;
	
	public Constant(String constName) {
		this.constName = constName;
	}
	
	public String getConstName() {
		return constName;
	}
	
	public ASTNode clone() {
		return new Constant(constName);
	}

	@Override
	public String toString() {
		return "Constant [constName=" + constName + "]";
	}
	
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Constant.class);
		plan.addStringParam(0, "constName", false);
	}
}
