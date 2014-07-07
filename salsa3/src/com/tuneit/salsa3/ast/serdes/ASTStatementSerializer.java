package com.tuneit.salsa3.ast.serdes;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;

public interface ASTStatementSerializer {
	public Object createStatement(ASTNode node) throws ASTNodeSerdesException;
	
	public void addNode(Object stmt, ASTNode node) throws ASTNodeSerdesException;
	public void addStatement(Object stmt, ASTStatement node, Object subStatement) throws ASTNodeSerdesException;
	public void addSpecialNode(Object stmt, String stateName, ASTNode node) throws ASTNodeSerdesException;
}
