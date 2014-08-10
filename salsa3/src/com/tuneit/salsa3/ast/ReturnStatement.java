package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>ReturnStatement</strong> is an AST node 
 * <ul>
 *   <li> returnExpression -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ReturnStatement extends ASTNode {

	@Parameter(offset = 0, optional = true)
	@NodeParameter
	private ASTNode returnExpression;
	
	public ReturnStatement() {
		super();
		this.returnExpression = null;
	}
	
	public ReturnStatement(ASTNode returnExpression) {
		super();
		this.returnExpression = returnExpression;
	}
	
	public ASTNode getReturnExpression() {
		return returnExpression;
	}
	
}
