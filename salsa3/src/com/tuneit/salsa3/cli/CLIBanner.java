package com.tuneit.salsa3.cli;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public final class CLIBanner implements BannerProvider {
	@Override
	public String getProviderName() {
		return "SALSA3 CLI";
	}

	@Override
	public String getBanner() {
		return " ____    _    _     ____    _    _____ \n" 
			 + "/ ___|  / \\  | |   / ___|  / \\  |___ / \n"
			 +  "\\___ \\ / _ \\ | |   \\___ \\ / _ \\   |_ \\ \n"
			 + " ___) / ___ \\| |___ ___) / ___ \\ ___) |\n"
			 + "|____/_/   \\_\\_____|____/_/   \\_\\____/ \n";
	}

	@Override
	public String getVersion() {
		return "0.1";
	}

	@Override
	public String getWelcomeMessage() {
		return "Welcome to SALSA3 CLI!";
	}

}
