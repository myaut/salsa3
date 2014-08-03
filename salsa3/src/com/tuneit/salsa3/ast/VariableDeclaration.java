package com.tuneit.salsa3.ast;

import java.util.List;
import java.util.ArrayList;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class VariableDeclaration extends ASTNode {
	private Variable variable;
	private TypeName typeName;
	private ASTNode defaultValue;
	
	public VariableDeclaration(ASTNode var, ASTNode typeName) {
		this(var, typeName, null);
	}
	
	public VariableDeclaration(ASTNode var, ASTNode typeName, ASTNode defaultValue) {
		this.variable = (Variable) var;
		this.typeName = (TypeName) typeName;
		this.defaultValue = defaultValue;
		
		var.reuseInExpression(this);
		typeName.reuseInExpression(this);
		if(defaultValue != null) {
			defaultValue.reuseInExpression(this);
		}
	}

	public Variable getVariable() {
		return variable;
	}
	
	public TypeName getTypeName() {
		return typeName;
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

	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(VariableDeclaration.class);
		plan.addNodeParam(0, "variable", false);
		plan.addNodeParam(1, "typeName", false);
		plan.addNodeParam(2, "defaultValue", true);
	}
}
