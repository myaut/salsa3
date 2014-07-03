package com.tuneit.salsa3.ast;

import java.util.List;
import java.util.ArrayList;

public class VariableDeclaration extends ASTNode {
	private Variable variable;
	private TypeName typeName;
	private ASTNode defaultValue;
	
	public VariableDeclaration(Variable var, String typeName) {
		this.variable = var;
		this.typeName = new TypeName(typeName);
		this.defaultValue = null;
	}
	
	public VariableDeclaration(Variable var, String typeName, ASTNode defaultValue) {
		this.variable = var;
		this.typeName = new TypeName(typeName);
		this.defaultValue = defaultValue;
	}

	public Variable getVariable() {
		return variable;
	}
	
	public void addTypeQualifier(String qualifier) {
		this.typeName.addTypeQualifier(qualifier);
	}
	
	public List<String> getTypeQualifiers() {
		return typeName.getTypeQualifiers();
	}
	
	public ASTNode getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("VD [");
		
		sb.append(variable);
		sb.append(" : ");
		sb.append(typeName);
		
		if(defaultValue != null) {
			sb.append(" = ");
			sb.append(defaultValue);
		}
		
		sb.append("]");
		
		return sb.toString();
	}

}
