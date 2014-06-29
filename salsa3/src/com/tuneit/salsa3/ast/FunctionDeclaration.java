package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.FunctionCall.Argument;

public class FunctionDeclaration extends ASTStatement {
	public class Argument {
		public String name;
		public List<String> typeDeclarators;
		public ASTNode defaultValue;
		
		public Argument(String name) {
			this.name = name;
			this.defaultValue = null;
			this.typeDeclarators = new ArrayList<String>();
		}
		
		public void addTypeDeclarator(String declarator) {
			typeDeclarators.add(declarator);
		}
	}
	
	private String functionName;
	
	private List<String> returnTypeDeclarators;
	private List<String> functionDeclarators;
	
	private List<Argument> arguments;
	
	public FunctionDeclaration(String functionName) {
		this.functionName = functionName;
		
		this.returnTypeDeclarators = new ArrayList<String>();
		this.functionDeclarators = new ArrayList<String>();
		
		this.arguments = new ArrayList<Argument>();
	}
	
	public void addReturnTypeDeclarator(String declarator) {
		returnTypeDeclarators.add(declarator);
	}
	
	public void addFunctionDeclarator(String declarator) {
		functionDeclarators.add(declarator);
	}
	
	public Argument addArgument(String name) {
		Argument arg = new Argument(name);
		arguments.add(arg);
		return arg;
	}
	
	public Argument addArgument(String name, ASTNode defaultValue) {
		Argument arg = new Argument(name);
		arg.defaultValue = defaultValue;
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
	
	public List<Argument> getArguments() {
		return arguments;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("FunctionDeclaration [name=");
		sb.append(functionName);
		
		sb.append(", args=[");
		
		for(Argument argument : arguments) {
			sb.append(argument.name);
			sb.append(", ");
		}
		
		sb.append("]]");
		
		return sb.toString();
	}
}
