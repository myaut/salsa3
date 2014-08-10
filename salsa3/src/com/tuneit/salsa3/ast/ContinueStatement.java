package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.Parameter;
import com.tuneit.salsa3.ast.serdes.annotations.DefaultIntegerValue;


/**
 * <strong>ContinueStatement</strong> is an AST node 
 * <ul>
 *   <li> label -- 
 *   <li> continueNesting -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ContinueStatement extends ASTNode {

	@Parameter(offset = 0, optional = true)
	private String label;

	@Parameter(offset = 0, optional = true)
	@DefaultIntegerValue(value = 1)
	private int continueNesting;
	
	public ContinueStatement() {
		this.label = null;
		this.continueNesting = 1;
	}
	
	public ContinueStatement(int continueNesting) {
		this.label = null;
		this.continueNesting = continueNesting;
	}
	
	public ContinueStatement(String label) {
		this.label = label;
		this.continueNesting = 1;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public int getContinueNesting() {
		return this.continueNesting;
	}
	
		
	
}
