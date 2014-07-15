package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.IfStatement;

public class PHPIfHandler extends PHPStatementHandler implements PHPParserHandler {
	private IfStatement ifStatement;
	private PHPParserHandler parent;
	
	private boolean isInIf = true; 
	private int conditions = 0;
	
	public PHPIfHandler(PHPParserHandler parent) {
		super();
		
		this.parent = parent;
		this.ifStatement = new IfStatement();
		
		setRootNode(ifStatement);
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(isInIf && state.isState("if_cond")) {
			ASTNode condition = state.getNode("cond");			
			ifStatement.beginBranch(condition);
			
			isInIf = false;
			++conditions;
			
			return this;
		}
		else if(state.isState("if_after")) {
			int initialize = state.getIntParam("initialize");
			
			ifStatement.endBranch();
			
			/* PHP parser doesn't have special state for 'else', so this handler considers
			 * any statement inside if, but outside if_cound...if_after block is else statement */
			ifStatement.beginElseBranch();
			
			isInIf = true;
			if(initialize == 0) {
				--conditions;
			}
			
			return this;
		}
		else if(state.isState("if_end")) {
			if(conditions < 0) {
				throw new ParserException("Invalid number of if-conditions!");
			}
			if(--conditions == 0) {
				return parent;
			}
			return this;
		}
		
		return super.handleState(state);
	}

}
