package com.tuneit.salsa3;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class SalsaCLI {
	public static void main(String[] args) {
		try {
			org.springframework.shell.Bootstrap.main(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
