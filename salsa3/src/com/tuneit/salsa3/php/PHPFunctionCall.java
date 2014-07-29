package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.FunctionCall;
import com.tuneit.salsa3.ast.FunctionName;
import com.tuneit.salsa3.ast.Literal;
import com.tuneit.salsa3.ast.NewObject;
import com.tuneit.salsa3.ast.StaticClassMember;
import com.tuneit.salsa3.ast.TakeReference;

public class PHPFunctionCall implements PHPParserHandler {
	private static final int ZEND_SEND_VAL = 65;
	private static final int ZEND_SEND_VAR = 66;
	private static final int ZEND_SEND_REF = 67;
	
	private PHPParserHandler parent;
	private FunctionCall fcall;
	
	private boolean beginIsHandled = false;
	private boolean isNewObject = false;

	public PHPFunctionCall(PHPParserHandler parent) {
		this.parent = parent;
		this.fcall = null;
	}
	
	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(!beginIsHandled && state.isState("begin_function_call")) {
			Literal functionNameNode = (Literal) state.getNode("function_name");
			String functionName = functionNameNode.getToken();
			
			fcall = new FunctionCall(new FunctionName(functionName));			
			functionNameNode.setNode(fcall);
			
			/* For nested function calls, pass "begin_function_call" state to  */
			beginIsHandled = true;
			
			return this;
		}
		else if(!beginIsHandled && state.isState("begin_class_member_function_call")) {
			Literal classNameNode = (Literal) state.getNode("class_name");
			Literal methodNameNode = (Literal) state.getNode("method_name");
			
			StaticClassMember method = new StaticClassMember(methodNameNode.getToken());
			method.addClassName(classNameNode.getToken());
			
			fcall = new FunctionCall(method);			
			classNameNode.setNode(fcall);
			
			beginIsHandled = true;
			
			return this;
		}
		else if(!beginIsHandled && state.isState("begin_method_call")) {
			ASTNode leftBracket = state.getNode("left_bracket");
			
			fcall = new FunctionCall(leftBracket.cloneNode());			
			leftBracket.setNode(fcall);
			
			beginIsHandled = true;
			
			return this;
		}
		else if(!beginIsHandled && state.isState("begin_new_object")) {
			ASTNode newToken = state.getNode("new_token");
			ASTNode classTypeNode = state.getNode("class_type");
			
			if(classTypeNode instanceof Literal) {
				Literal litClassName = (Literal) classTypeNode;
				fcall = new FunctionCall(new FunctionName(litClassName.getToken()));
			}
			else {
				fcall = new FunctionCall(classTypeNode);
			}
			
			
			NewObject newObject = new NewObject(classTypeNode, fcall);
			
			newToken.setNode(newObject);
			
			beginIsHandled = true;
			isNewObject = true;
			
			return this;
		}
		else if(!beginIsHandled && state.isState("begin_dynamic_function_call")) {
			ASTNode functionNameNode = state.getNode("function_name");
			
			fcall = new FunctionCall(functionNameNode);			
			functionNameNode.setNode(fcall);
						
			beginIsHandled = true;
			
			return this;
		}
		else if(state.isState("pass_param")) {
			int op = state.getIntParam("op");
			ASTNode param = state.getNode("param");
			
			switch(op) {
			case ZEND_SEND_VAL:
			case ZEND_SEND_VAR:
				fcall.addArgument(param);
				break;
			case ZEND_SEND_REF:
				fcall.addArgument(new TakeReference(param));
				break;
			}
			
			return this;
		}
		else if(state.isState("end_function_call")) {
			ASTNode result = state.getNode("result");
			
			result.setNode(fcall);
			
			return parent;	
		}
		else if(isNewObject && state.isState("end_new_object")) {
			return parent;
		}
				
		return PHPExpressionHelper.handleState(state, this);
	}

	@Override
	public ASTNode getRootNode() {
		return fcall;
	}
}
