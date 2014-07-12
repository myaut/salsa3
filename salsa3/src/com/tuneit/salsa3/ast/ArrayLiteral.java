package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class ArrayLiteral extends ASTNode {
	public static class Element extends ASTNode {
		private ASTNode key;
		private ASTNode value;
		
		public Element(ASTNode value) {
			this.key = null;
			this.value = value;
		}
		
		public Element(ASTNode value, ASTNode key) {
			this.key = key;
			this.value = value;
		}

		public ASTNode getKey() {
			return key;
		}

		public ASTNode getValue() {
			return value;
		}
		
		static {
			ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Element.class);
			plan.addNodeParam(0, "value", false);
			plan.addNodeParam(1, "key", true);
		}
	}
	
	private List<ASTNode> elements;
	
	public ArrayLiteral() {
		this.elements = new ArrayList<ASTNode>();
	}
	
	public ArrayLiteral(List<ASTNode> elements) {
		this.elements = elements;
	}
	
	public List<ASTNode> getElements() {
		return elements;
	}
	
	public void addElement(Element el) {
		elements.add(el);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("ArrayLiteral {");
		
		for(ASTNode node : elements) {
			Element el = (Element) node;
			
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
	
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ArrayLiteral.class);
		plan.addNodeListParam(0, "elements", false);
	}
}
