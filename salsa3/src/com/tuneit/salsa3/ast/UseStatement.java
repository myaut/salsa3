package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class UseStatement extends ASTNode {
	private NamespaceName name;
	private NamespaceName alias;
	
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

	@Override
	public String toString() {
		return "UseStatement [name=" + name + ", alias=" + alias + "]";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(UseStatement.class);
		plan.addNodeParam(0, "name", false);
		plan.addNodeParam(1, "alias", false);
	}
}
