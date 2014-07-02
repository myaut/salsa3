package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

public class NamespaceName extends ASTNode {
	private List<String> components;
	
	public NamespaceName() {
		components = new ArrayList<String>();
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
}
