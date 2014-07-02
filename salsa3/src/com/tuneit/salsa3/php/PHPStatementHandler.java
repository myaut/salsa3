package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPStatementHandler implements PHPParserHandler {
	private ASTStatement rootNode = null;
	
	private static final int ZEND_EVAL = 1 << 0;
	private static final int ZEND_INCLUDE = 1 << 1;
	private static final int ZEND_INCLUDE_ONCE = 1 << 2;
	private static final int ZEND_REQUIRE = 1 << 3;
	private static final int ZEND_REQUIRE_ONCE = 1 << 4;

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(state.isState("echo")) {
			return this.handleEcho(state);
		}
		else if(state.isState("assign")) {
			return this.handleAssign(state);
		}
		else if(state.isState("if_cond")) {
			return this.handleIfCondition(state);
		}
		else if(state.isState("include_or_eval")) {
			return this.handleIncludeOrEval(state);
		}
		else if(state.isState("use")) {
			return this.handleUse(state);
		}
		
		return PHPExpressionHelper.handleState(state, this);
	}
	
	public PHPParserHandler handleEcho(PHPParserState state) throws ParserException {
		ASTNode arg = state.getNode("arg");
		
		/* Treat echo operator as a special function call */
		FunctionCall fcall = new FunctionCall("echo"); 			
		fcall.addArgument(arg, false);
		
		rootNode.addChild(fcall);
		
		return this;
	}
	
	public PHPParserHandler handleAssign(PHPParserState state) throws ParserException {
		ASTNode value = state.getNode("value");
		ASTNode variable = state.getNode("variable");
		
		/* Do not do node-replacement here, ignore 'result' */
		
		Assign assign = new Assign(variable, value);
		rootNode.addChild(assign);
		
		return this;
	}
	
	public PHPParserHandler handleIncludeOrEval(PHPParserState state) throws ParserException {
		int type = state.getIntParam("type");
		ASTNode op1 = state.getNode("op1");
		
		switch(type) {
		case ZEND_EVAL:
			ForeignCode foreignCode = new ForeignCode(op1);
			rootNode.addChild(foreignCode);
			break;
		case ZEND_INCLUDE:
		case ZEND_REQUIRE:
			IncludeStatement includeStatement = new IncludeStatement(op1, false);
			rootNode.addChild(includeStatement);
			break;
		case ZEND_INCLUDE_ONCE:
		case ZEND_REQUIRE_ONCE:
			IncludeStatement includeStatementOnce = new IncludeStatement(op1, true);
			rootNode.addChild(includeStatementOnce);
			break;
		default:
			throw new ParserException("Unknown include_or_eval type: " + type + "!");
		}
		
		return this;
	}
	
	public PHPParserHandler handleIfCondition(PHPParserState state) throws ParserException {
		PHPIfHandler ifHandler = new PHPIfHandler(this);
		PHPParserHandler newHandler = ifHandler.handleState(state);
		
		ASTNode ifNode = ifHandler.getRootNode();
		
		rootNode.addChild(ifNode);
		
		return newHandler;
	}
	
	public PHPParserHandler handleUse(PHPParserState state) throws ParserException {
		ASTNode nsName = state.getNode("ns_name");
		ASTNode nsAlias = state.getNodeOptional("new_name");
		
		UseStatement useNode = new UseStatement((NamespaceName) nsName, (NamespaceName) nsAlias);
		
		rootNode.addChild(useNode);
		
		return this;
	}

	@Override
	public ASTNode getRootNode() {
		return rootNode;
	}	
	
	public void setRootNode(ASTStatement rootNode) {
		this.rootNode = rootNode;
	}
}
