package com.tuneit.salsa3.cli;

import java.util.List;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.tuneit.salsa3.Task;
import com.tuneit.salsa3.TaskManager;
import com.tuneit.salsa3.TestTask;

@Component
public class CLITasks implements CommandMarker {
	@CliCommand(value = "task list", help = "List tasks that are currently executing") 
	public String list() {
		TableView tv = new TableView();
		TaskManager tm = TaskManager.getInstance();
		List<Task> tasks = tm.getTasks();
		
		tv.newRow()
			.append("ID")
			.append("STATE")
			.append("TASK")
			.append("\n\tEXCEPTION");
		
		for(Task task : tasks) {
			Throwable throwable = task.getThrowable();
			String excInfo = 
					(throwable == null)
						? "" 
						: throwable.toString();
			
			tv.newRow()
				.append(task.getTaskId())
				.append(task.getState().toString())
				.append(task.getDescription())
				.append("\n\t" + excInfo);
		}
		
		return tv.toString();
	}
	
	@CliCommand(value = "task set", help = "Tune TaskManager parameters")
	public String set(
			@CliOption(key = {"core-size"}, mandatory = false, help = "Default number of threads") 
				final Integer coreSize,
			@CliOption(key = {"max-size"}, mandatory = false, help = "Maximum number of threads") 
				final Integer maxSize,
			@CliOption(key = {"keep-alive"}, mandatory = false, help = "Keep alive time (in milliseconds)") 
				final Long keepAliveTime) {	
		StringBuilder sb = new StringBuilder();
		
		if(coreSize != null) {
			TaskManager.setCorePoolSize(coreSize);
		}
		
		if(maxSize != null) {
			TaskManager.setMaximumPoolSize(maxSize);
		}
		
		if(keepAliveTime != null) {
			TaskManager.setKeepAliveTime(keepAliveTime);
		}
		
		sb.append("Core size:       "); sb.append(TaskManager.getCorePoolSize()); 
			sb.append("\n");
		sb.append("Maximum size:    "); sb.append(TaskManager.getMaximumPoolSize());
			sb.append("\n");
		sb.append("Keep alive time: "); sb.append(TaskManager.getKeepAliveTime());
			sb.append("\n");
		
		return sb.toString();
	}
	
	@CliCommand(value = "task stop", help = "Stop execution of task")
	public String stop(
			@CliOption(key = {"id"}, mandatory = true, help = "Id of a task") 
			final Integer id) {
		TaskManager tm = TaskManager.getInstance();
		
		tm.stopTask(id);
			
		return "Task " + id + " is successfully stopped";
	}
	
	@CliCommand(value = "task test", help = "Submit a test task") 
	public String test() {
		TestTask task = new TestTask();
		TaskManager tm = TaskManager.getInstance();
		
		tm.addTask(task);
			
		return "Test task " + task.getTaskId() + " is successfully submitted";
	}
}
