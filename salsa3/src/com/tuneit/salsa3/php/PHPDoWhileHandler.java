package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPDoWhileHandler extends PHPStatementHandler implements
		PHPParserHandler {
	private DoWhileStatement doWhileStatement;
	private PHPParserHandler parent;
	
	public PHPDoWhileHandler(PHPParserHandler parent) {
		this.parent = parent;
		
		this.doWhileStatement = null;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		
		if(state.isState("do_while_begin")) {
			doWhileStatement = new DoWhileStatement();			
			setRootNode(doWhileStatement);
			
			return this;
		}
		else if(state.isState("do_while_end")) {
			ASTNode expr = state.getNode("expr");
			doWhileStatement.setCondition(expr);
			
			return parent;
		}
		
		return super.handleState(state);
	}
}
