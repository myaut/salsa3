package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SwitchStatement extends ASTStatement {
	public static class Case {
		public ASTNode pattern;
		public ListIterator<ASTNode> iterator;
	};
	
	private ASTNode expr;
	private List<Case> cases;
	
	public SwitchStatement(ASTNode expr) {
		this.expr = expr;
		this.cases = new ArrayList<Case>();
	}
	
	public void addCase(ASTNode pattern) {
		List<ASTNode> children = getChildren(); 
		ListIterator<ASTNode> iterator = children.listIterator(children.size());
		
		Case kase = new Case();
		
		kase.pattern = pattern;
		kase.iterator = iterator;
		
		cases.add(kase);
	}

	public ASTNode getExpr() {
		return expr;
	}

	public List<Case> getCases() {
		return cases;
	}

	@Override
	public String toString() {
		return "Switch [expr=" + expr + "]";
	}
	
	@Override
	public void dumpStatement(OutputStream os, String indent) {
		PrintStream s = new PrintStream(os);
		
		ListIterator<Case> caseIterator = cases.listIterator();
		ListIterator<ASTNode> nodeIterator = getChildren().listIterator();
		
		String nodeIndent = indent + TABSTOP;
		
		/* Iterate over nodes in this statement.
		 * If case references current node through iterator, print case
		 * until it wouldn't reference other node */
		while(nodeIterator.hasNext()) {
			ASTNode node = nodeIterator.next();
			
			while(caseIterator.hasNext()) {						
				Case kase = caseIterator.next();
				
				/* Node already picked next, so substitute one from index */
				if(kase.iterator.previousIndex() != (nodeIterator.previousIndex() - 1)) {
					/* Case references other expression, go back and print nodes */
					caseIterator.previous();
					break;					
				}
				
				s.print(indent);
				if(kase.pattern == null) {
					s.println("default");
				}
				else {
					s.print("case ");
					s.println(kase.pattern.toString());
				}
			}
			
			s.print(nodeIndent);
			s.println(node.toString());
			
			if(node instanceof ASTStatement) {
				ASTStatement stmt = (ASTStatement) node;
				stmt.dumpStatement(os, nodeIndent + TABSTOP);
				
				s.println();
			}
		}
	}
	
}
