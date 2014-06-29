package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.IfStatement;

public class PHPIfHandler extends PHPStatementHandler implements PHPParserHandler {
	private IfStatement ifStatement;
	private PHPParserHandler parent;
	
	public PHPIfHandler(PHPParserHandler parent) {
		super();
		
		this.parent = parent;
		this.ifStatement = new IfStatement();
		
		setRootNode(ifStatement);
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(state.isState("if_cond")) {
			ASTNode condition = state.getNode("cond");			
			ifStatement.beginBranch(condition);
			
			return this;
		}
		else if(state.isState("if_after")) {
			ifStatement.endBranch();
			
			/* PHP parser doesn't have special state for 'else', so this handler considers
			 * any statement inside if, but outside if_cound...if_after block is else statement */
			ifStatement.beginElseBranch();
			
			return this;
		}
		else if(state.isState("if_end")) {
			return parent;
		}
		
		return super.handleState(state);
	}

}
