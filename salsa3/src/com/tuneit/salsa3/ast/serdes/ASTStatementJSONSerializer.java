package com.tuneit.salsa3.ast.serdes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;

public class ASTStatementJSONSerializer implements ASTStatementSerializer {
	public static String AST_STMT_STATE = "_s";
	public static String AST_STMT_NODE = "_n";
	public static String AST_STMT_STMTS = "_l";
	
	private ASTNodeSerializer nodeSerializer; 
	
	public ASTStatementJSONSerializer(boolean useShortNames) {
		nodeSerializer = new ASTNodeJSONSerializer(useShortNames);
	}
	
	@Override
	public Object createStatement(ASTNode node) throws ASTNodeSerdesException {
		JSONArray array = new JSONArray();
		JSONObject jsonStatement = new JSONObject();
		JSONObject jsonNode = (JSONObject) ASTNodeSerdes.serializeNode(nodeSerializer, node);
		
		try {
			jsonStatement.put(AST_STMT_NODE, jsonNode);
			jsonStatement.put(AST_STMT_STMTS, array);
		
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
		
		return jsonStatement;
	}

	@Override
	public void addNode(Object stmt, ASTNode node) throws ASTNodeSerdesException  {		
		JSONArray array = getArrayFromStatement(stmt);
		JSONObject jsonNode = (JSONObject) ASTNodeSerdes.serializeNode(nodeSerializer, node);
		
		array.put(jsonNode);
	}
	
	@Override
	public void addStatement(Object stmt, ASTStatement node, Object subStatement) throws ASTNodeSerdesException  {
		JSONArray array = getArrayFromStatement(stmt);
		JSONObject jsonStatement = (JSONObject) node.serializeStatement(this);
		
		array.put(jsonStatement);
	}

	@Override
	public void addSpecialNode(Object stmt, String stateName, ASTNode node) throws ASTNodeSerdesException {
		JSONArray array = getArrayFromStatement(stmt);
		JSONObject state = new JSONObject();
		
		try {
			state.put(AST_STMT_STATE, stateName);
				
			if(node != null) {
				JSONObject jsonNode = (JSONObject) ASTNodeSerdes.serializeNode(nodeSerializer, node);
				state.put(AST_STMT_NODE, jsonNode);
			}			
			
			array.put(state);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}

	private JSONArray getArrayFromStatement(Object stmt) throws ASTNodeSerdesException {
		JSONObject jsonStatement = (JSONObject) stmt;
		
		try {
			return (JSONArray) jsonStatement.get(AST_STMT_STMTS);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}
}
