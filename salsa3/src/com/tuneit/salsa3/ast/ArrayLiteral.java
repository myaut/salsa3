package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.annotations.ListParameter;
import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>ArrayLiteral</strong> is an AST node 
 * <ul>
 *   <li> elements -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ArrayLiteral extends ASTNode {
	
	/**
	 * <strong>Element</strong> is an AST node 
	 * <ul>
	 *   <li> key -- 
	 *   <li> value -- 
	 * </ul>
	 * 
	 * @author Sergey Klyaus
	 */
	public static class Element extends ASTNode {

		@Parameter(offset = 1, optional = true)
		@NodeParameter
		private ASTNode key;

		@Parameter(offset = 0, optional = false)
		@NodeParameter
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
		
	}
	

	@Parameter(offset = 0, optional = false)
	@ListParameter
	@NodeParameter
	private List<ASTNode> elements;
	
	public ArrayLiteral() {
		this.elements = new ArrayList<ASTNode>();
	}
	
	public ArrayLiteral(List<ASTNode> elements) {
		this.elements = elements;
	}
	
	public ASTNode clone() throws CloneNotSupportedException {
		ArrayLiteral clone = new ArrayLiteral();
		
		for(ASTNode node : elements) {						
			if(node instanceof Element) {
				Element el = (Element) node;
				ASTNode keyClone = null;
				
				if(el.getKey() != null) {
					keyClone = (ASTNode) el.getKey().clone();
				}
				
				ASTNode valueClone = (ASTNode) el.getValue().clone();
				
				Element elClone = new Element(valueClone, keyClone);
				
				clone.addElement(elClone);
			}
		}
		
		return clone;
	}
	
	public List<ASTNode> getElements() {
		return elements;
	}
	
	public void addElement(Element el) {
		elements.add(el);
	}
	
		
		
			
			
		
		
	
}
