package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>FunctionName</strong> is an AST node 
 * <ul>
 *   <li> functionName -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class FunctionName extends ASTNode {

	@Parameter(offset = 0, optional = false)
	private String functionName;
	
	public FunctionName(String functionName) {
		this.functionName = functionName;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	
}
