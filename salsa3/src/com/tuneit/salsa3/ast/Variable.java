package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>Variable</strong> is an AST node 
 * <ul>
 *   <li> varName -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class Variable extends ASTNode {

	@Parameter(offset = 0, optional = false)
	private String varName;
	
	public Variable(String varName) {
		this.varName = varName;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public ASTNode clone() {
		return new Variable(varName);
	}

	
}
