package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

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
	
	public void dumpStatement(OutputStream os) {
		this.dumpStatement(os, "");
	}
	
	public void dumpStatement(OutputStream os, String indent) {
		PrintStream s = new PrintStream(os);
		
		for(ASTNode node : children) {
			s.print(indent);
			s.println(node.toString());
			
			if(node instanceof ASTStatement) {
				ASTStatement stmt = (ASTStatement) node;
				stmt.dumpStatement(os, indent + TABSTOP);
				
				s.println();
			}
		}
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
}
