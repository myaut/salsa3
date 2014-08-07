package com.tuneit.salsa3.cli;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.stereotype.Component;

import com.tuneit.salsa3.RepositoryManager;
import com.tuneit.salsa3.RepositoryWalkTask;
import com.tuneit.salsa3.TaskManager;
import com.tuneit.salsa3.model.Repository;
import com.tuneit.salsa3.model.Source;

@Component
public final class CLIRepository implements CommandMarker {
	@Autowired
	private CLIRepositoryHolder holder;
	
	@CliAvailabilityIndicator({"walk", "sources", "reset"})
	public boolean isRepositorySelected() {
		return holder.getRepository() != null;
	}
	
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
	
	@CliCommand(value = "select", help = "Select repository or source. " 
				+ "If called without options, deselects repository."
				+ "If --repository do not have argument, deselects source")
	public String select(
			@CliOption(key = {"repository"}, mandatory = false, help = "Repository", 
					specifiedDefaultValue = "__REPOSITORY__") 
				final String name,
			@CliOption(key = {"path"}, mandatory = false, help = "Path to the source") 
				final String path,
			@CliOption(key = {"id"}, mandatory = false, help = "ID of source file") 
				final Integer id) {	
		if(name == null && path == null && id == null) {
			holder.setRepository(null);
			holder.setSource(null);
			return "";
		}
		
		RepositoryManager rm = RepositoryManager.getInstance();
		Repository repository = holder.getRepository();
		
		boolean doChangeRepository = false;
		
		if(name != null) {
			if(name.equals("__REPOSITORY__")) {
				if(repository == null) {
					throw new IllegalArgumentException("Couldn't deselect source, when no repository is selected. " + 
							"Add argument to --repository option");
				}
				else {
					holder.setSource(null);
					return "";
				}
			}
			else {
				doChangeRepository = true;
			}
		}
		
		if(repository == null) {
			if(name == null && (path != null || id != null)) { 
				throw new IllegalArgumentException("Couldn't select source, when no repository is selected." + 
							"Add --repository option");
			}
			else {
				doChangeRepository = true;				
			}
		}
		
		if(doChangeRepository) {
			repository = rm.getRepositoryByName(name);
			
			if(repository == null) {
				throw new IllegalArgumentException("Repository '" + name + "' is not found!");
			}
			
			holder.setRepository(repository);
		}
		
		Source source = null;
		
		if(path != null) {
			source = rm.getSourceByPath(repository, path);
			
			if(source == null) {
				throw new IllegalArgumentException("Source '" + path + "' is not found in repository '" + 
								repository.getRepositoryName() + "'!");
			}
			
		}
		else if(id != null) {
			source = rm.getSourceById(repository, id);
			
			if(source == null) {
				throw new IllegalArgumentException("Source #" + id + " is not found in repository '" + 
								repository.getRepositoryName() + "'!");
			}
		}
		
		holder.setSource(source);
		
		return "";
	}
	
	@CliCommand(value = "walk", help = "Walk over repository and add sources") 
	public String walk() {
		TaskManager tm = TaskManager.getInstance();
		Repository repository = holder.getRepository();
		
		tm.addTask(new RepositoryWalkTask(repository));
		
		return "Walking repository '" + repository.getRepositoryName() + "' is started.\nUse task list to monitor activity";
	}
	
	@CliCommand(value = "reset", help = "Reset source or repository to non-parsed state")
	public String reset() {
		RepositoryManager rm = RepositoryManager.getInstance();
		StringBuilder sb = new StringBuilder();
		
		Repository repository = holder.getRepository();
		Source singleSource = holder.getSource();
		List<Source> sources;
		
		if(singleSource == null) {
			sources = rm.getSources(repository);
		}
		else {
			sources = new ArrayList<Source>();
			sources.add(singleSource);
		}
		
		sb.append("Resetting sources: ");
		
		for(Source source : sources) {
			if(source.isParsed()) {
				sb.append(source.getPath());
				sb.append(", ");
				
				/* TODO: Do this exception-safe */
				rm.resetSource(source);
			}
		}
		
		return sb.toString();
	}
	
	@CliCommand(value = "sources", help = "Show repository sources") 
	public String sources(
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
		
		Repository repository = holder.getRepository();
		
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
		if(holder.getRepository() != null 
				&& holder.getRepository().getRepositoryName().equals(name)) {
			holder.setRepository(null);
		}
		
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
