package com.tuneit.salsa3.ast.serdes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tuneit.salsa3.ast.ASTNode;

public class ASTStatementJSONSerializer implements ASTStatementSerializer {
	public static String AST_STMT_STATE = "_stmtstate";
	public static String AST_STMT_NODE = "_stmtnode";
	public static String AST_STMT_STMTS = "_stmts";
	public static String AST_STMT_STMT = "_stmt";
	
	private static ASTNodeSerializer nodeSerializer = new ASTNodeJSONSerializer();
	
	@Override
	public Object createStatement(ASTNode node) throws ASTNodeSerdesException {
		JSONArray array = new JSONArray();
		JSONObject jsonStatement = new JSONObject();
		JSONObject jsonNode = (JSONObject) ASTNodeSerdes.serializeNode(nodeSerializer, node);
		
		try {
			jsonStatement.put(AST_STMT_NODE, jsonNode);
			jsonStatement.put(AST_STMT_STMT, array);
		
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
	public void addStatement(Object stmt, ASTNode node, Object subStatement) throws ASTNodeSerdesException  {
		JSONArray array = getArrayFromStatement(stmt);
		JSONObject jsonNode = (JSONObject) ASTNodeSerdes.serializeNode(nodeSerializer, node);
		JSONObject jsonStatement = new JSONObject();
		
		try {
			jsonStatement.put(AST_STMT_NODE, jsonNode);
			jsonStatement.put(AST_STMT_STMTS, subStatement);
		
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
		
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
			return (JSONArray) jsonStatement.get(AST_STMT_STMT);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}
}
