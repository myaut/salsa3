package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class ForeachStatement extends ASTStatement {
	private ASTNode iterable;
	private ASTNode key;
	private ASTNode value;
	
	public ForeachStatement(ASTNode iterable) {
		this(iterable, null, null);
	}
	
	public ForeachStatement(ASTNode iterable, ASTNode value) {
		this(iterable, value, null);
	}
	
	public ForeachStatement(ASTNode iterable, ASTNode value, ASTNode key) {
		this.iterable = iterable;
		this.key = key;
		this.value = value;
		
		iterable.reuseInExpression(this);		
		if(value != null) {
			value.reuseInExpression(this);
		}
		if(key != null) {
			key.reuseInExpression(this);
		}
	}

	public ASTNode getKey() {
		return key;
	}

	public void setKey(ASTNode key) {
		this.key = key;
	}

	public ASTNode getValue() {
		return value;
	}

	public void setValue(ASTNode value) {
		this.value = value;
	}

	public ASTNode getIterable() {
		return iterable;
	}

	@Override
	public String toString() {
		return "Foreach [iterable=" + iterable + ", key=" + key
				+ ", value=" + value + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ForeachStatement.class);
		plan.addNodeParam(0, "iterable", false);
		plan.addNodeParam(1, "value", false);
		plan.addNodeParam(2, "key", true);
	}
}

