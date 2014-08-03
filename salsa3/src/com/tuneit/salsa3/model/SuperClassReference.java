package com.tuneit.salsa3.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ElementCollection;
import javax.persistence.ManyToOne;

import com.tuneit.salsa3.ast.ClassDeclaration.SuperClassModifier;

@Entity
public class SuperClassReference {
	@Id
	@GeneratedValue
	private int id;
	
	private String superClassName;	
	
	@ManyToOne
	private ClassDeclaration classDeclaration;
	
	@ElementCollection
	private List<SuperClassModifier> modifiers;
	
	public SuperClassReference() {
		this(null, null, 
			 Arrays.asList(SuperClassModifier.SC_EXTENDS_PUBLIC));
	}
	
	public SuperClassReference(ClassDeclaration classDeclaration, String superClassName,
			 List<SuperClassModifier> modifiers) {
		super();
		
		this.classDeclaration = classDeclaration;
		this.superClassName = superClassName;
		this.modifiers = modifiers;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public List<SuperClassModifier> getModifiers() {
		return modifiers;
	}

	public void setModifiers(List<SuperClassModifier> modifiers) {
		this.modifiers = modifiers;
	}

	public int getId() {
		return id;
	}
	
	public ClassDeclaration getClassDeclaration() {
		return classDeclaration;
	}
}
