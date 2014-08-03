package com.tuneit.salsa3.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

import com.tuneit.salsa3.model.Repository;
import com.tuneit.salsa3.model.Source;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CLIPrompt implements PromptProvider {
	@Autowired
	private CLIRepositoryHolder holder;
	
	@Override
	public String getProviderName() {
		return "SALSA3 CLI";
	}

	@Override
	public String getPrompt() {
		Repository repository = holder.getRepository(); 
		Source source = holder.getSource();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("salsa3-cli");
		
		if(repository != null) {
			sb.append(" ");
			sb.append(repository.getRepositoryName());
			
			if(source != null) {
				sb.append(":/");
				sb.append(source.getPath());
			}
		}
		
		sb.append("> ");
		return sb.toString();
	}

}
