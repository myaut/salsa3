package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;

public interface PHPParserHandler {
	public PHPParserHandler handleState(PHPParserState state) throws ParserException;
	public ASTNode getRootNode();
}
