package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>ForeachStatement</strong> is an AST compound statement 
 * <ul>
 *   <li> iterable -- 
 *   <li> key -- 
 *   <li> value -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ForeachStatement extends ASTStatement {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode iterable;

	@Parameter(offset = 2, optional = true)
	@NodeParameter
	private ASTNode key;

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode value;
	
	public ForeachStatement(ASTNode iterable) {
		this(iterable, null, null);
	}
	
	public ForeachStatement(ASTNode iterable, ASTNode value) {
		this(iterable, value, null);
	}
	
	public ForeachStatement(ASTNode iterable, ASTNode value, ASTNode key) {
		this.iterable = iterable;
		this.key = key;
		this.value = value;
		
		iterable.reuseInExpression(this);		
		if(value != null) {
			value.reuseInExpression(this);
		}
		if(key != null) {
			key.reuseInExpression(this);
		}
	}

	public ASTNode getKey() {
		return key;
	}

	public void setKey(ASTNode key) {
		this.key = key;
	}

	public ASTNode getValue() {
		return value;
	}

	public void setValue(ASTNode value) {
		this.value = value;
	}

	public ASTNode getIterable() {
		return iterable;
	}

	
}

