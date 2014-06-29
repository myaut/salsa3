package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPStatementHandler implements PHPParserHandler {
	private ASTStatement rootNode;
	
	public PHPStatementHandler() {
		rootNode = new ASTStatement();
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(state.isState("echo")) {
			ASTNode arg = state.getNode("arg");
			
			/* Treat echo operator as a special function call */
			FunctionCall fcall = new FunctionCall("echo"); 			
			fcall.addArgument(arg);
			
			rootNode.addChild(fcall);
		}
		
		ASTNode node = PHPExpressionHelper.handleState(state);
		if(node != null) {
			rootNode.addChild(node);
		}		
		
		return this;
	}

	@Override
	public ASTNode getRootNode() {
		return rootNode;
	}	
}
