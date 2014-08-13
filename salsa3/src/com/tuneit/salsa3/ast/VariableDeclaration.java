package com.tuneit.salsa3.ast;

import java.util.List;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>VariableDeclaration</strong> is an AST node 
 * <ul>
 *   <li> variable -- 
 *   <li> typeName -- 
 *   <li> defaultValue -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class VariableDeclaration extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private Variable variable;

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private TypeName typeName;

	@Parameter(offset = 2, optional = true)
	@NodeParameter
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

		
		
		
		
		

}
