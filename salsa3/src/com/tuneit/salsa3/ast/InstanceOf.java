package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>InstanceOf</strong> is an AST node 
 * <ul>
 *   <li> expression -- 
 *   <li> className -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class InstanceOf extends ASTNode {

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode expression;

	@Parameter(offset = 0, optional = false)
	private String className;
	
	public InstanceOf(String className, ASTNode expression) {
		super();
		this.className = className;
		this.expression = expression;
	}
	
	public ASTNode clone() throws CloneNotSupportedException {
		return new InstanceOf(className, (ASTNode) expression.clone());
	}

	public ASTNode getExpression() {
		return expression;
	}

	public String getClassName() {
		return className;
	}
	
}
