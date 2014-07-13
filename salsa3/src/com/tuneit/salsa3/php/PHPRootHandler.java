package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;

public class PHPRootHandler extends PHPStatementHandler implements PHPParserHandler {
	public PHPRootHandler() {
		setRootNode(new ASTStatement());
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		/* TODO: Global operations, such as function/class declarations */
		if(state.isState("begin_function_declaration")) {
			PHPFunctionDeclaration phpFunctionDecl = new PHPFunctionDeclaration(this);
			PHPParserHandler newHandler = phpFunctionDecl.handleState(state);
			
			ASTNode fdecl = newHandler.getRootNode();
			
			((ASTStatement) getRootNode()).addChild(fdecl);
			
			return newHandler;
		}
		else if(state.isState("begin_class_declaration")) {
			PHPClassHandler phpClassDecl = new PHPClassHandler(this);
			PHPParserHandler newHandler = phpClassDecl.handleState(state);
			
			ASTNode classDecl = newHandler.getRootNode();
			
			((ASTStatement) getRootNode()).addChild(classDecl);
			
			return newHandler;
		}
		
		return super.handleState(state);
	}

}
