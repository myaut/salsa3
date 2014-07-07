package com.tuneit.salsa3.ast.serdes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;

public class ASTStatementJSONDeserializer implements ASTStatementDeserializer {
	public static class JSONStatementIterator extends StatementIterator {
		public static final int LAST_STATEMENT = -1;
		
		private int index;
		private Node node;
		private JSONArray stmtList;
		
		public int getIndex() {
			return index;
		}
		
		public Node getNode() {
			return node;
		}
		
		public JSONArray getStmtList() {
			return stmtList;
		}
		
		public boolean isLastStatement() {
			return index == LAST_STATEMENT;
		}

		public JSONStatementIterator(int index, Node node, JSONArray stmtList) {
			super();
			this.index = index;
			this.node = node;
			this.stmtList = stmtList;
		}
	}
	
	@Override
	public ASTStatement getStatementNode(Object o) throws ASTNodeSerdesException {
		JSONObject jsonNode = (JSONObject) o;
		
		try {
			JSONObject stmtNode = jsonNode.getJSONObject(ASTStatementJSONSerializer.AST_STMT_NODE);			
			ASTNode node = ASTNodeSerdes.deserializeNode(stmtNode);
			
			if(!(node instanceof ASTStatement)) {
				throw new ASTNodeSerdesException("Failed to deserialize statement - node returned");
			}
			
			return (ASTStatement) node;
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}

	@Override
	public StatementIterator getFirstStatement(Object o) throws ASTNodeSerdesException {
		JSONObject jsonNode = (JSONObject) o;		
		
		try {
			JSONArray stmtList = jsonNode.getJSONArray(ASTStatementJSONSerializer.AST_STMT_STMTS);
			
			if(stmtList.length() == 0) {
				return new JSONStatementIterator(JSONStatementIterator.LAST_STATEMENT, null, stmtList);
			}
			
			return createIterator(stmtList, 0);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}
	
	@Override
	public StatementIterator getNextStatement(Object o,
			StatementIterator iterator) throws ASTNodeSerdesException {
		try {
			JSONStatementIterator jsonIterator = (JSONStatementIterator) iterator;
			JSONArray stmtList = jsonIterator.getStmtList();
			int nextIndex = jsonIterator.getIndex() + 1;
			
			if(stmtList.length() == nextIndex) {
				return new JSONStatementIterator(JSONStatementIterator.LAST_STATEMENT, null, stmtList);
			}
			
			return createIterator(stmtList, nextIndex);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}
	
	private StatementIterator createIterator(JSONArray stmtList, int index) throws JSONException {
		JSONObject stmtNode = stmtList.getJSONObject(index);
		Node node = deserializeNode(stmtNode);
		
		return new JSONStatementIterator(index, node, stmtList);
	}
	
	private Node deserializeNode(JSONObject stmtNode) throws JSONException {
		if(stmtNode.has(ASTStatementJSONSerializer.AST_STMT_STATE)) {
			String state = stmtNode.getString(ASTStatementJSONSerializer.AST_STMT_STATE);
			Object node = stmtNode.get(ASTStatementJSONSerializer.AST_STMT_NODE);
			
			return new SpecialState(node, state);
		}
		else if(stmtNode.has(ASTStatementJSONSerializer.AST_STMT_STMTS)) {
			return new Statement(stmtNode);
		}
		
		return new Node(stmtNode);
	}
}
