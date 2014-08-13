package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.annotations.ListParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>NamespaceName</strong> is an AST node 
 * <ul>
 *   <li> components -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class NamespaceName extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@ListParameter
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
	
		
		
		
		
	
}
