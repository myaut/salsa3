package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class VariableList extends ASTNode {
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
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(VariableList.class);
		plan.addNodeListParam(0, "variables", false);
	}	
}
