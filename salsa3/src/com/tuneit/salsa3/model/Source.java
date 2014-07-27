package com.tuneit.salsa3.model;

import javax.persistence.*;

@Entity
public class Source {
	@Id
	@GeneratedValue
	private int id;
	
	private String path;
	private boolean isParsed;
	
	private String parseResult;
	
	@ManyToOne
	private Repository repository;

	public Source() {
		this.path = null;
		this.isParsed = false;
		this.repository = null;
		this.parseResult = "";
	}
	
	public Source(Repository repository, String path) {
		super();
		this.path = path;
		this.repository = repository;
		this.parseResult = "";
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isParsed() {
		return isParsed;
	}

	public void setParsed(boolean isParsed) {
		this.isParsed = isParsed;
	}

	public int getId() {
		return id;
	}

	public Repository getRepository() {
		return repository;
	}

	public String getParseResult() {
		return parseResult;
	}

	public void setParseResult(String parseResult) {
		this.parseResult = parseResult;
	}
}
