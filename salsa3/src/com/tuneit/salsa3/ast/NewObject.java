package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>NewObject</strong> is an AST node 
 * <ul>
 *   <li> className -- 
 *   <li> constructorCall -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class NewObject extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode className;

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode constructorCall;
	
	public NewObject(ASTNode className, ASTNode constructorCall) {
		super();
		this.className = className;
		this.constructorCall = constructorCall;
		
		className.reuseInExpression(this);
		constructorCall.reuseInExpression(this);
	}

	public ASTNode getClassName() {
		return className;
	}

	public ASTNode getConstructorCall() {
		return constructorCall;
	}
	
}
