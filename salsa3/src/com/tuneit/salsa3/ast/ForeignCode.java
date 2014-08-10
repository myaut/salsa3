package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>ForeignCode</strong> is an AST node 
 * <ul>
 *   <li> code -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ForeignCode extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode code;
	
	public ForeignCode(ASTNode code) {
		super();
		
		this.code = code;
	}
	
	public ASTNode getCode() {
		return code;
	}

	
}
