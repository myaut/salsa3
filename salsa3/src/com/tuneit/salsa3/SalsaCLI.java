package com.tuneit.salsa3;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static jline.internal.Log.Level.*;

public final class SalsaCLI {
	private static final String PERSISTENCE_UNIT_NAME = "salsaPU";
	
	private static class JlineLoggerOutputStream extends PrintStream {	
		private static Logger log = Logger.getLogger("jline.internal.Log");
		
		private Level level;
		private StringBuilder sb;
		
		public JlineLoggerOutputStream() {
			super(System.out);
			
			sb = new StringBuilder();
		}

		@Override
		public void flush() {
			log.log(level, sb.toString());
			
			sb = new StringBuilder();
		}

		@Override
		public PrintStream format(String format, Object... args) {
			// Format is used only once - when level is provided
			if(format.equals("[%s] ")) {
				jline.internal.Log.Level jlineLevel = (jline.internal.Log.Level) args[0];
				
				switch(jlineLevel) {
				case INFO:
					level = Level.INFO;
					break;
				case WARN:
					level = Level.WARNING;
					break;
				case ERROR:
					level = Level.SEVERE;
					break;
				case DEBUG:
					level = Level.FINE;
					break;
				case TRACE:
					level = Level.FINER;
					break;
				default:
					level = Level.FINEST;
					break;
				}
			}
			else {
				return super.format(format, args);
			}
			
			return this;
		}

		@Override
		public void print(Object o) {
			sb.append(o);
		}
		
		@Override
		public void print(String s) {
			sb.append(s);
		}

		@Override
		public void println() {
			sb.append('\n');
		}

		@Override
		public void println(Object o) {
			sb.append(o);
			sb.append('\n');
		}
		
		@Override
		public void println(String s) {
			sb.append(s);
			sb.append('\n');
		}
	}
	
	public static void main(String[] args) {
		try {
			/* Override Jline logging */
			jline.internal.Log.setOutput(new JlineLoggerOutputStream());
			
			/* Connect to database in background */
			(new Thread() {
				@Override
				public void run() {
					EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
					emf.createEntityManager();
				}
			}).start();
			
			/* Finally, start Spring Shell! */
			org.springframework.shell.Bootstrap.main(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
