package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;



/**
 * <strong>IfStatement</strong> is an AST compound statement 
 * <ul>
 *   <li> branches -- 
 *   <li> currentBranch -- 
 *   <li> elseBranch -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class IfStatement extends ASTStatement {
	
	/**
	 * <strong>Branch</strong> is an AST compound statement 
	 * <ul>
	 *   <li> condition -- 
	 * </ul>
	 * 
	 * @author Sergey Klyaus
	 */
	public static class Branch extends ASTStatement {
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
	public void deserializeState(String state, ASTNode node) throws ASTNodeSerdesException {
		if(state.equals("if")) {
			beginBranch(node);
			return;
		}
		else if(state.equals("else")) {
			beginElseBranch();
			return;
		}
		
		// Let parent throw an exception
		super.deserializeState(state, node);
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
	
	
}
