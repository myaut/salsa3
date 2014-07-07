package com.tuneit.salsa3.ast.serdes;

import com.tuneit.salsa3.ast.ASTStatement;

public interface ASTStatementDeserializer {
	public static class Node {
		private Object nodeObject;
		
		public Node(Object nodeObject) {
			super();
			this.nodeObject = nodeObject;
		}

		public Object getNodeObject() {
			return nodeObject;
		}
	}
	
	public static class Statement extends Node {
		public Statement(Object nodeObject) {
			super(nodeObject);
		}
	}
	
	public static class SpecialState extends Node {
		private String stateName;
		
		public SpecialState(Object nodeObject, String stateName) {
			super(nodeObject);
			this.stateName = stateName;
		}

		public String getStateName() {
			return stateName;
		}
	}
	
	public static abstract class StatementIterator {
		public abstract Node getNode();
		public abstract boolean isLastStatement();
	}
	
	public ASTStatement getStatementNode(Object o) throws ASTNodeSerdesException;
	
	public StatementIterator getFirstStatement(Object o) throws ASTNodeSerdesException;
	public StatementIterator getNextStatement(Object o, StatementIterator iterator) throws ASTNodeSerdesException;	
}
