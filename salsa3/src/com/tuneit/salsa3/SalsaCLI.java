package com.tuneit.salsa3;

import java.io.IOException;

public final class SalsaCLI {
	public static void main(String[] args) {
		try {
			org.springframework.shell.Bootstrap.main(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
