package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ParserException;

public class ASTNode {
	private ASTNode realNode = null;
	
	/* ZNodes in PHP are mutable. For example, result for a + b may be a,
	 * and then 'a' is reused in following statement. However, it require tight
	 * integration with ZNode2AST. Instead of that, implement this hack globally:
	 * When a is replaced with a + b:
	 * 		- clone a
	 * 		- create BinaryOperation(+, clone of a, b),
	 * 		- set a.realNode = BinaryOperation() 
	 * When next statement acesses a, it calls getNode() and gets BinaryOperation() */
	
	public ASTNode getNode() {
		if(realNode == null) {
			return this;
		}
		
		return realNode;
	}
	
	public void setNode(ASTNode node) {
		this.realNode = node;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public ASTNode cloneNode() throws ParserException {
		try {
			return (ASTNode) this.clone();
		}
		catch(CloneNotSupportedException cnse) {
			throw new ParserException("Cloning not supported by this ASTNode");
		}
	}
}
