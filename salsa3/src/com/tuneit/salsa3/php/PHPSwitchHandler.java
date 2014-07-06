package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPSwitchHandler extends PHPStatementHandler implements
		PHPParserHandler {
	private SwitchStatement switchStatement;
	private PHPParserHandler parent;
	
	public PHPSwitchHandler(PHPParserHandler parent) {
		this.parent = parent;
		
		this.switchStatement = null;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		
		if(state.isState("switch_cond")) {
			ASTNode cond = state.getNode("cond");	
			switchStatement = new SwitchStatement(cond);
			
			setRootNode(switchStatement);
			
			return this;
		}
		else if(state.isState("case_before_statement") || state.isState("default_before_statement")) {
			ASTNode pattern = state.getNodeOptional("case_expr");
			
			switchStatement.addCase(pattern);
			
			return this;
		}
		else if(state.isState("case_after_statement")) {
			// Simply ignore that
		}
		else if(state.isState("switch_end")) {
			return parent;
		}
		
		return super.handleState(state);
	}
}
