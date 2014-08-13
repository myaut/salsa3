package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>Constant</strong> is an AST node 
 * <ul>
 *   <li> constName -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class Constant extends ASTNode {

	@Parameter(offset = 0, optional = false)
	private String constName;
	
	public Constant(String constName) {
		this.constName = constName;
	}
	
	public String getConstName() {
		return constName;
	}
	
	public ASTNode clone() {
		return new Constant(constName);
	}

	
}
