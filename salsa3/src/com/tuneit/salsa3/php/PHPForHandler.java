package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPForHandler extends PHPStatementHandler implements
		PHPParserHandler {
	private ForStatement forStatement;
	private PHPParserHandler parent;
	
	private boolean isForBody = false;
	
	public PHPForHandler(PHPParserHandler parent) {
		this.parent = parent;
		
		this.forStatement = null;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(!isForBody && state.isState("for_begin")) {
			forStatement = new ForStatement();
			
			setRootNode(forStatement);
			
			return this;
		}
		else if(!isForBody && state.isState("for_cond")) {
			ASTNode expr = state.getNode("expr");				
			
			forStatement.setCondition(expr);
			forStatement.beginIncrementStatement();
			
			return this;
		}
		else if(state.isState("for_before_statement")) {
			isForBody = true;
			
			forStatement.endIncrementStatement();
			return this;
		}
		else if(state.isState("for_end")) {
			return parent;
		}
		
		return super.handleState(state);
	}
}
