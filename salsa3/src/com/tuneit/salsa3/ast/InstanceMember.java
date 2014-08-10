package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>InstanceMember</strong> is an AST node 
 * <ul>
 *   <li> instance -- 
 *   <li> memberName -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class InstanceMember extends ASTNode {

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode instance;

	@Parameter(offset = 1, optional = false)
	private String memberName;
	
	public InstanceMember(ASTNode instance, String memberName) {
		super();
		this.instance = instance;
		this.memberName = memberName;
		
		instance.reuseInExpression(this);
	}
	
	public ASTNode clone() {
		return new InstanceMember(instance, memberName);
	}

	public ASTNode getInstance() {
		return instance;
	}

	public String getMemberName() {
		return memberName;
	}
	
}
