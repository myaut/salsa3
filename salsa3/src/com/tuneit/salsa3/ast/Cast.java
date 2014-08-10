package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>Cast</strong> is an AST node 
 * <ul>
 *   <li> type -- 
 *   <li> expression -- 
 *   <li> castType -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class Cast extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private TypeName type;

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode expression;

	@Parameter(offset = 2, optional = true)
	private String castType;
	
	public Cast(ASTNode type, ASTNode expression) {
		this(type, expression, null);
	}
	
	public Cast(ASTNode type, ASTNode expression, String castType) {
		super();
		this.type = (TypeName) type;
		this.expression = expression;
		this.castType = castType;
		
		type.reuseInExpression(this);
		expression.reuseInExpression(this);
	}
	
	public Cast clone() {
		return new Cast(type, expression, castType);
	}
	
	public TypeName getType() {
		return type;
	}
	
	public ASTNode getExpression() {
		return expression;
	}
	
	public String getCastType() {
		return castType;
	}
	
	
}
