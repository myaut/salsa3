package com.tuneit.salsa3.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class SourceSnippet {
	@Id
	@GeneratedValue
	private int id;
	
	private Type type;
	private String name; 
	
	private SourceReference sourceReference;
	
	@Lob
	private String ast;
	
	public enum Type {
		SS_FUNCTION,
		SS_VARIABLE_OR_CONSTANT,
		SS_DIRECTIVE
	}

	public SourceSnippet(Type type, String name,
			SourceReference sourceReference, String ast) {
		super();
		
		this.type = type;
		this.name = name;
		this.sourceReference = sourceReference;
		this.ast = ast;
	}
	
	public SourceSnippet() {
		this(Type.SS_DIRECTIVE, null, null, null);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SourceReference getSourceReference() {
		return sourceReference;
	}

	public void setSourceReference(SourceReference sourceReference) {
		this.sourceReference = sourceReference;
	}

	public String getAst() {
		return ast;
	}

	public void setAst(String ast) {
		this.ast = ast;
	}

	public int getId() {
		return id;
	}
}
