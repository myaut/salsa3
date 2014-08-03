package com.tuneit.salsa3;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;
import com.tuneit.salsa3.ast.FunctionDeclaration;
import com.tuneit.salsa3.ast.Variable;
import com.tuneit.salsa3.ast.VariableDeclaration;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.model.*;

public class SourcePostProcessor {
	private static final String PERSISTENCE_UNIT_NAME = "salsaPU";
	
	private EntityManager em;
	
	public Source source; 
	public ASTStatement root;
	
	public SourcePostProcessor(Source source, ASTStatement root) {
		super();
		
		this.source = source;
		this.root = root;
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		em = emf.createEntityManager();		
	}
	
	public void postProcessSource() throws ASTNodeSerdesException { 		
		try {
			em.getTransaction().begin();
			postProcessSourceImpl();
			em.getTransaction().commit();
		}
		catch(Exception e) {			
			em.getTransaction().rollback();
			throw e;
		}
	}
	
	private void postProcessSourceImpl() throws ASTNodeSerdesException {
		for(ASTNode node : root.getChildren()) {
			if(node instanceof com.tuneit.salsa3.ast.ClassDeclaration) {
				postProcessClass((com.tuneit.salsa3.ast.ClassDeclaration) node);
			}
			else {
				postProcessCode(node);
			}
		}
	}
	
	private void postProcessClass(com.tuneit.salsa3.ast.ClassDeclaration classDeclaration)
			 throws ASTNodeSerdesException {
		ClassDeclaration klass = new ClassDeclaration(createSourceReference(classDeclaration),
				classDeclaration.getClassName(), classDeclaration.getType());
				
		for(com.tuneit.salsa3.ast.ClassDeclaration.SuperClass superClass : classDeclaration.getSuperClasses()) {
			SuperClassReference ref = new SuperClassReference(klass, superClass.getName(), superClass.getModifiers());
			em.persist(ref);
			
			klass.getSuperClasses().add(ref);
		}
		
		em.persist(klass);
		
		for(ASTNode node : classDeclaration.getChildren()) {
			com.tuneit.salsa3.ast.ClassDeclaration.Member member = 
					(com.tuneit.salsa3.ast.ClassDeclaration.Member) node;
			ASTNode declaration = member.getChildren().get(0);
			
			SourceSnippet snippet = postProcessCode(declaration);
			
			ClassMember classMember = new ClassMember(klass, snippet);
			classMember.setModifiers(member.getModifiers());
			
			em.persist(classMember);
		}
	}
	
	private SourceSnippet postProcessCode(ASTNode node) throws ASTNodeSerdesException {
		SourceSnippet.Type type;
		String name;
		String ast;
		
		if(node instanceof FunctionDeclaration) {
			FunctionDeclaration fdecl = (FunctionDeclaration) node;			
			
			name = fdecl.getFunctionName();			
			type = SourceSnippet.Type.SS_FUNCTION;
		}
		else if(node instanceof VariableDeclaration) {
			VariableDeclaration varDecl = (VariableDeclaration) node;	
			
			name = ((Variable) varDecl.getVariable()).getVarName();
			type = SourceSnippet.Type.SS_VARIABLE_OR_CONSTANT;
		}
		else {
			name = "";
			type = SourceSnippet.Type.SS_DIRECTIVE;
		}
		
		ASTStatement nodeRoot = new ASTStatement();		
		nodeRoot.addChild(node);			
		ast = ASTStatementUtils.serializeStatement(nodeRoot);
		
		SourceReference sourceReference = createSourceReference(node); 		
		SourceSnippet snippet = new SourceSnippet(type, name, sourceReference, ast);
		em.persist(snippet);
		
		return snippet;
	}
	
	private SourceReference createSourceReference(ASTNode node) {
		/* TODO: References */
		SourceReference sourceReference = new SourceReference(source, 0, 0, 0, 0, 0, 0);
		em.persist(sourceReference);
		
		return sourceReference;
	}
}
