package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;

import com.tuneit.salsa3.ast.IfStatement.Branch;

public class ForStatement extends ASTStatement {
	private ASTNode condition;
	
	private ASTStatement initializationStatement;
	private ASTStatement incrementStatement;
	
	private ASTStatement currentStatement;
	
	public ForStatement() {
		this.condition = null;
		
		this.initializationStatement = new ASTStatement();
		this.incrementStatement = new ASTStatement();
		
		this.currentStatement = this.initializationStatement;
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
	public void dumpStatement(OutputStream os, String indent) {
		PrintStream s = new PrintStream(os);
	
		s.print(indent);
		s.println("init: ");
		initializationStatement.dumpStatement(os, indent + ASTStatement.TABSTOP);
		
		s.print(indent);
		s.println("increment: ");
		incrementStatement.dumpStatement(os, indent + ASTStatement.TABSTOP);
		
		super.dumpStatement(os, indent);
	}
	
	@Override
	public String toString() {
		return "For [" + condition + "]";
	}
}
