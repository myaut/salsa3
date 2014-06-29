package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.FunctionCall;
import com.tuneit.salsa3.ast.Literal;

public class PHPFunctionCall implements PHPParserHandler {
	private static final int ZEND_SEND_VAL = 65;
	private static final int ZEND_SEND_VAR = 66;
	private static final int ZEND_SEND_REF = 67;
	
	private PHPParserHandler parent;
	private FunctionCall fcall;

	public PHPFunctionCall(PHPParserHandler parent) {
		this.parent = parent;
		this.fcall = null;
	}
	
	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(state.isState("begin_function_call")) {
			Literal functionNameNode = (Literal) state.getNode("function_name");
			String functionName = functionNameNode.getToken();
			
			fcall = new FunctionCall(functionName);			
			functionNameNode.setNode(fcall);
			
			return this;
		}
		else if(state.isState("pass_param")) {
			int op = state.getIntParam("op");
			ASTNode param = state.getNode("param");
			
			switch(op) {
			case ZEND_SEND_VAL:
			case ZEND_SEND_VAR:
				fcall.addArgument(param, false);
				break;
			case ZEND_SEND_REF:
				fcall.addArgument(param, true);
				break;
			}
			
			return this;
		}
		else if(state.isState("end_function_call")) {
			return parent;
		}
		
		ASTNode node = PHPExpressionHelper.handleState(state);
		return this;
	}

	@Override
	public ASTNode getRootNode() {
		return fcall;
	}
}
