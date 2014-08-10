package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.ListParameter;
import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>FunctionCall</strong> is an AST node 
 * <ul>
 *   <li> function -- 
 *   <li> argCount -- 
 *   <li> arguments -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class FunctionCall extends ASTNode {	
	
	/**
	 * <strong>Argument</strong> is an AST node 
	 * <ul>
	 *   <li> argument -- 
	 *   <li> key -- 
	 *   <li> offset -- 
	 * </ul>
	 * 
	 * @author Sergey Klyaus
	 */
	public static class Argument extends ASTNode {

		@Parameter(offset = 0, optional = false)
		@NodeParameter
		public ASTNode argument;

		@Parameter(offset = 1, optional = true)
		public String key;

		@Parameter(offset = 1, optional = true)
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
		
	}
	

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode function;	
	
	private int argCount;

	@Parameter(offset = 1, optional = false)
	@ListParameter
	@NodeParameter
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
	
		
		
			
		
		
	
}
