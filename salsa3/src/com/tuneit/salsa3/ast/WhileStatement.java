package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>WhileStatement</strong> is an AST compound statement 
 * <ul>
 *   <li> condition -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class WhileStatement extends ASTStatement {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode condition;
	
	public WhileStatement(ASTNode condition) {
		this.condition = condition;
		
		condition.reuseInExpression(this);
	}
	
	public ASTNode getCondition() {
		return condition;
	}
	
	
}
