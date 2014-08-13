package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;
import com.tuneit.salsa3.ast.serdes.annotations.EnumParameter;


/**
 * <strong>UnaryOperation</strong> is an AST node 
 * <ul>
 *   <li> expression -- 
 *   <li> type -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class UnaryOperation extends ASTNode implements Cloneable {
	public enum Type {
		/* Bit operations */
		UOP_BIT_NOT,
		
		/* Logical operations */
		UOP_LOGICAL_NOT,
		
		/* Basic arithmetics & shifts */
		UOP_PLUS,
		UOP_MINUS,
		UOP_PRE_DECREMENT,
		UOP_POST_DECREMENT,
		UOP_PRE_INCREMENT,
		UOP_POST_INCREMENT
	};
	

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode expression;
	

	@Parameter(offset = 0, optional = false)
	@EnumParameter(enumClass = Type.class)
	private Type type;
	
	public UnaryOperation(Type type, ASTNode expression) {
		super();
		this.expression = expression;
		this.type = type;
		
		expression.reuseInExpression(this);
	}
	
	public ASTNode clone() {
		return new UnaryOperation(type, expression);
	}

	public ASTNode getExpression() {
		return expression;
	}

	public Type getType() {
		return type;
	}

	
}
