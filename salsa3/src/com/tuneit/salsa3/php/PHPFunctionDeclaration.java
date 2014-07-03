package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.FunctionDeclaration;
import com.tuneit.salsa3.ast.Literal;
import com.tuneit.salsa3.ast.Variable;
import com.tuneit.salsa3.ast.VariableDeclaration;

public class PHPFunctionDeclaration extends PHPStatementHandler {	
	private static final int ZEND_RECV = 63;
	private static final int ZEND_RECV_INIT = 64;
	
	private PHPParserHandler parent;
	private FunctionDeclaration fdecl;

	public PHPFunctionDeclaration(PHPParserHandler parent) {
		super();
		this.parent = parent;
		this.fdecl = null;
	}
	
	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(state.isState("begin_function_declaration")) {
			int returnReference = state.getIntParam("return_reference");
			Literal functionNameNode = (Literal) state.getNode("function_name");
			String functionName = functionNameNode.getToken();
			
			fdecl = new FunctionDeclaration(functionName);	
			
			if(returnReference == 1) {
				fdecl.addReturnTypeDeclarator("&");
			}
			
			setRootNode(fdecl);
			
			return this;
		}
		else if(state.isState("receive_arg")) {
			int op = state.getIntParam("op");
			int passByReference = state.getIntParam("pass_by_reference");
			
			Variable varName = (Variable) state.getNode("varname");
			ASTNode classType = state.getNodeOptional("class_type"); 
			
			VariableDeclaration argument = null;
			String typeName = "";
			
			if(classType != null) {
				typeName = ((Literal) classType).getToken();
			}
			
			switch(op) {
			case ZEND_RECV:
				argument = fdecl.addArgument(varName.getVarName(), typeName);
				break;
			case ZEND_RECV_INIT:
				ASTNode defaultValue = state.getNode("initialization");
				argument = fdecl.addArgument(varName.getVarName(), typeName, defaultValue);
				break;
			default:
				throw new ParserException("Invalid receive_arg op " + op + "!");
			}
			
			if(passByReference == 1) {
				argument.addTypeQualifier("&");
			}
			
			return this;
		}
		else if(state.isState("end_function_declaration")) {
			return parent;
		}
		
		return super.handleState(state);
	}

	@Override
	public ASTNode getRootNode() {
		return fdecl;
	}
}
