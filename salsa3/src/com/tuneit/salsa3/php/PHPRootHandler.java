package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;

public class PHPRootHandler extends PHPStatementHandler implements PHPParserHandler {
	

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		/* TODO: Global operations, such as function/class declarations */
		
		return super.handleState(state);
	}

}
