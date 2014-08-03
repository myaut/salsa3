package com.tuneit.salsa3.ast.serdes;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.tuneit.salsa3.ast.Literal;

public class ASTNodeJSONDeserializer implements ASTNodeDeserializer {
	public class JSONNodeIterator implements Iterator<Object> {
		private JSONArray list;
		private int index;
		
		public JSONNodeIterator(JSONArray list) {
			this.index = 0;
			this.list = list;
		}
		
		@Override
		public boolean hasNext() {
			return index < list.length();
		}
		
		@Override
		public Object next() {
			if(index == list.length()) {
				throw new NoSuchElementException();
			}
			
			try {
				Object o = list.get(index);				
				++index;
				
				return o;
			} catch (JSONException e) {
				throw new RuntimeException("JSONNodeIterator: list.get failed!");
			}
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove is not supported by JSONNodeIterator"); 
		}
	}
	
	private boolean useShortNames;
	
	public ASTNodeJSONDeserializer(boolean useShortNames) {
		super();
		this.useShortNames = useShortNames;
	}
	
	@Override
	public String getNodeClassName(Object o) throws ASTNodeSerdesException {
		JSONObject jso = (JSONObject) o;
		
		try {
			return jso.getString(ASTNodeJSONSerializer.ASTTYPE_NAME);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}

	@Override
	public Object getNodeParam(Object o, String paramName, String paramShortName) throws ASTNodeSerdesException {
		JSONObject jso = (JSONObject) o;
		
		return jso.opt(useShortNames ? paramShortName : paramName);
	}

	@Override
	public Iterator<Object> getListIterator(Object o) throws ASTNodeSerdesException {	
		if(!(o instanceof JSONArray)) {
			throw new ASTNodeSerdesException("Failed to iterate over list - not JSONArray");
		}
		
		return new JSONNodeIterator((JSONArray) o);
	}

	@Override
	public boolean isLiteral(Object o) throws ASTNodeSerdesException {
		return o instanceof String;
	}

	@Override
	public Literal deserializeLiteral(Object o) throws ASTNodeSerdesException {
		String literal = (String) o;	
		String[] pair = literal.split("\\|", 2);
		
		try {
			Literal.Type type = Enum.valueOf(Literal.Type.class, pair[0]);
			return new Literal(type, pair[1]);
		}
		catch(Exception e) {
			throw new ASTNodeSerdesException("Failed to parse literal '" + literal + "'!", e);
		}
	}
}

