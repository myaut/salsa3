package com.tuneit.salsa3.model;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.tuneit.salsa3.ast.ClassDeclaration.Type;

@Entity
public class ClassDeclaration {
	@Id
	@GeneratedValue
	private int id;
	
	private String className;
	
	private Type classType;	
	private SourceReference sourceReference;
	
	@OneToMany(mappedBy = "classDeclaration")
	private List<SuperClassReference> superClasses;

	public ClassDeclaration() {
		this(null, null, Type.CLASS_NORMAL);
	}

	public ClassDeclaration(SourceReference sourceReference, String className, Type classType) {
		super();
		
		
		this.className = className;
		this.classType = classType;
		this.sourceReference = sourceReference;
		this.superClasses = new ArrayList<SuperClassReference>();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Type getClassType() {
		return classType;
	}

	public void setClassType(Type classType) {
		this.classType = classType;
	}

	public SourceReference getSourceReference() {
		return sourceReference;
	}

	public void setSourceReference(SourceReference sourceReference) {
		this.sourceReference = sourceReference;
	}

	public int getId() {
		return id;
	}

	public List<SuperClassReference> getSuperClasses() {
		return superClasses;
	}
}
