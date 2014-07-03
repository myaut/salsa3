package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPFinallyHandler extends PHPStatementHandler implements
		PHPParserHandler {
	private ASTStatement finallyStatement;
	private PHPParserHandler parent;
	
	public PHPFinallyHandler(PHPParserHandler parent) {
		this.parent = parent;
		
		this.finallyStatement = null;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state)
			throws ParserException {
		
		if(state.isState("finally")) {
			ASTNode finallyToken = state.getNode("finally_token");					
			finallyStatement = new ASTStatement();
			
			setRootNode(finallyStatement);
			finallyToken.setNode(finallyStatement);
			
			return this;
		}
		else if(state.isState("end_finally")) {
			TryStatement tryStatement = (TryStatement) state.getNode("try_token");
			
			tryStatement.setFinallyStatement(finallyStatement);
			
			return parent;
		}
		
		return super.handleState(state);
	}
}
