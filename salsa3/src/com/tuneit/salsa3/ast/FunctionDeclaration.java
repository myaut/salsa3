package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.FunctionCall.Argument;

public class FunctionDeclaration extends ASTStatement {
	private String functionName;
	
	private List<String> returnTypeDeclarators;
	private List<String> functionDeclarators;
	
	private List<VariableDeclaration> arguments;
	
	public FunctionDeclaration(String functionName) {
		this.functionName = functionName;
		
		this.returnTypeDeclarators = new ArrayList<String>();
		this.functionDeclarators = new ArrayList<String>();
		
		this.arguments = new ArrayList<VariableDeclaration>();
	}
	
	public void addReturnTypeDeclarator(String declarator) {
		returnTypeDeclarators.add(declarator);
	}
	
	public void addFunctionDeclarator(String declarator) {
		functionDeclarators.add(declarator);
	}
	
	public VariableDeclaration addArgument(String name, String typeName) {
		VariableDeclaration arg = new VariableDeclaration(new Variable(name), typeName);
		arguments.add(arg);
		return arg;
	}
	
	public VariableDeclaration addArgument(String name, String typeName, ASTNode defaultValue) {
		VariableDeclaration arg = new VariableDeclaration(new Variable(name), typeName, defaultValue);
		arguments.add(arg);
		return arg;
	}

	public String getFunctionName() {
		return functionName;
	}

	public List<String> getReturnTypeDeclarators() {
		return returnTypeDeclarators;
	}

	public List<String> getFunctionDeclarators() {
		return functionDeclarators;
	}
	
	public List<VariableDeclaration> getArguments() {
		return arguments;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("FunctionDeclaration [name=");
		sb.append(functionName);
		
		sb.append(", args=[");
		
		for(VariableDeclaration argument : arguments) {
			sb.append(argument.toString());			
			sb.append(", ");
		}
		
		sb.append("]]");
		
		return sb.toString();
	}
}
