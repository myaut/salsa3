package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class TypeName extends ASTNode {
	private List<String> typeQualifiers;
	private String typeName;
	
	public TypeName(String typeName) {
		this.typeName = typeName;
		this.typeQualifiers = new ArrayList<String>();
	}
	
	public TypeName(String typeName, List<String> typeQualifiers) {
		this.typeName = typeName;
		this.typeQualifiers = typeQualifiers;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public List<String> getTypeQualifiers() {
		return typeQualifiers;
	}
	
	public void addTypeQualifier(String qualifier) {
		typeQualifiers.add(qualifier);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("TypeName [name=");
		sb.append(typeName);
		
		sb.append(", declarators=");		
		for(String declarator : typeQualifiers) {
			sb.append(declarator);
			sb.append(",");
		}
		
		sb.append("]]");
		
		return sb.toString();
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(TypeName.class);
		plan.addStringParam(0, "typeName", false);
		plan.addStringListParam(1, "typeQualifiers", false);
	}	
}
