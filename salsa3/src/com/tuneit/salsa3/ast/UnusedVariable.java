package com.tuneit.salsa3.ast;

/**
 * <strong>UnusedVariable</strong> is an AST  
 * 
 * @author Sergey Klyaus
 */
public class UnusedVariable extends Variable {
	public UnusedVariable() {
		super("_");
	}
	
	public ASTNode clone() {
		return new UnusedVariable();
	}
	
}
