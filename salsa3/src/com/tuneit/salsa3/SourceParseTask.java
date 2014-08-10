package com.tuneit.salsa3;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.tuneit.salsa3.ast.ASTStatement;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.model.Repository;
import com.tuneit.salsa3.model.Source;

import static com.tuneit.salsa3.model.Repository.Language.*;

public class SourceParseTask extends Task {
	private Source source;
	
	private static Logger log = Logger.getLogger(SourceParseTask.class.getName());		
	
	public SourceParseTask(Source source) {
		this.source = source;
		
		setDescription("Parsing source # " + source.getId() + " '" + source.getPath() + 
		   		"' from repository " + source.getRepository().getRepositoryName());
	}
	
	@Override
	public void run() {
		RepositoryManager rm = RepositoryManager.getInstance();
		Repository repository = source.getRepository();
		Path sourcePath = Paths.get(repository.getPath(), source.getPath());
		
		SourceParser parser;
		ASTStatement root;
		
		switch(repository.getLanguage()) {
		case LANG_PHP:
			parser = new PHPParser(sourcePath.toString());
			break;
		default:
			throw new UnsupportedOperationException("Unsupported parser's language " + 
					repository.getLanguage().toString() + "!");
		}
		
		/* Generate AST with parser */
		try {
			root = parser.parse();
		} catch (Exception e) {
			rm.onSourceParsed(source, false, e);
			
			log.log(Level.WARNING, "Source '" + 
						        source.getPath() + "' parsing is failed", e);
			
			throw new TaskException(e);
		}
		
		/* Generate source objects and put them to database */
		try {
			(new SourcePostProcessor(source, root)).postProcessSource();
		} catch (Exception e) {
			rm.onSourceParsed(source, false, e);
			
			log.log(Level.WARNING, "Post-processing of source '" + 
						source.getPath() + "' is failed", e);
			
			throw new TaskException(e);
		}		

		rm.onSourceParsed(source, true, null);
		
	}
}
