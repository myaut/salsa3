package com.tuneit.salsa3.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SourceReference {
	@Id
	@GeneratedValue
	private int id;
	
	private Source source;
	
	private int startLine;
	private int startCharacter;
	private int startOffset;
	
	private int endLine;
	private int endCharacter;
	private int endOffset;
	
	public SourceReference() {
		this(null, 0, 0, 0, 0, 0, 0);
	}
	
	public SourceReference(Source source, int startLine, int startCharacter,
			int startOffset, int endLine, int endCharacter, int endOffset) {
		super();
		
		this.source = source;
		
		this.startLine = startLine;
		this.startCharacter = startCharacter;
		this.startOffset = startOffset;
		
		this.endLine = endLine;
		this.endCharacter = endCharacter;
		this.endOffset = endOffset;
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getStartCharacter() {
		return startCharacter;
	}

	public void setStartCharacter(int startCharacter) {
		this.startCharacter = startCharacter;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public int getEndCharacter() {
		return endCharacter;
	}

	public void setEndCharacter(int endCharacter) {
		this.endCharacter = endCharacter;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public int getId() {
		return id;
	}

	public Source getSource() {
		return source;
	}
}
