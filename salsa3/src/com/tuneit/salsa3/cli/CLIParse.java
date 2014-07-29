package com.tuneit.salsa3.cli;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.tuneit.salsa3.RepositoryManager;
import com.tuneit.salsa3.RepositoryParseTask;
import com.tuneit.salsa3.SourceParseTask;
import com.tuneit.salsa3.TaskManager;
import com.tuneit.salsa3.model.Repository;
import com.tuneit.salsa3.model.Source;

@Component
public class CLIParse implements CommandMarker {
	@CliCommand(value = "parse", help = "Parse a source")
	public String parse(
			@CliOption(key = {"repository"}, mandatory = true, help = "Repository") 
				final String name,
			@CliOption(key = {"path"}, mandatory = false, help = "Path to the source") 
				final String path,
			@CliOption(key = {"id"}, mandatory = false, help = "ID of source file") 
				final Integer id) {
		RepositoryManager rm = RepositoryManager.getInstance();
		TaskManager tm = TaskManager.getInstance();
		
		Repository repository = rm.getRepositoryByName(name);
		
		if(repository == null) {
			throw new IllegalArgumentException("No such repository '" + name + "'!");
		}
		
		if(id != null && path != null) {
			throw new IllegalArgumentException("--id and --path are mutually exclusive");
		}
		
		if(id == null && path == null) {
			RepositoryParseTask task = new RepositoryParseTask(repository);
			tm.addTask(task);
		}
		else if(path == null) {
			Source source = rm.getSourceById(repository, id);
			SourceParseTask task = new SourceParseTask(source);
			tm.addTask(task);
		}
		else if(id == null) {
			Source source = rm.getSourceByPath(repository, path);
			SourceParseTask task = new SourceParseTask(source);
			tm.addTask(task);
		}		
		
		return "Parsing is started.\nUse 'task list' to monitor activities.";
	}
}
