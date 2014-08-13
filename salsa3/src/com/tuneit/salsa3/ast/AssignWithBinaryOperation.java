package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.EnumParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;

/**
 * <strong>AssignWithBinaryOperation</strong> is an AST  
 * 
 * @author Sergey Klyaus
 */
public class AssignWithBinaryOperation extends Assign {
	@Parameter(offset = 2, optional = false)
	@EnumParameter(enumClass = BinaryOperation.Type.class)
	private BinaryOperation.Type type;
	
	public AssignWithBinaryOperation(ASTNode left, ASTNode right, BinaryOperation.Type type) {
		super(left, right);
		this.type = type;
	}

	public ASTNode clone() {
		return new AssignWithBinaryOperation(getLeft(), getRight(), type);
	}
	
	public BinaryOperation.Type getType() {
		return type;
	}
}
