package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class StaticClassMember extends ASTNode {
	private List<String> classNames;
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("StaticClassMember[");
		
		for(String className : classNames) {
			sb.append(className);
			sb.append("::");
		}
		
		sb.append(member.toString());
		
		sb.append("]");
		
		return sb.toString();
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(StaticClassMember.class);
		plan.addStringParam(0, "member", false);
		plan.addStringListParam(1, "classNames", false);
	}
}
