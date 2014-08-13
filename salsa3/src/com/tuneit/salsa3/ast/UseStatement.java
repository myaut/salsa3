package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>UseStatement</strong> is an AST node 
 * <ul>
 *   <li> name -- 
 *   <li> alias -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class UseStatement extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private NamespaceName name;

	@Parameter(offset = 1, optional = true)
	@NodeParameter
	private NamespaceName alias;
	
	public UseStatement(ASTNode name) {
		super();
		this.name = (NamespaceName) name;
		this.alias = null;
	}
	
	public UseStatement(ASTNode name, ASTNode alias) {
		super();
		this.name = (NamespaceName) name;
		this.alias = (NamespaceName) alias;
	}

	public NamespaceName getName() {
		return name;
	}

	public NamespaceName getAlias() {
		return alias;
	}

	
}
