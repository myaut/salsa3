package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;

public class TryStatement extends ASTStatement {
	public static class CatchStatement extends ASTStatement {
		public VariableDeclaration varDecl;
		
		public CatchStatement(VariableDeclaration varDecl) {
			super();
			this.varDecl = varDecl;
		}
	};
	
	private List<CatchStatement> catches;
	private ASTStatement finallyStatement;
	
	public TryStatement() {
		super();
		
		catches = new ArrayList<CatchStatement>();
		finallyStatement = null;
	}
	
	public void addCatchStatement(CatchStatement katch) {
		this.catches.add(katch);
	}
	
	public void setFinallyStatement(ASTStatement finallyStatement) {
		this.finallyStatement = finallyStatement;
	}
	
	@Override 
	public Object serializeStatement(ASTStatementSerializer serializer) throws ASTNodeSerdesException {
		Object tryStatement = serializer.createStatement(this);
		
		serializer.addSpecialNode(tryStatement, "try", null);
		serializeStatementChildren(serializer, tryStatement);
		
		for(CatchStatement katch : catches) {
			serializer.addSpecialNode(tryStatement, "try", katch.varDecl);
			katch.serializeStatementChildren(serializer, tryStatement);
		}
	
		if(finallyStatement != null) {
			serializer.addSpecialNode(tryStatement, "finally", null);
			finallyStatement.serializeStatementChildren(serializer, tryStatement);
		}
		
		return tryStatement;
	}
	
	@Override
	public String toString() {
		return "TryStatement";
	}
}
