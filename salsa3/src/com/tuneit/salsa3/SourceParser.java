package com.tuneit.salsa3;
import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTStatement;

public interface SourceParser {
	public ASTStatement parse() throws ParserException;
}
