package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;

public class StaticClassMember extends ASTNode {
	private List<String> classNames;
	private String member;
	
	public StaticClassMember(String member) {
		this.member = member;
		this.classNames = new ArrayList<String>();
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
}
