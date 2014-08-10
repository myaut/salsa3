package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ParserException;



/**
 * <strong>ASTNode</strong> is an AST  
 * 
 * @author Sergey Klyaus
 */
public class ASTNode {
	private ASTNode realNode = null;
	private ASTNode parent = null;
	private boolean reusedInExpression = false;
	
	/*
	 * Some AST nodes may be reported twice: i.e. function calls
	 * Tag first call with reusedInExpression flag, 
	 * so they may be removed from root statement tree
	 */
	
	public void reuseInExpression(ASTNode expression) {
		reusedInExpression = true;
	}
	
	public boolean getReusedInExpression() {
		return reusedInExpression;
	}
	
	void filterReused() {
		/* NOTIMPLEMENTED */
	}
	
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
		ASTNode parent = this;
		
		while(parent.getParent() != null) {
			parent = parent.getParent(); 
		}
		
		parent.realNode = node;
		node.setParent(parent);
	}
	
	private ASTNode getParent() {
		return parent;
	}
	
	private void setParent(ASTNode parent) {
		this.parent = parent;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public ASTNode cloneNode() throws ParserException {
		try {
			return (ASTNode) this.clone();
		}
		catch(CloneNotSupportedException cnse) {
			throw new ParserException("Cloning not supported by " + this.getClass().getName() + "!");
		}
	}
}
