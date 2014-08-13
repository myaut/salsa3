package com.tuneit.salsa3.ast.serdes;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class ASTNodeJSONSerializer implements ASTNodeSerializer {
	public static String ASTTYPE_NAME = "_t";
	
	private boolean useShortNames;
	
	public ASTNodeJSONSerializer(boolean useShortNames) {
		super();
		this.useShortNames = useShortNames;
	}
	
	@Override
	public Object createNode(String className) throws ASTNodeSerdesException {
		JSONObject jso = new JSONObject();
		
		try {
			jso.put(ASTTYPE_NAME, className);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
		
		return jso;
	}

	@Override
	public void addToNode(Object node, String paramName, 
			String paramShortName, Object value) throws ASTNodeSerdesException {
		JSONObject jso = (JSONObject) node;

		try {
			jso.put(useShortNames ? paramShortName : paramName, value);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}

	@Override
	public Object createList() throws ASTNodeSerdesException {
		JSONArray array = new JSONArray();
		return array;
	}

	@Override
	public void addToList(Object list, Object value)
			throws ASTNodeSerdesException {
		JSONArray array = (JSONArray) list;
		array.put(value);
	}

}

