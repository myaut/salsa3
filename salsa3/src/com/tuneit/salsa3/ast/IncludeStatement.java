package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>IncludeStatement</strong> is an AST node 
 * <ul>
 *   <li> includePath -- 
 *   <li> isOnce -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class IncludeStatement extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode includePath;	

	@Parameter(offset = 1, optional = false)
	private boolean isOnce;
	
	public IncludeStatement(ASTNode includePath, Boolean isOnce) {
		super();
		this.includePath = includePath;
		this.isOnce = isOnce;
	}

	public ASTNode getIncludePath() {
		return includePath;
	}

	public boolean isOnce() {
		return isOnce;
	}
	
	public boolean getIsOnce() {
		return isOnce;
	}

	
}
