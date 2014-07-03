package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class IfStatement extends ASTStatement {
	public class Branch extends ASTStatement {
		public ASTNode condition;
		
		public Branch() {
			super();
			condition = null;
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
	public void dumpStatement(OutputStream os, String indent) {
		PrintStream s = new PrintStream(os);
		
		for(Branch branch : branches) {
			s.print(indent);
			s.print("if ");
			s.println(branch.condition.toString());
			
			branch.dumpStatement(os, indent + ASTStatement.TABSTOP);
		}
	
		s.print(indent);
		s.println("else");
		
		elseBranch.dumpStatement(os, indent + ASTStatement.TABSTOP);
	}
	
	@Override
	public String toString() {
		return "IfStatement";
	}
}
