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
		
		public Argument(ASTNode argument, String key) {
			this.argument = argument;
			this.key = key;
			this.offset = -1;
		}
		
		public Argument(ASTNode argument, Integer offset) {
			this.argument = argument;
			this.key = null;
			this.offset = offset;
		}
		
		@Override
		public ASTNode clone() {
			if(offset == -1) {
				return new Argument(argument, key);
			}
			
			return new Argument(argument, offset);
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
		
		/* Serialization code */
		static {
			ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Argument.class);
			plan.addNodeParam(0, "argument", false);
			plan.addIntegerParam(1, "offset", true);
			plan.addStringParam(1, "key", true);
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
		this.argCount = arguments.size();
	}
	
	@Override
	public ASTNode clone() {
		/* XXX: does deep cloning needed here? */
		
		return new FunctionCall(function, arguments);
	}
	
	/**
	 * @param argument Argument expression
	 * @param key For key-value arguments, key
	 */
	public void addArgument(ASTNode argument, String key) {
		Argument arg = new Argument(argument, key);
		
		this.arguments.add(arg);
		
		argument.reuseInExpression(this);
	}
	
	public void addArgument(ASTNode argument) {
		Argument arg = new Argument(argument, this.argCount++);
		
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
		plan.addNodeParam(0, "function", false);
		plan.addNodeListParam(1, "arguments", false);
	}
}
