package com.tuneit.salsa3.ast.serdes;

import java.util.HashMap;

import com.tuneit.salsa3.ast.ASTNode;

public class ASTNodeSerdes {
	private static final String AST_PACKAGE_NAME = "com.tuneit.salsa3.ast";
	
	public static HashMap<Class<?>, ASTNodeSerdesPlan> classHashMap;
	public static HashMap<String, ASTNodeSerdesPlan> planHashMap;
	
	static {
		classHashMap = new HashMap<Class<?>, ASTNodeSerdesPlan>();
		planHashMap = new HashMap<String, ASTNodeSerdesPlan>();
	}
	
	public static ASTNodeSerdesPlan newPlan(Class<?> nodeClass) {
		ASTNodeSerdesPlan plan = new ASTNodeSerdesPlan(nodeClass);
		String name = plan.getClassName();
		
		classHashMap.put(nodeClass, plan);
		planHashMap.put(name, plan);
		
		return plan;
	}
	
	public static Object serializeNode(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
		ASTNodeSerdesPlan plan = classHashMap.get(o.getClass());
		
		if(plan == null) {
			plan = newPlan(o.getClass());
		}
		
		return plan.serializeNode(serializer, o);
	}
	
	public static ASTNode deserializeNode(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException {
		String className = deserializer.getNodeClassName(o);
		ASTNodeSerdesPlan plan = planHashMap.get(className);
		
		if(plan == null) { 
			try {
				Class<?> nodeClass = Class.forName(AST_PACKAGE_NAME + "." + className, true, 
												ASTNode.class.getClassLoader());
				plan = newPlan(nodeClass);
			} catch (ClassNotFoundException e) {
				throw new ASTNodeSerdesException("Class '" + className + "' was not found", e);
			}
		}
		
		return plan.deserializeNode(deserializer, o);
	}
}
