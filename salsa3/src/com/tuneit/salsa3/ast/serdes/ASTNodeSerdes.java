package com.tuneit.salsa3.ast.serdes;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.BreakStatement;

public class ASTNodeSerdes {
	public static HashMap<Class<?>, ASTNodeSerdesPlan> classHashMap;
	public static HashMap<String, ASTNodeSerdesPlan> planHashMap;
	
	static {
		classHashMap = new HashMap<Class<?>, ASTNodeSerdesPlan>();
		planHashMap = new HashMap<String, ASTNodeSerdesPlan>();
	}
	
	public static ASTNodeSerdesPlan newPlan(Class<?> nodeClass) {
		ASTNodeSerdesPlan plan = new ASTNodeSerdesPlan(nodeClass);
		String name = nodeClass.getName();
		
		classHashMap.put(nodeClass, plan);
		planHashMap.put(name, plan);
		
		return plan;
	}
	
	public static Object serializeNode(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {		
		ASTNodeSerdesPlan plan = classHashMap.get(o.getClass());
				
		if(plan == null) {
			throw new ASTNodeSerdesException("Doesn't know how to serialize node '" + o.getClass().getName() + "'");
		}
		
		return plan.serializeNode(serializer, o);
	}
	
	public static ASTNode deserializeNode(Object o) throws ASTNodeSerdesException {
		/* FIXME: Universal deserializer */
		JSONObject jso = (JSONObject) o;
		
		try {
			String className = jso.getString(ASTNodeJSONSerializer.ASTTYPE_NAME);
			ASTNodeSerdesPlan plan = planHashMap.get(className);
			
			jso.put(ASTNodeJSONSerializer.ASTTYPE_NAME, (Object) null);
			
			return plan.deserializeNode(jso);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error!", e);
		}
	}
	
	public static void main(String[] args) throws ASTNodeSerdesException, JSONException
	{
		BreakStatement statements[] = {
			new BreakStatement(),
			new BreakStatement(2),
			new BreakStatement("label"),
		};
		
		for(BreakStatement stmt : statements) {
			JSONObject jso = (JSONObject) serializeNode(new ASTNodeJSONSerializer(), stmt);
			System.out.println(jso.toString());
			BreakStatement stmt2 = (BreakStatement) deserializeNode(jso);
			
			System.out.println(stmt2.toString());
		}
	}
}
