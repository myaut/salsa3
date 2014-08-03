package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.FunctionCall.Argument;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class FunctionDeclaration extends ASTStatement {
	private String functionName;
	
	private TypeName returnType;
	private List<String> functionDeclarators;
	
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("FunctionDeclaration [name=");
		sb.append(functionName);
		
		sb.append(", args=[");
		
		for(ASTNode argument : arguments) {
			sb.append(argument.toString());			
			sb.append(", ");
		}
		
		sb.append("]]");
		
		return sb.toString();
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(FunctionDeclaration.class);
		plan.addStringParam(0, "functionName", false);
		plan.addNodeParam(1, "returnType", false);
		plan.addStringListParam(2, "functionDeclarators", false);
		plan.addNodeListParam(3, "arguments", false);
	}
}
