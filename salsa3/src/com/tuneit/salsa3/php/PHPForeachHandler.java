package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPForeachHandler extends PHPStatementHandler implements
		PHPParserHandler {
	private ForeachStatement foreachStatement;
	private PHPParserHandler parent;
	private boolean inStatement;
	
	public PHPForeachHandler(PHPParserHandler parent) {
		this.parent = parent;
		this.inStatement = false;
		this.foreachStatement = null;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(state.isState("foreach_begin")) {
			ASTNode array = state.getNode("array");
			
			foreachStatement = new ForeachStatement(array);
			
			setRootNode(foreachStatement);
			return this;
		}
		else if(state.isState("foreach_cont")) {
			ASTNode key = state.getNodeOptional("key");				
			ASTNode value = state.getNode("value");
			
			foreachStatement.setValue(value);
			if(key != null) {
				foreachStatement.setKey(key);				
			}
			
			this.inStatement = true;
			
			return this;
		}
		else if(state.isState("foreach_end")) {
			return parent;
		}
		
		if(this.inStatement)
			return super.handleState(state);
		
		/* Only expressions allowed until we reach foreach_cont node */
		return PHPExpressionHelper.handleState(state, this);
	}
}
