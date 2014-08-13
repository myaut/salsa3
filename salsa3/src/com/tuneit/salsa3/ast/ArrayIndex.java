package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>ArrayIndex</strong> is an AST node 
 * <ul>
 *   <li> array -- 
 *   <li> index -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ArrayIndex extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode array;

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode index;
	
	public ArrayIndex(ASTNode array, ASTNode index) {
		super();
		this.array = array;
		this.index = index;
	}

	public ASTNode getArray() {
		return array;
	}

	public ASTNode getIndex() {
		return index;
	}
	
	public ASTNode clone() {
		return new ArrayIndex(array, index);
	}

	
}
