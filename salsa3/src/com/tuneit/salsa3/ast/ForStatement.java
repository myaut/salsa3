package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>ForStatement</strong> is an AST compound statement 
 * <ul>
 *   <li> condition -- 
 *   <li> initializationStatement -- 
 *   <li> incrementStatement -- 
 *   <li> currentStatement -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ForStatement extends ASTStatement {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode condition;
	
	private ASTStatement initializationStatement;
	private ASTStatement incrementStatement;
	
	private ASTStatement currentStatement;
	
	public ForStatement(ASTNode condition) {
		this.condition = condition;
		
		this.initializationStatement = new ASTStatement();
		this.incrementStatement = new ASTStatement();
		
		this.currentStatement = this.initializationStatement;
	}
	
	public ForStatement() {
		this(null);
	}
	
	public ASTNode getCondition() {
		return condition;
	}
	
	public void setCondition(ASTNode condition) {
		this.condition = condition;
	}
	
	public ASTStatement getInitializationStatement() {
		return initializationStatement;
	}
	
	public ASTStatement getIncrementStatement() {
		return incrementStatement;
	}
	
	public void beginIncrementStatement() {
		this.currentStatement = this.incrementStatement;
	}
	
	public void endIncrementStatement() {
		this.currentStatement = this;
	}
	
	@Override
	public void addChild(ASTNode child) {
		if(currentStatement == this) {
			super.addChild(child);
		}
		else {
			this.currentStatement.addChild(child);
		}
	}
	
	@Override 
	public Object serializeStatement(ASTStatementSerializer serializer) throws ASTNodeSerdesException {
		Object forStatement = serializer.createStatement(this); 
		
		serializer.addSpecialNode(forStatement, "init", null);
		initializationStatement.serializeStatementChildren(serializer, forStatement);
		
		serializer.addSpecialNode(forStatement, "increment", null);
		incrementStatement.serializeStatementChildren(serializer, forStatement);		
		
		serializer.addSpecialNode(forStatement, "body", null);
		serializeStatementChildren(serializer, forStatement);
		
		return forStatement;
	}
	
	
	@Override
	public void deserializeState(String state, ASTNode node) throws ASTNodeSerdesException {
		if(state.equals("init")) {
			this.currentStatement = initializationStatement;
		}
		else if(state.equals("increment")) {
			this.currentStatement = incrementStatement;
		}
		else if(state.equals("body")) {
			this.currentStatement = this;
		}
		else {
			super.deserializeState(state, node);
		}
	}
	
}
