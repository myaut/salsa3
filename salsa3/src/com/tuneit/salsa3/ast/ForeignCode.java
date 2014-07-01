package com.tuneit.salsa3.ast;

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
}
