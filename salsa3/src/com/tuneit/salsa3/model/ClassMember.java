package com.tuneit.salsa3.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ElementCollection;

import com.tuneit.salsa3.ast.ClassDeclaration.MemberModifier;

@Entity
public class ClassMember {
	@Id
	@GeneratedValue
	private int id;
	
	private ClassDeclaration klass;
	private SourceSnippet code;
	
	@ElementCollection
	private List<MemberModifier> modifiers;
	
	public ClassMember(ClassDeclaration klass, SourceSnippet code) {
		super();
		
		this.klass = klass;
		this.code = code;
		this.modifiers = new ArrayList<MemberModifier>();
	}
	
	public ClassMember() {
		this(null, null);
	}

	public ClassDeclaration getKlass() {
		return klass;
	}

	public void setKlass(ClassDeclaration klass) {
		this.klass = klass;
	}

	public SourceSnippet getCode() {
		return code;
	}

	public void setCode(SourceSnippet code) {
		this.code = code;
	}

	public int getId() {
		return id;
	}

	public List<MemberModifier> getModifiers() {
		return modifiers;
	}

	public void setModifiers(List<MemberModifier> modifiers) {
		this.modifiers = modifiers;
	}
}
