package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>PHPAddToArray</strong> is an AST node 
 * <ul>
 *   <li> array -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class PHPAddToArray extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode array;
	
	public PHPAddToArray(ASTNode array) {
		super();
		this.array = array;
		
		array.reuseInExpression(this);
	}

	public ASTNode getArray() {
		return array;
	}

	
	public ASTNode clone() {
		return new PHPAddToArray(array);
	}

	
}
