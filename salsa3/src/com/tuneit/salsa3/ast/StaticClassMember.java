package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.ListParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>StaticClassMember</strong> is an AST node 
 * <ul>
 *   <li> classNames -- 
 *   <li> member -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class StaticClassMember extends ASTNode {

	@Parameter(offset = 1, optional = false)
	@ListParameter
	private List<String> classNames;

	@Parameter(offset = 0, optional = false)
	private String member;
	
	public StaticClassMember(String member) {
		this.member = member;
		this.classNames = new ArrayList<String>();
	}
	
	public StaticClassMember(String member, List<String> classNames) {
		this.member = member;
		this.classNames = classNames;
	}
	
	public ASTNode clone() {
		return new StaticClassMember(member, classNames);
	}
	
	public String getMember() {
		return member;
	}
	
	public void addClassName(String className) {
		this.classNames.add(className);
	}
	
	public List<String> getClassNames() {
		return this.classNames;
	}
	
		
		
		
		
		
	
}
