package com.tuneit.salsa3;

import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Logger;

import com.tuneit.salsa3.model.Repository;

public class RepositoryWalkTask extends Task {
	private static Logger log = Logger.getLogger(RepositoryWalkTask.class.getName());
	
	public static class SourceVisitor extends SimpleFileVisitor<Path> {
		private Repository repository;
		private Path path;
		private String[] suffixes;
		private boolean isRootVisitor = false;
		private boolean isStopped = false;
		
		private RepositoryManager rm;
		
		public SourceVisitor(Repository repository, Path path, String[] suffixes) {
			super();
			this.repository = repository;
			this.path = path;
			this.rm = RepositoryManager.getInstance();
			this.suffixes = suffixes;
		}
		
		public void setIsRootVisitor(boolean isRootVisitor) {
			this.isRootVisitor = isRootVisitor;
		}
		
		public synchronized void stop() {
			isStopped = true;
		}
		
		public Repository getRepository() {
			return repository;
		}
		
		public Path getPath() {
			return path;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir,
				BasicFileAttributes attrs) throws IOException {
			if(isStopped)
				return TERMINATE;
			
			if(isRootVisitor) {
				if(dir.equals(Paths.get(repository.getPath()))) {
					return CONTINUE;
				}
				
				log.info("Spawning walk task for repository '" + repository.getRepositoryName() +
						 "' at " + dir.toString());
				
				Task subTask = new RepositoryWalkTask(repository, dir);
				TaskManager tm = TaskManager.getInstance();
				
				tm.addTask(subTask);
				
				return SKIP_SUBTREE;
			}
			
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException {
			if(isStopped)
				return TERMINATE;
			
			if(!attrs.isRegularFile())
				return CONTINUE;
			
			String fileName = file.getFileName().toString();			
			
			for(String suffix : suffixes) {
				if(fileName.endsWith(suffix)) {
					Path fileRelative = 
							Paths.get(repository.getPath()).relativize(file);
					
					rm.addSourceFile(repository, fileRelative.toString());
					
					return CONTINUE;
				}
			}
			
			return CONTINUE;
		}
	}
	
	private static HashMap<Repository.Language, String> suffixesMap = 
			new HashMap<Repository.Language, String>();	
	private static String phpSuffixes=".php,.inc";
	private static String cSuffixes=".h,.c";
	private static String cxxSuffixes=".h,.c,.hpp,.cxx,.cc,.cpp";
	private static String javaSuffixes=".java";
	
	static {
		suffixesMap.put(Repository.Language.LANG_PHP, phpSuffixes);
		suffixesMap.put(Repository.Language.LANG_C, cSuffixes);
		suffixesMap.put(Repository.Language.LANG_CXX, cxxSuffixes);
		suffixesMap.put(Repository.Language.LANG_JAVA, javaSuffixes);
	}
	
	private SourceVisitor sourceVisitor;
	
	public RepositoryWalkTask(Repository repository, Path path) {
		super();
		
		String[] suffixes = suffixesMap.get(repository.getLanguage()).split(",");
		
		if(path == null) {
			path = Paths.get(repository.getPath());
		}
		
		sourceVisitor = new SourceVisitor(repository, path, suffixes);
		
		setDescription("Walking repository '" + repository.getRepositoryName() + 
					   "' path " + path.toString());
	}
		
	public RepositoryWalkTask(Repository repository) {
		this(repository, null);
		
		sourceVisitor.setIsRootVisitor(true);
	}
		
	public void run() {
		EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
		RepositoryManager rm = RepositoryManager.getInstance();
		
		rm.beginSourcesWalk(sourceVisitor.getRepository());
		
		try {
			Files.walkFileTree(sourceVisitor.getPath(), opts,
						Integer.MAX_VALUE, sourceVisitor);
		} catch (IOException e) {			
			rm.finishSourcesWalk(sourceVisitor.getRepository(), true);
			
			throw new IllegalStateException(e);
		}
		
		rm.finishSourcesWalk(sourceVisitor.getRepository(), shouldStop());
	}
	
	public void stop() {
		sourceVisitor.stop();
		super.stop();
	}
}
