package com.tuneit.salsa3.cli;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.tuneit.salsa3.RepositoryParseTask;
import com.tuneit.salsa3.SourceManager;
import com.tuneit.salsa3.SourceParseTask;
import com.tuneit.salsa3.TaskManager;
import com.tuneit.salsa3.model.ClassDeclaration;
import com.tuneit.salsa3.model.ClassMember;
import com.tuneit.salsa3.model.Repository;
import com.tuneit.salsa3.model.Source;
import com.tuneit.salsa3.model.SourceSnippet;
import com.tuneit.salsa3.model.SuperClassReference;
import com.tuneit.salsa3.cli.ProfileView.*;
import com.tuneit.salsa3.ASTStatementUtils;
import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;
import com.tuneit.salsa3.ast.FunctionDeclaration;
import com.tuneit.salsa3.ast.Literal;
import com.tuneit.salsa3.ast.VariableDeclaration;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.ClassDeclaration.Type;

/* XXX: Delete this when time will come */
import com.tuneit.salsa3.PHPParser;
import com.tuneit.salsa3.ParserException;

@Component
public class CLISource implements CommandMarker {
	private static final Type classTypeCriteria[][] = {
		{Type.CLASS_INTERFACE},
		{Type.CLASS_TRAIT},
		{Type.CLASS_FINAL},
		{Type.CLASS_ABSTRACT},
		{Type.CLASS_NORMAL, Type.CLASS_FINAL, Type.CLASS_ABSTRACT},
	};
	
	private static final SourceSnippet.Type snippetTypeCriteria[] = {
		SourceSnippet.Type.SS_FUNCTION,
		SourceSnippet.Type.SS_VARIABLE_OR_CONSTANT,
		SourceSnippet.Type.SS_VARIABLE_OR_CONSTANT,
		SourceSnippet.Type.SS_DIRECTIVE
	};
	
	@Autowired
	private CLIRepositoryHolder holder;
	
	@CliAvailabilityIndicator({"parser", "class list"})
	public boolean isRepositorySelected() {
		return holder.getRepository() != null;
	}
	
	@CliCommand(value = "parse", help = "Parse a source")
	public String parse() {
		TaskManager tm = TaskManager.getInstance();
		
		Repository repository = holder.getRepository();
		Source source = holder.getSource();
		
		if(source != null) {
			if(source.isParsed()) {
				throw new IllegalStateException("Source '" + source.getPath() + "' from repository '" + 
						repository.getRepositoryName() + "' is already parsed!");
			}
			
			SourceParseTask task = new SourceParseTask(source);
			tm.addTask(task);
		}
		else if(repository != null) {
			RepositoryParseTask task = new RepositoryParseTask(repository);
			tm.addTask(task);
		}
		else {
			throw new IllegalStateException("Nothing to parse");
		}
		
		return "Parsing is started.\nUse 'task list' to monitor activities.";
	}
	
