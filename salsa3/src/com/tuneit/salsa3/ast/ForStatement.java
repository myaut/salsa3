package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;

import com.tuneit.salsa3.ast.IfStatement.Branch;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;

public class ForStatement extends ASTStatement {
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
	public String toString() {
		return "For [" + condition + "]";
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
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ForStatement.class);
		plan.addNodeParam(0, "condition", false);
	}
}
