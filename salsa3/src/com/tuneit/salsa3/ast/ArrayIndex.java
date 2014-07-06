package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class ArrayIndex extends ASTNode {
	private ASTNode array;
	private ASTNode index;
	
	public ArrayIndex(ASTNode array, ASTNode index) {
		super();
		this.array = array;
		this.index = index;
	}

	public ASTNode getArray() {
		return array;
	}

	public ASTNode getIndex() {
		return index;
	}
	
	public ASTNode clone() {
		return new ArrayIndex(array, index);
	}

	@Override
	public String toString() {
		return "ArrayIndex [array=" + array + ", index=" + index + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ArrayIndex.class);
		plan.addNodeParam(0, "array", false);
		plan.addNodeParam(1, "index", false);
	}
}
