package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class Cast extends ASTNode {
	private TypeName type;
	private ASTNode expression;
	private String castType;
	
	public Cast(ASTNode type, ASTNode expression) {
		this(type, expression, null);
	}
	
	public Cast(ASTNode type, ASTNode expression, String castType) {
		super();
		this.type = (TypeName) type;
		this.expression = expression;
		this.castType = castType;
		
		type.reuseInExpression(this);
		expression.reuseInExpression(this);
	}
	
	public Cast clone() {
		return new Cast(type, expression, castType);
	}
	
	public TypeName getType() {
		return type;
	}
	
	public ASTNode getExpression() {
		return expression;
	}
	
	public String getCastType() {
		return castType;
	}
	
	@Override
	public String toString() {
		return "Cast [type=" + type + ", expression=" + expression
				+ ", castType=" + castType + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Cast.class);
		plan.addNodeParam(0, "type", false);
		plan.addNodeParam(1, "expression", false);
		plan.addStringParam(2, "castType", true);
	}
}
