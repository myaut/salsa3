package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

public class ArrayLiteral extends ASTNode {
	public static class Element {
		public ASTNode key;
		public ASTNode value;
		
		public Element(ASTNode value) {
			this.key = null;
			this.value = value;
		}
		
		public Element(ASTNode value, ASTNode key) {
			this.key = key;
			this.value = value;
		}
	}
	
	private List<Element> elements;
	
	public ArrayLiteral() {
		elements = new ArrayList<Element>();
	}
	
	public List<Element> getElements() {
		return elements;
	}
	
	public void addElement(Element el) {
		elements.add(el);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("ArrayLiteral {");
		
		for(Element el : elements) {
			if(el.key != null) {
				sb.append(el.key.toString());
				sb.append(" : ");
			}
			
			sb.append(el.value.toString());			
			sb.append(", ");
		}
		
		sb.append("}");
		
		return sb.toString();
	}
}
