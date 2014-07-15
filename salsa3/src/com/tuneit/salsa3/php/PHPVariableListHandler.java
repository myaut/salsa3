package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPVariableListHandler implements PHPParserHandler {
	private VariableList variableList;
	private Assign assign;
	private PHPParserHandler parent;
	
	public PHPVariableListHandler(PHPParserHandler parent) {
		this.parent = parent;
		
		this.variableList = null;
		this.assign = null;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {		
		if(state.isState("list_init")) {
			variableList = new VariableList();
			return this;
		}
		else if(state.isState("add_list_element")) {
			ASTNode variable = state.getNodeOptional("element");
			
			if(variable == null) {
				variable = new UnusedVariable();
			}
			
			variableList.addVariable(variable);
			return this;
		}
		else if(state.isState("list_end")) {
			ASTNode result = state.getNode("result");
			ASTNode expression = state.getNode("expr");
			
			assign = new Assign(variableList, expression);
			
			result.setNode(assign);
			
			/* XXX: unlike most statements, we have node on last state, 
			 * so we cannot add child froms PHPStatementHandler */
			((ASTStatement) parent.getRootNode()).addChild(assign);			
			
			return parent;
		}
		
		return PHPExpressionHelper.handleState(state, this);
	}

	@Override
	public ASTNode getRootNode() {
		return assign;
	}
}
