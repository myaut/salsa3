package com.tuneit.salsa3.cli;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CLIPrompt implements PromptProvider {

	@Override
	public String getProviderName() {
		return "SALSA3 CLI";
	}

	@Override
	public String getPrompt() {
		return "salsa3-cli> ";
	}

}
