package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPStatementHandler implements PHPParserHandler {
	private ASTStatement rootNode = null;

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(state.isState("echo")) {
			ASTNode arg = state.getNode("arg");
			
			/* Treat echo operator as a special function call */
			FunctionCall fcall = new FunctionCall("echo"); 			
			fcall.addArgument(arg, false);
			
			rootNode.addChild(fcall);
			
			return this;
		}
		else if(state.isState("assign")) {
			ASTNode value = state.getNode("value");
			ASTNode variable = state.getNode("variable");
			
			/* Do not do node-replacement here, ignore 'result' */
			
			Assign assign = new Assign(variable, value);
			rootNode.addChild(assign);
			
			return this;
		}
		else if(state.isState("if_cond")) {
			PHPIfHandler ifHandler = new PHPIfHandler(this);
			PHPParserHandler newHandler = ifHandler.handleState(state);
			
			ASTNode ifNode = ifHandler.getRootNode();
			
			rootNode.addChild(ifNode);
			
			return newHandler;
		}
		
		return PHPExpressionHelper.handleState(state, this);
	}

	@Override
	public ASTNode getRootNode() {
		return rootNode;
	}	
	
	public void setRootNode(ASTStatement rootNode) {
		this.rootNode = rootNode;
	}
}
