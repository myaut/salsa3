package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;



/**
 * <strong>TryStatement</strong> is an AST compound statement 
 * <ul>
 *   <li> catches -- 
 *   <li> finallyStatement -- 
 *   <li> currentStatement -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class TryStatement extends ASTStatement {
	
	/**
	 * <strong>CatchStatement</strong> is an AST compound statement 
	 * <ul>
	 *   <li> varDecl -- 
	 * </ul>
	 * 
	 * @author Sergey Klyaus
	 */
	private static class CatchStatement extends ASTStatement {
		public VariableDeclaration varDecl;
		
		public CatchStatement(VariableDeclaration varDecl) {
			super();
			this.varDecl = varDecl;
		}
	};
	
	private List<CatchStatement> catches;
	private ASTStatement finallyStatement;
	private ASTStatement currentStatement;
	
	public TryStatement() {
		super();
		
		catches = new ArrayList<CatchStatement>();
		finallyStatement = null;
		currentStatement = this;
	}
	
	public void addCatchStatement(VariableDeclaration varDecl) {
		CatchStatement katch = new CatchStatement(varDecl);
		currentStatement = katch;
		
		this.catches.add(katch);
	}
	
	public void setFinallyStatement(ASTStatement finallyStatement) {
		this.currentStatement = this.finallyStatement = finallyStatement;
	}
	
	@Override
	public void addChild(ASTNode child) {
		if(currentStatement == this) {
			super.addChild(child);
			return;
		}
		
		this.currentStatement.addChild(child);
	}
	
	@Override 
	public Object serializeStatement(ASTStatementSerializer serializer) throws ASTNodeSerdesException {
		Object tryStatement = serializer.createStatement(this);
		
		serializer.addSpecialNode(tryStatement, "try", null);
		serializeStatementChildren(serializer, tryStatement);
		
		for(CatchStatement katch : catches) {
			serializer.addSpecialNode(tryStatement, "catch", katch.varDecl);
			katch.serializeStatementChildren(serializer, tryStatement);
		}
	
		if(finallyStatement != null) {
			serializer.addSpecialNode(tryStatement, "finally", null);
			finallyStatement.serializeStatementChildren(serializer, tryStatement);
		}
		
		return tryStatement;
	}
	
	@Override
	public void deserializeState(String state, ASTNode node) throws ASTNodeSerdesException {
		if(state.equals("try")) {
			currentStatement = this;
		}
		else if(state.equals("catch")) {
			addCatchStatement((VariableDeclaration) node);
		}
		else if(state.equals("finally")) {
			setFinallyStatement(new ASTStatement());
		}
		else {
			super.deserializeState(state, node);
		}
	}
	
	
}
