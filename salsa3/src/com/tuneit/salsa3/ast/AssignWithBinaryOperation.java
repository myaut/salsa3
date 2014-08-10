package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.BinaryOperation.Type;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;



/**
 * <strong>AssignWithBinaryOperation</strong> is an AST  
 * 
 * @author Sergey Klyaus
 */
public class AssignWithBinaryOperation extends Assign {
	private BinaryOperation.Type type;
	
	public AssignWithBinaryOperation(BinaryOperation.Type type, ASTNode left, ASTNode right) {
		super(left, right);
		this.type = type;
	}

	public BinaryOperation.Type getType() {
		return type;
	}
	
	
}
