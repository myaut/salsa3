package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;



/**
 * <strong>UnusedVariable</strong> is an AST  
 * 
 * @author Sergey Klyaus
 */
public class UnusedVariable extends Variable {
	public UnusedVariable() {
		super("_");
	}
	
	public ASTNode clone() {
		return new UnusedVariable();
	}
	
}
