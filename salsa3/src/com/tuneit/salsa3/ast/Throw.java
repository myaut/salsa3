package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>Throw</strong> is an AST node 
 * <ul>
 *   <li> object -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class Throw extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode object;
	
	public Throw(ASTNode object) {
		super();
		this.object = object;
		
		object.reuseInExpression(this);
	}

	public ASTNode getObject() {
		return object;
	}

	
}
