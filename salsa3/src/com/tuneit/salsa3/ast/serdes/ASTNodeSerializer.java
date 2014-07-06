package com.tuneit.salsa3.ast.serdes;

public interface ASTNodeSerializer {
	public Object createNode(String className) throws ASTNodeSerdesException;
	public void addToNode(Object node, String param, Object value) throws ASTNodeSerdesException;
	
	public Object createList() throws ASTNodeSerdesException;
	public void addToList(Object list, Object value) throws ASTNodeSerdesException;
}
