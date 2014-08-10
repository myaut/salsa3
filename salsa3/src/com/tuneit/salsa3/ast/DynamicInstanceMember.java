package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>DynamicInstanceMember</strong> is an AST node 
 * <ul>
 *   <li> instance -- 
 *   <li> memberExpression -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class DynamicInstanceMember extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode instance;

	@Parameter(offset = 1, optional = false)
	@NodeParameter
	private ASTNode memberExpression;
	
	public DynamicInstanceMember(ASTNode instance, ASTNode memberExpression) {
		super();
		this.instance = instance;
		this.memberExpression = memberExpression;
		
		instance.reuseInExpression(this);
		memberExpression.reuseInExpression(this);
	}
	
	public ASTNode clone() {
		return new DynamicInstanceMember(instance, memberExpression);
	}

	public ASTNode getInstance() {
		return instance;
	}

	public ASTNode getMemberExpression() {
		return memberExpression;
	}
	
}
