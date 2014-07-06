package com.tuneit.salsa3.ast.serdes;

import com.tuneit.salsa3.ast.ASTNode;

public interface ASTStatementSerializer {
	public Object createStatement(ASTNode node) throws ASTNodeSerdesException;
	
	public void addNode(Object stmt, ASTNode node) throws ASTNodeSerdesException;
	public void addStatement(Object stmt, ASTNode node, Object subStatement) throws ASTNodeSerdesException;
	public void addSpecialNode(Object stmt, String stateName, ASTNode node) throws ASTNodeSerdesException;
}
