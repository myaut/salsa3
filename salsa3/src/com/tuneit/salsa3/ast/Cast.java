package com.tuneit.salsa3.ast;

public class Cast extends ASTNode {
	private TypeName type;
	private ASTNode expression;
	private String castType;
	
	public Cast(TypeName type, ASTNode expression) {
		super();
		this.type = type;
		this.expression = expression;
		this.castType = null;
	}
	
	public Cast(TypeName type, ASTNode expression, String castType) {
		super();
		this.type = type;
		this.expression = expression;
		this.castType = castType;
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
	
}
