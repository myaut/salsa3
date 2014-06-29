package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Class for compound statements
 */
public class ASTStatement extends ASTNode {
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
				stmt.dumpStatement(os, indent + "   ");
			}
		}
	}
}
