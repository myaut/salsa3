package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.Literal;
import com.tuneit.salsa3.ast.TryStatement;
import com.tuneit.salsa3.ast.Variable;
import com.tuneit.salsa3.ast.VariableDeclaration;

public class PHPTryHandler extends PHPStatementHandler implements
		PHPParserHandler {
	private TryStatement tryStatement;
	private PHPParserHandler parent;
	
	private boolean isFirstTry;
	
	public PHPTryHandler(PHPParserHandler parent) {
		this.parent = parent;
		
		this.tryStatement = new TryStatement();
		setRootNode(this.tryStatement);
		
		this.isFirstTry = true;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state)
			throws ParserException {
		
		if(isFirstTry && state.isState("try")) {
			ASTNode tryToken = state.getNode("try_token");			
			tryToken.setNode(tryStatement);
			
			isFirstTry = false;
			
			return this;
		}
		else if(state.isState("catch")) {		
			Literal classNameNode = (Literal) state.getNode("class_name");
			Variable variable = (Variable) state.getNode("catch_var");
			
			VariableDeclaration varDecl = new VariableDeclaration(variable, classNameNode.getToken());
			
			TryStatement.CatchStatement catchStatement = new TryStatement.CatchStatement(varDecl);
			
			setRootNode(catchStatement);			
			tryStatement.addCatchStatement(catchStatement);
			
			return this;
		}
		else if(state.isState("end_catch")) {	
			setRootNode(null);
			return this;
		}
		else if(state.isState("mark_last_catch")) {
			/* Set root node back to tryStatement */
			setRootNode(this.tryStatement);
			return parent;
		}
		
		return super.handleState(state);
	}
}
