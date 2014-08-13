package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.annotations.ListParameter;
import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>FunctionDeclaration</strong> is an AST compound statement 
 * <ul>
 *   <li> functionName -- 
 *   <li> returnType -- 
 *   <li> functionDeclarators -- 
 *   <li> arguments -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class FunctionDeclaration extends ASTStatement {

	@Parameter(offset = 0, optional = false)
	private String functionName;
	

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private TypeName returnType;

	@Parameter(offset = 2, optional = false)
	@ListParameter
	private List<String> functionDeclarators;
	

	@Parameter(offset = 3, optional = false)
	@ListParameter
	@NodeParameter
	private List<ASTNode> arguments;
	
	public FunctionDeclaration(String functionName, ASTNode returnType) {
		this.functionName = functionName;
		
		this.returnType = (TypeName) returnType;
		this.functionDeclarators = new ArrayList<String>();
		
		this.arguments = new ArrayList<ASTNode>();
	}
	
	public FunctionDeclaration(String functionName, ASTNode returnType, 
				List<String> functionDeclarators, List<ASTNode> arguments) {
		this.functionName = functionName;
		
		this.returnType = (TypeName) returnType;
		this.functionDeclarators = functionDeclarators;
		
		this.arguments = arguments;
	}
	
	public void addFunctionDeclarator(String declarator) {
		functionDeclarators.add(declarator);
	}
	
	public VariableDeclaration addArgument(String name, String typeName) {
		VariableDeclaration arg = new VariableDeclaration(new Variable(name), new TypeName(typeName));
		arguments.add(arg);
		return arg;
	}
	
	public VariableDeclaration addArgument(String name, String typeName, ASTNode defaultValue) {
		VariableDeclaration arg = new VariableDeclaration(new Variable(name), new TypeName(typeName), defaultValue);
		arguments.add(arg);
		return arg;
	}

	public String getFunctionName() {
		return functionName;
	}

	public TypeName getReturnType() {
		return returnType;
	}

	public List<String> getFunctionDeclarators() {
		return functionDeclarators;
	}
	
	public List<ASTNode> getArguments() {
		return arguments;
	}
	
		
		
		
		
		
	
}
