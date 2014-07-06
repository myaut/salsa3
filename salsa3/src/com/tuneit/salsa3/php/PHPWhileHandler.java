package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPWhileHandler extends PHPStatementHandler implements
		PHPParserHandler {
	private WhileStatement whileStatement;
	private PHPParserHandler parent;
	
	public PHPWhileHandler(PHPParserHandler parent) {
		this.parent = parent;
		
		this.whileStatement = null;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		
		if(state.isState("while_cond")) {
			ASTNode expr = state.getNode("expr");	
			whileStatement = new WhileStatement(expr);
			
			setRootNode(whileStatement);
			
			return this;
		}
		else if(state.isState("while_end")) {
			return parent;
		}
		
		return super.handleState(state);
	}
}
