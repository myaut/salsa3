package com.tuneit.salsa3.ast.serdes;

import com.tuneit.salsa3.ast.Literal;

public interface ASTNodeSerializer {
	public Object createNode(String className) throws ASTNodeSerdesException;
	public void addToNode(Object node, String paramName, String paramShortName, Object value) throws ASTNodeSerdesException;
	
	public Object serializeLiteral(Literal literal) throws ASTNodeSerdesException;
	
	public Object createList() throws ASTNodeSerdesException;
	public void addToList(Object list, Object value) throws ASTNodeSerdesException;
}
