package com.tuneit.salsa3.cli;

import java.util.List;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.tuneit.salsa3.RepositoryManager;
import com.tuneit.salsa3.RepositoryWalkTask;
import com.tuneit.salsa3.TaskManager;
import com.tuneit.salsa3.model.Repository;
import com.tuneit.salsa3.model.Source;

@Component
public final class CLIRepository implements CommandMarker {
	@CliCommand(value = "repo create", help = "Create a new SALSA3 repository") 
	public String create(
			@CliOption(key = {"name"}, mandatory = true, help = "Name of repository") 
				final String name,
			@CliOption(key = {"path"}, mandatory = true, help = "Path to repository sources") 
				final String path,
			@CliOption(key = {"lang"}, mandatory = true, help = "Source file language")
				final Language language) {
		RepositoryManager rm = RepositoryManager.getInstance();
		
		rm.createRepository(name, path, language.getLanguage());
		
		return "Repository " + name + " is successfully created";
	}
	
	@CliCommand(value = "repo list", help = "Print list of repositories") 
	public String list() {
		TableView tv = new TableView();
		RepositoryManager rm = RepositoryManager.getInstance();
		List<Repository> repositories = rm.getRepositories();
		
		tv.newRow()
			.append("ID")
			.append("LANG")
			.append("REPOSITORY")
			.append("PATH");
		
		for(Repository repo : repositories) {
			tv.newRow()
				.append(repo.getId())
				.append(repo.getLanguage().toString())
				.append(repo.getRepositoryName())
				.append(repo.getPath());
		}
		
		return tv.toString();
	}
	
	@CliCommand(value = "repo walk", help = "Walk over repository and add sources") 
	public String walk(
		@CliOption(key = {"name"}, mandatory = true, help = "Name of repository") 
			final String name) {
		TaskManager tm = TaskManager.getInstance();
		RepositoryManager rm = RepositoryManager.getInstance();
		
		Repository repository = rm.getRepositoryByName(name);
		
		if(repository == null) {
			throw new IllegalArgumentException("No such repository '" + name + "'!");
		}
		
		tm.addTask(new RepositoryWalkTask(repository));
		
		return "Walking repository '" + name + "' is started.\nUse task list to monitor activity";
	}
	
	@CliCommand(value = "repo sources", help = "Show repository sources") 
	public String sources(
			@CliOption(key = {"name"}, mandatory = true, help = "Name of repository") 
				final String name,
			@CliOption(key = {"parsed"}, mandatory = false, help = "Show only parsed sources",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean parsed,
			@CliOption(key = {"not-parsed"}, mandatory = false, help = "Show only sources that are not parsed",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false")
				final Boolean notParsed,
			@CliOption(key = {"failed"}, mandatory = false, help = "Show only failed sources",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean failed) {
		
		if((parsed && notParsed) || (parsed && failed) || (failed && notParsed)) {
			throw new IllegalArgumentException("--parsed, --not-parsed and --failed switches are mutually exclusive");
		}
		
		TableView tv = new TableView();
		RepositoryManager rm = RepositoryManager.getInstance();
		
		Repository repository = rm.getRepositoryByName(name);
		
		if(repository == null) {
			throw new IllegalArgumentException("No such repository '" + name + "'!");
		}
		
		tv.newRow()
			.append("ID")
			.append(" ")
			.append("PATH")
			.append("\n\tERROR");
		
		for(Source source : rm.getSources(repository)) {
			boolean parseFailed = source.getParseResult() != null && source.getParseResult().length() > 0;
			
			if(parsed && !source.isParsed())
				continue;				
			if(notParsed && source.isParsed())
				continue;
			if(failed && !parseFailed)
				continue;
			
			TableView.Row row = tv.newRow();
						
			row
				.append(source.getId())
				.append(source.isParsed() ? "+" : " ")
				.append(source.getPath());
			
			if(parseFailed) {
				row.append("\n\t" + source.getParseResult());
			}
		}
		
		return tv.toString();
	}
	
	@CliCommand(value = "repo delete", help = "Delete SALSA3 repository") 
	public String delete(
			@CliOption(key = {"name"}, mandatory = true, help = "Name of repository") 
				final String name) {
		RepositoryManager rm = RepositoryManager.getInstance();
		
		rm.deleteRepository(name);
		
		return "Repository " + name + " is successfully deleted";
	}
	
	enum Language {
		c(Repository.Language.LANG_C),
		cxx(Repository.Language.LANG_CXX),
		php(Repository.Language.LANG_PHP),
		java(Repository.Language.LANG_JAVA);
		
		private Repository.Language language;
		
		private Language(Repository.Language language) {
			this.language = language;
		}
		
		public Repository.Language getLanguage() {
			return language;
		}
	}
}