	@CliCommand(value = "class list", help = "Shows list of classes")
	public String listClasses(
			/* Pick by class / super-class */	
			@CliOption(key = {"name"}, mandatory = false, help = "Class name (masks with * are acceptible)") 
				final String classNameMask,
			@CliOption(key = {"super"}, mandatory = false, help = "Super class") 
				final String superClassName,
			
			@CliOption(key = {"interface"}, mandatory = false, help = "Find interfaces",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findInterfaces,
			@CliOption(key = {"trait"}, mandatory = false, help = "Find traits",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findTraits,
			@CliOption(key = {"final"}, mandatory = false, help = "Find final classes",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findFinalClasses,
			@CliOption(key = {"abstract"}, mandatory = false, help = "Find abstract classes",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findAbstractClasses,
			@CliOption(key = {"class"}, mandatory = false, help = "Find classes",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findClasses) {				
		SourceManager sm = SourceManager.getInstance();
		
		Repository repository = holder.getRepository();
		Source source = holder.getSource();
		
		Type classTypeFilter[] = getClassTypeCriteria(findInterfaces, findTraits, 
				findFinalClasses, findAbstractClasses, 
				findClasses); 
		
		List<ClassDeclaration> classes = 
				sm.findClasses(repository, source, classNameMask, 
							   superClassName, classTypeFilter);
		
		TableView tv = new TableView();
		
		TableView.Row row = tv.newRow()
			.append("ID")
			.append("TYPE")
			.append("CLASS");
		
		if(source == null) {
			row.append("SOURCE");
		}
		
		for(ClassDeclaration classDecl : classes) {
			row = tv.newRow()
				.append(classDecl.getId())
				.append(classDecl.getClassType().toString().substring(6))
				.append(classDecl.getClassName());
			
			if(source == null) {
				row.append(classDecl.getSourceReference().getSource().getPath());
			}
		}
		
		return tv.toString();
	}
	
	private Type[] getClassTypeCriteria(Boolean... flags) {
		Set<Type> classTypeSet = new HashSet<Type>();
		
		for(int i = 0; i < 5; ++i) {
			if(flags[i]) {
				classTypeSet.addAll(Arrays.asList(classTypeCriteria[i]));
			}
		}
		
		return (Type[]) classTypeSet.toArray(new Type[classTypeSet.size()]);
	}
	
	@CliCommand(value = "class show", help = "Shows information on class")
	public String showClass(
			@CliOption(key = {"name"}, mandatory = true, help = "Class name (masks with * are acceptible)") 
				final String className) {
		SourceManager sm = SourceManager.getInstance();
		
		Repository repository = holder.getRepository();
		Source source = holder.getSource();
		
		ClassDeclaration klass = sm.getClassByName(repository, source, className);		
		
		if(klass == null) {
			throw new IllegalArgumentException("Class '" + className + "' was not found!");
		}
		
		List<SourceManager.SubClassReference> subClasses = sm.getSubClasses(repository, source, className);
		List<ClassMember> classMembers = sm.getClassMembers(klass);
		
		ProfileView pv = new ProfileView();
		ProfileSection pvClass = pv.newProfileSection(klass.getClassName());
		
		pvClass.setParameter("ID", klass.getId());
		pvClass.setParameter("Type", klass.getClassType());
		pvClass.setParameter("Source", klass.getSourceReference().getSource().getPath());
		
		if(klass.getSuperClasses().size() > 0) {
			TableSection pvSuperClasses = pv.newTableSection("Direct super classes");
			pvSuperClasses.newRow().append("RELATIONSHIP").append("CLASS");
			
			for(SuperClassReference superClass : klass.getSuperClasses()) {
				pvSuperClasses.newRow()
					.append(StringUtils.join(superClass.getModifiers(), ", "))
					.append(superClass.getSuperClassName());
			}
		}
		
		if(classMembers.size() > 0) {
			TableSection pvClassMembers = pv.newTableSection("Members");
			pvClassMembers.newRow().append("ID").append("MODIFIERS").append("TYPE").append("MEMBER");
			
			for(ClassMember member : classMembers) {
				pvClassMembers.newRow()
					.append(member.getCode().getId())
					.append(StringUtils.join(member.getModifiers(), ", "))
					.append(member.getCode().getType())
					.append(member.getCode().getName());
			}
		}
		
		if(subClasses.size() > 0) {
			TableSection pvSubClasses = pv.newTableSection("Direct sub classes");
			pvSubClasses.newRow().append("RELATIONSHIP").append("CLASS");
			
			for(SourceManager.SubClassReference subClass : subClasses) {
				pvSubClasses.newRow()
					.append(StringUtils.join(subClass.getReference().getModifiers(), ", "))
					.append(subClass.getSubClass().getClassName());
			}
		}
		
		return pv.toString();
	}
	
	@CliCommand(value = "snippet list", help = "Shows list of source snippets")
	public String listSnippets(
			@CliOption(key = {"name"}, mandatory = false, help = "Snippet name mask (masks with * are acceptible)") 
				final String snippetNameMask,
			
			@CliOption(key = {"function"}, mandatory = false, help = "Find functions",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findFunctions,
			@CliOption(key = {"variable"}, mandatory = false, help = "Find variables and constants",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findVariables,
			@CliOption(key = {"constant"}, mandatory = false, help = "Find variables and constants",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findConstants,
			@CliOption(key = {"directive"}, mandatory = false, help = "Find directives",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean findDirectives,
				
			@CliOption(key = {"class-member"}, mandatory = false, help = "Find classes",
					specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean showClassMembers) {				
		SourceManager sm = SourceManager.getInstance();
		
		Repository repository = holder.getRepository();
		Source source = holder.getSource();
		
		SourceSnippet.Type[] snippetTypeFilter = getSnippetTypeCriteria(findFunctions, 
				findVariables, findConstants, findDirectives);
		
		List<SourceSnippet> snippetList = 
				sm.getSourceSnippets(repository, source, showClassMembers, 
									 snippetNameMask, snippetTypeFilter);
		
		TableView tv = new TableView();
		
		TableView.Row row = tv.newRow()
			.append("ID")
			.append("TYPE")
			.append("NAME");
		
		if(source == null) {
			row.append("SOURCE");
		}
		
		for(SourceSnippet snippet : snippetList) {
			row = tv.newRow()
				.append(snippet.getId())
				.append(snippet.getType().toString().substring(3))
				.append(snippet.getName());
			
			if(source == null) {
				row.append(snippet.getSourceReference().getSource().getPath());
			}
		}
		
		return tv.toString();
	}
	
	private SourceSnippet.Type[] getSnippetTypeCriteria(Boolean... flags) {
		Set<SourceSnippet.Type> snippetTypeSet = new HashSet<SourceSnippet.Type>();
		
		for(int i = 0; i < 4; ++i) {
			if(flags[i]) {
				snippetTypeSet.add(snippetTypeCriteria[i]);
			}
		}
		
		return (SourceSnippet.Type[]) snippetTypeSet.toArray(new SourceSnippet.Type[snippetTypeSet.size()]);
	}
	
	@CliCommand(value = "snippet show", help = "Shows information about snippet")
	public String showSnippet(
			@CliOption(key = {"id"}, mandatory = true, help = "Snippet id") 
				final Integer id)  {
		SourceManager sm = SourceManager.getInstance();
		
		SourceSnippet snippet = sm.getSourceSnippetById(id);
		
		if(snippet == null) {
			throw new IllegalArgumentException("Source snippet #" + id + " was not found!");
		}
		
		ASTStatement root;
		ASTNode node;
		
		try {
			root = ASTStatementUtils.deserializeStatement(snippet.getAst(), true);
			node = root.getChildren().get(0);
		}
		catch(ASTNodeSerdesException anse) {
			throw new IllegalStateException("Couldn't deserialize snippet's AST", anse);
		}
		
		ProfileView pv = new ProfileView();
		ProfileSection pvSnippet = pv.newProfileSection("Snippet #" + id);
		
		pvSnippet.setParameter("Name", snippet.getName());
		pvSnippet.setParameter("Type", snippet.getType());
		pvSnippet.setParameter("Source", snippet.getSourceReference().getSource().getPath());
		
		if(snippet.getType().equals(SourceSnippet.Type.SS_FUNCTION)) {
			FunctionDeclaration fdecl = (FunctionDeclaration) node;
			
			pvSnippet.setParameter("Function name", fdecl.getFunctionName());
			pvSnippet.setParameter("Function declarators", 
									StringUtils.join(fdecl.getFunctionDeclarators(), " "));
			pvSnippet.setParameter("Return type",  fdecl.getReturnType().getTypeName());
			pvSnippet.setParameter("Return type qualifiers", 
									StringUtils.join(fdecl.getReturnType().getTypeQualifiers(), " "));
			
			TableSection pvArgs = pv.newTableSection("Arguments");
			pvArgs.newRow()
				.append("OFFSET")
				.append("NAME")
				.append("TYPE")
				.append("VALUE");
			
			int offset = 0;
			for(ASTNode argument : fdecl.getArguments()) {
				VariableDeclaration varDecl = (VariableDeclaration) argument;
				ASTNode defaultValue = varDecl.getDefaultValue();
				
				TableView.Row row = 
					pvArgs.newRow()
						.append(Integer.valueOf(offset).toString())
						.append(varDecl.getVariable().getVarName())
						.append(StringUtils.join(varDecl.getTypeName().getTypeQualifiers(), " ") 
								+ " " + varDecl.getTypeName().getTypeName());
				
				if(defaultValue != null) {
					if(defaultValue instanceof Literal) {
						Literal litDefaultValue = (Literal) defaultValue;
						
						row.append(litDefaultValue.getToken());
					}
					else {
						row.append("[EXPRESSION]");
					}
				}
				
				++offset;
			}
		}
		else if(snippet.getType().equals(SourceSnippet.Type.SS_VARIABLE_OR_CONSTANT)) {
			VariableDeclaration varDecl = (VariableDeclaration) node;
			ASTNode defaultValue = varDecl.getDefaultValue();
			
			pvSnippet.setParameter("Variable name", varDecl.getVariable().getVarName());
			pvSnippet.setParameter("Type", varDecl.getTypeName().getTypeName());
			pvSnippet.setParameter("Type qualifiers", 
									StringUtils.join(varDecl.getTypeName().getTypeQualifiers(), " "));
			
			if(defaultValue != null) {
				if(defaultValue instanceof Literal) {
					Literal litDefaultValue = (Literal) defaultValue;
					
					pvSnippet.setParameter("Default value", litDefaultValue.getToken());
				}
				else {
					pvSnippet.setParameter("Default value", "[EXPRESSION]");
				}
			}
		}
		
		return pv.toString();
	}
	
	/* FIXME: Delete this in release */
	@CliCommand(value = "_parsefile", help = "Test parse/serializer function")
	public String parseFile(
			@CliOption(key = {"path"}, mandatory = false, help = "Path to the source") 
				final String path,
			@CliOption(key = {"deserialize"}, mandatory = false, help = "Test deserialization",
				specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean deserialize,
			@CliOption(key = {"short-names"}, mandatory = false, help = "Use short names for AST nodes",
				specifiedDefaultValue = "true", unspecifiedDefaultValue = "false") 
				final Boolean useShortNames,
			@CliOption(key = {"indent"}, mandatory = false, help = "Dump with indent",
				unspecifiedDefaultValue = "0") 
				final Integer indent) throws ParserException, ASTNodeSerdesException {
		try {
			PHPParser parser = new PHPParser(path);
			ASTStatement root = parser.parse();
			
			String ast = ASTStatementUtils.serializeStatement(root, indent.intValue(), useShortNames);
			
			if(deserialize) {
				ASTStatementUtils.deserializeStatement(ast, useShortNames);
			}
			
			return ast;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
