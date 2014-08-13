package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>ConditionalExpression</strong> is an AST node 
 * <ul>
 *   <li> condition -- 
 *   <li> expressionTrue -- 
 *   <li> expressionFalse -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ConditionalExpression extends ASTNode {	

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode condition;

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode expressionTrue;

	@Parameter(offset = 2, optional = false)
	@NodeParameter
	private ASTNode expressionFalse;
	
	public ConditionalExpression(ASTNode condition, ASTNode expressionTrue,
			ASTNode expressionFalse) {
		super();
		this.condition = condition;
		this.expressionTrue = expressionTrue;
		this.expressionFalse = expressionFalse;
		
		condition.reuseInExpression(this);
		expressionTrue.reuseInExpression(this);
		expressionFalse.reuseInExpression(this);
	}
	
	public ConditionalExpression(ASTNode condition) {
		super();
		this.condition = condition;
		this.expressionTrue = null;
		this.expressionFalse = null;
	}
	
	public ASTNode clone() {
		return new ConditionalExpression(condition, expressionTrue, expressionFalse);
	}

	public ASTNode getExpressionTrue() {
		return expressionTrue;
	}

	public void setExpressionTrue(ASTNode expressionTrue) {
		this.expressionTrue = expressionTrue;
	}

	public ASTNode getExpressionFalse() {
		return expressionFalse;
	}

	public void setExpressionFalse(ASTNode expressionFalse) {
		this.expressionFalse = expressionFalse;
	}

	public ASTNode getCondition() {
		return condition;
	}
	
}
