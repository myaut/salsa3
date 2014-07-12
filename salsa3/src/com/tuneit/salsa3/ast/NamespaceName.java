package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class NamespaceName extends ASTNode {
	private List<String> components;
	
	public NamespaceName() {
		this.components = new ArrayList<String>();
	}
	
	public NamespaceName(List<String> components) {
		this.components = components;
	}
	
	public void addComponent(String component) {
		components.add(component);
	}
	
	public List<String> getComponents() {
		return components;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("NamespaceName [");
		
		for(String component : components) {
			sb.append(component);
			sb.append(".");
		}
		
		sb.append("]");
		
		return sb.toString();
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(NamespaceName.class);
		plan.addStringListParam(0, "components", false);
	}	
}
