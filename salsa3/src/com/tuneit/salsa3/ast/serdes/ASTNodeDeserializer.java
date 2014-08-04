package com.tuneit.salsa3.ast.serdes;

import java.util.Iterator;

import com.tuneit.salsa3.ast.Literal;

public interface ASTNodeDeserializer {
	public String getNodeClassName(Object o) throws ASTNodeSerdesException;
	public Object getNodeParam(Object o, String paramName, String paramShortName) throws ASTNodeSerdesException;
	
	public Iterator<Object> getListIterator(Object o) throws ASTNodeSerdesException;
}
