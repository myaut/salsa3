package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.Parameter;
import com.tuneit.salsa3.ast.serdes.annotations.DefaultIntegerValue;


/**
 * <strong>BreakStatement</strong> is an AST node 
 * <ul>
 *   <li> label -- 
 *   <li> breakNesting -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class BreakStatement extends ASTNode {

	@Parameter(offset = 0, optional = true)
	private String label;

	@Parameter(offset = 0, optional = true)
	@DefaultIntegerValue(value = 1)
	private int breakNesting;
	
	public BreakStatement() {
		this.label = null;
		this.breakNesting = 1;
	}
	
	public BreakStatement(Integer breakNesting) {
		this.label = null;
		this.breakNesting = breakNesting;
	}
	
	public BreakStatement(String label) {
		this.label = label;
		this.breakNesting = 1;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public int getBreakNesting() {
		return this.breakNesting;
	}
	
		
	
}
