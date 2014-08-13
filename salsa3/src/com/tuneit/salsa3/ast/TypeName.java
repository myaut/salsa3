package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.annotations.ListParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>TypeName</strong> is an AST node 
 * <ul>
 *   <li> typeQualifiers -- 
 *   <li> typeName -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class TypeName extends ASTNode {

	@Parameter(offset = 1, optional = false)
	@ListParameter
	private List<String> typeQualifiers;

	@Parameter(offset = 0, optional = false)
	private String typeName;
	
	public TypeName(String typeName) {
		this.typeName = typeName;
		this.typeQualifiers = new ArrayList<String>();
	}
	
	public TypeName(String typeName, List<String> typeQualifiers) {
		this.typeName = typeName;
		this.typeQualifiers = typeQualifiers;
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
	
		
		
		
		
	
}
