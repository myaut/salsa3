package com.tuneit.salsa3.ast;

import java.util.List;
import java.util.ArrayList;

public class VariableDeclaration extends ASTNode {
	private Variable variable;
	private List<String> typeDeclarators;
	
	public VariableDeclaration(Variable var) {
		this.variable = var;
		this.typeDeclarators = new ArrayList<String>();
	}

	public Variable getVariable() {
		return variable;
	}
	
	public void addTypeDeclarator(String declarator) {
		this.typeDeclarators.add(declarator);
	}
	
	public List<String> getTypeDeclarators() {
		return this.typeDeclarators;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("VariableDeclaration [var=");
		sb.append(variable.toString());
		
		sb.append(", declarators=");		
		for(String declarator : typeDeclarators) {
			sb.append(declarator);
			sb.append(",");
		}
		
		sb.append("]]");
		
		return sb.toString();
	}
}
