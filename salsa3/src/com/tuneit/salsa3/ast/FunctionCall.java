package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class FunctionCall extends ASTNode {	
	public static class Argument extends ASTNode {
		public ASTNode argument;
		public String key;
		public int offset;
		public boolean byReference;
		
		public Argument(ASTNode argument, Boolean byReference, String key) {
			this.argument = argument;
			this.key = key;
			this.offset = -1;
			this.byReference = byReference;
		}
		
		public Argument(ASTNode argument, Boolean byReference, Integer offset) {
			this.argument = argument;
			this.key = null;
			this.offset = offset;
			this.byReference = byReference;
		}

		public ASTNode getArgument() {
			return argument;
		}

		public String getKey() {
			return key;
		}

		public Integer getOffset() {
			return offset;
		}

		public Boolean getByReference() {
			return byReference;
		}
		
		/* Serialization code */
		static {
			ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Argument.class);
			plan.addNodeParam(0, "argument", false);
			plan.addBooleanParam(1, "byReference", false);
			plan.addIntegerParam(2, "offset", false);
			plan.addStringParam(2, "key", false);
		}
	}
	
	private ASTNode function;	
	
	private int argCount;
	private List<Argument> arguments;
	
	public FunctionCall(ASTNode function) {
		super();
		
		this.function = function;
		this.arguments = new ArrayList<Argument>();
		this.argCount = 0;
	}
	
	public FunctionCall(ASTNode function, List<Argument> arguments) {
		super();
		
		this.function = function;
		this.arguments = arguments;
		this.argCount = 0;
	}
	
	/**
	 * @param argument Argument expression
	 * @param key For key-value arguments, key
	 */
	public void addArgument(ASTNode argument, boolean byReference, String key) {
		Argument arg = new Argument(argument, byReference, key);
		
		this.arguments.add(arg);
		
		argument.reuseInExpression(this);
	}
	
	public void addArgument(ASTNode argument, boolean byReference) {
		Argument arg = new Argument(argument, byReference, this.argCount++);
		
		this.arguments.add(arg);
		
		argument.reuseInExpression(this);
	}

	public ASTNode getFunction() {
		return function;
	}

	public int getArgCount() {
		return argCount;
	}

	public List<Argument> getArguments() {
		return arguments;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("FunctionCall [name=");
		sb.append(function.toString());
		
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
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(FunctionCall.class);
		plan.addStringParam(0, "function", false);
		plan.addNodeListParam(0, "arguments", false);
	}
}
