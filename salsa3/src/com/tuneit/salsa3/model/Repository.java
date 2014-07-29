package com.tuneit.salsa3.model;

import javax.persistence.*;

import java.util.List;

@Entity
public class Repository {
	@Id
	@GeneratedValue
	private int id;
	
	@Column(unique = true)
	private String repositoryName;
	
	private Language language;
	private String path;
	
	public Repository() {
		this.path = null;
		this.repositoryName = null;
		this.language = null;
	}
	
	public Repository(String repositoryName, String path, Language language) {
		this.path = path;
		this.repositoryName = repositoryName;
		this.language = language;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public int getId() {
		return id;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public enum Language {
		LANG_C,
		LANG_CXX,
		LANG_PHP,
		LANG_JAVA;
		
		public String toString() {
			return super.toString().substring(5);
		}
	}
}