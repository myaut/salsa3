package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class PHPAddToArray extends ASTNode {
	private ASTNode array;
	
	public PHPAddToArray(ASTNode array) {
		super();
		this.array = array;
	}

	public ASTNode getArray() {
		return array;
	}

	
	public ASTNode clone() {
		return new PHPAddToArray(array);
	}

	@Override
	public String toString() {
		return "PHPAddToArray [array=" + array + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(PHPAddToArray.class);
		plan.addNodeParam(0, "array", false);
	}
}
