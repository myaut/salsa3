package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>TakeReference</strong> is an AST node 
 * <ul>
 *   <li> expression -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class TakeReference extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode expression;
	
	public TakeReference(ASTNode expression) {
		this.expression = expression;
		
		expression.reuseInExpression(this);
	}
	
	public ASTNode getExpression() {
		return expression;
	}
	
}
