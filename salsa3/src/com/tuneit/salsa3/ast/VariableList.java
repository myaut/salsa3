package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.ListParameter;
import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>VariableList</strong> is an AST node 
 * <ul>
 *   <li> variables -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class VariableList extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@ListParameter
	@NodeParameter
	private List<ASTNode> variables;
	
	public VariableList() {
		super();
		this.variables = new ArrayList<ASTNode>();
	}
	
	public VariableList(List<ASTNode> variables) {
		super();
		this.variables = variables;
	}
	
	public List<ASTNode> getVariables() {
		return variables;
	}
	
	public void addVariable(ASTNode variable) {
		variables.add(variable);
	}
	
}
