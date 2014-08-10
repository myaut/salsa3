package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>DoWhileStatement</strong> is an AST compound statement 
 * <ul>
 *   <li> condition -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class DoWhileStatement extends ASTStatement {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode condition;
	
	public DoWhileStatement() {
		this.condition = null;
	}
	
	public DoWhileStatement(ASTNode condition) {
		this.condition = condition;
		
		condition.reuseInExpression(this);
	}
	
	public ASTNode getCondition() {
		return condition;
	}
	
	public void setCondition(ASTNode condition) {
		this.condition = condition;
	}
	
	
}
