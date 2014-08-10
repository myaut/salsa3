package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;



/**
 * <strong>VariableThis</strong> is an AST  
 * 
 * @author Sergey Klyaus
 */
public class VariableThis extends Variable {
	public VariableThis() {
		super("this");
	}

}
