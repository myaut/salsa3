package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;

public class IfStatement extends ASTStatement {
	public static class Branch extends ASTStatement {
		public ASTNode condition;
		
		public Branch() {
			super();
			condition = null;
		}
		
		/* Serialization code */
		static {
			ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Branch.class);
		}
	};
	
	private List<Branch> branches;
	private Branch currentBranch;
	private Branch elseBranch;
	
	public IfStatement() {
		super();
		
		branches = new ArrayList<Branch>();
		elseBranch = new Branch();
		
		currentBranch = null;
	}
	
	public void beginBranch(ASTNode condition) {
		Branch branch = new Branch();		
		branch.condition = condition;
		
		condition.reuseInExpression(this);
		
		currentBranch = branch;
		branches.add(branch);
	}
	
	public void endBranch() {
		currentBranch = null;
	}
	
	public void beginElseBranch() {
		currentBranch = elseBranch;
	}
	
	@Override
	public void addChild(ASTNode child) {
		this.currentBranch.addChild(child);
	}
	
	@Override 
	public Object serializeStatement(ASTStatementSerializer serializer) throws ASTNodeSerdesException {
		Object ifStatement = serializer.createStatement(this);
		
		for(Branch branch : branches) {
			serializer.addSpecialNode(ifStatement, "if", branch.condition);			
			branch.serializeStatementChildren(serializer, ifStatement);
		}
	
		serializer.addSpecialNode(ifStatement, "else", null);
		elseBranch.serializeStatementChildren(serializer, ifStatement);
		
		return ifStatement;
	}
	
	@Override
	public String toString() {
		return "IfStatement";
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(IfStatement.class);
	}	
}
