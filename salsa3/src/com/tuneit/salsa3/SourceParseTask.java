package com.tuneit.salsa3;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.tuneit.salsa3.ast.ASTStatement;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.model.Repository;
import com.tuneit.salsa3.model.Source;

import static com.tuneit.salsa3.model.Repository.Language.*;

public class SourceParseTask extends Task {
	private Source source;
	
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
		
		try {
			root = parser.parse();
		} catch (Exception e) {
			rm.onSourceParsed(source, false, e);
			
			throw new TaskException(e);
		}
		
		try {
			(new SourcePostProcessor(source, root)).postProcessSource();
		} catch (Exception e) {
			rm.onSourceParsed(source, false, e);
			
			throw new TaskException(e);
		}		

		rm.onSourceParsed(source, true, null);
		
	}
}
