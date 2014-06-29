package com.tuneit.salsa3.ast;

import java.util.ArrayList;

public class FunctionCall extends ASTNode {	
	public class Argument {
		public ASTNode argument;
		public String key;
		public int offset;
		public boolean byReference;
	}
	
	private String functionName;	
	
	private int argCount;
	private ArrayList<Argument> arguments;
	
	public FunctionCall(String functionName) {
		super();
		
		this.functionName = functionName;
		this.arguments = new ArrayList<Argument>();
		this.argCount = 0;
	}
	
	/**
	 * @param argument Argument expression
	 * @param key For key-value arguments, key
	 */
	public void addArgument(ASTNode argument, boolean byReference, String key) {
		Argument arg = new Argument();
		arg.argument = argument;
		arg.key = key;
		arg.offset = -1;
		arg.byReference = byReference;
		
		this.arguments.add(arg);
		
		argument.reuseInExpression(this);
	}
	
	public void addArgument(ASTNode argument, boolean byReference) {
		Argument arg = new Argument();
		arg.argument = argument;
		arg.key = null;
		arg.offset = this.argCount++;
		arg.byReference = byReference;
		
		this.arguments.add(arg);
		
		argument.reuseInExpression(this);
	}

	public String getFunctionName() {
		return functionName;
	}

	public int getArgCount() {
		return argCount;
	}

	public ArrayList<Argument> getArguments() {
		return arguments;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("FunctionCall [name=");
		sb.append(functionName);
		
		for(Argument argument : arguments) {
			sb.append(", ");
			
			if(argument.key != null) {
				sb.append(argument.key);
			}
			else {
				sb.append(argument.offset);
			}
			sb.append("=");
			sb.append(argument.argument.toString());
		}
		
		sb.append("]");
		
		return sb.toString();
	}
}
