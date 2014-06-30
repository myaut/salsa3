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
	
	private boolean beginIsHandled = false;

	public PHPFunctionCall(PHPParserHandler parent) {
		this.parent = parent;
		this.fcall = null;
	}
	
	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(!beginIsHandled && state.isState("begin_function_call")) {
			Literal functionNameNode = (Literal) state.getNode("function_name");
			String functionName = functionNameNode.getToken();
			
			fcall = new FunctionCall(functionName);			
			functionNameNode.setNode(fcall);
			
			/* For nested function calls, pass "begin_function_call" state to  */
			beginIsHandled = true;
			
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
			ASTNode result = state.getNode("result");
			
			result.setNode(fcall);
			
			return parent;	
		}
				
		return PHPExpressionHelper.handleState(state, this);
	}

	@Override
	public ASTNode getRootNode() {
		return fcall;
	}
}
