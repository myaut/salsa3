package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>Assign</strong> is an AST node 
 * <ul>
 *   <li> left -- 
 *   <li> right -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class Assign extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode left;

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode right;
	
	public Assign(ASTNode left, ASTNode right) {
		super();
		this.left = left;
		this.right = right;
		
		left.reuseInExpression(this);
		right.reuseInExpression(this);
	}
	
	public ASTNode clone() {
		return new Assign(left, right);
	}
	
	public ASTNode getLeft() {
		return left;
	}

	public ASTNode getRight() {
		return right;
	}

	
}
