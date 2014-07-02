package com.tuneit.salsa3.ast;

public class UseStatement extends ASTNode {
	private NamespaceName name;
	private NamespaceName alias;
	
	public UseStatement(NamespaceName name, NamespaceName alias) {
		super();
		this.name = name;
		this.alias = alias;
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
}
