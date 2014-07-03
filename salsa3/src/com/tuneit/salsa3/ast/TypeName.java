package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

public class TypeName extends ASTNode {
	private List<String> typeQualifiers;
	private String typeName;
	
	public TypeName(String typeName) {
		this.typeName = typeName;
		this.typeQualifiers = new ArrayList<String>();
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
}
