package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;

/**
 * Class for compound statements
 */
public class ASTStatement extends ASTNode {
	public static final String TABSTOP = "   ";
	
	private ArrayList<ASTNode> children;
	
	public ASTStatement() {
		children = new ArrayList<ASTNode>();
	}

	public ArrayList<ASTNode> getChildren() {
		return children;
	}	
	
	public void addChild(ASTNode child) {
		children.add(child);
	}
	
	public Object serializeStatement(ASTStatementSerializer serializer) throws ASTNodeSerdesException {
		Object statement = serializer.createStatement(this);

		serializeStatementChildren(serializer, statement);
		
		return statement;
	}
	
	public void serializeStatementChildren(ASTStatementSerializer serializer, Object statement) throws ASTNodeSerdesException {
		for(ASTNode node : children) {
			serializeStatementNode(serializer, statement, node);
		}		
	}
	
	public void serializeStatementNode(ASTStatementSerializer serializer, Object statement, ASTNode node) throws ASTNodeSerdesException {
		if(node instanceof ASTStatement) {
			ASTStatement stmt = (ASTStatement) node;
			Object subStatement = stmt.serializeStatement(serializer);
			
			serializer.addStatement(statement, stmt, subStatement);
		}
		else {
			serializer.addNode(statement, node);
		}
	}
	
	public void deserializeState(String state, ASTNode node) throws ASTNodeSerdesException {
		throw new ASTNodeSerdesException("Couldn't deserialize state " + state + 
							"for statement" + this.getClass().getName() + "!");
	}
	
	@Override
	public void filterReused() {
		ArrayList<ASTNode> newChildren = new ArrayList<ASTNode>();
		
		for(ASTNode node : children) {
			if(!node.getReusedInExpression()) {
				newChildren.add(node);
			}
			
			node.filterReused();
		}
		
		children = newChildren;
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ASTStatement.class);
	}
}
