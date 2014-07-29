package com.tuneit.salsa3.php;

import java.util.ArrayList;
import java.util.List;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPClassHandler extends PHPStatementHandler implements
		PHPParserHandler {
	private static final int ZEND_ACC_STATIC = 0x01;
	private static final int ZEND_ACC_ABSTRACT = 0x02;
	private static final int ZEND_ACC_FINAL = 0x04;
	private static final int ZEND_ACC_PUBLIC = 0x100;
	private static final int ZEND_ACC_PROTECTED = 0x200;
	private static final int ZEND_ACC_PRIVATE = 0x400;
	
	private static final int ZEND_ACC_IMPLICIT_ABSTRACT_CLASS = 0x10;
	private static final int ZEND_ACC_EXPLICIT_ABSTRACT_CLASS = 0x20;
	private static final int ZEND_ACC_ABSTRACT_CLASS = 
				ZEND_ACC_IMPLICIT_ABSTRACT_CLASS | ZEND_ACC_EXPLICIT_ABSTRACT_CLASS;	
	private static final int ZEND_ACC_FINAL_CLASS = 0x40;
	private static final int ZEND_ACC_INTERFACE = 0x80;
	private static final int ZEND_ACC_TRAIT = 0x120;
	
	private ClassDeclaration classDecl;
	private PHPParserHandler parent;
	
	public PHPClassHandler(PHPParserHandler parent) {
		this.parent = parent;
		
		this.classDecl = null;
	}

	@Override
	public PHPParserHandler handleState(PHPParserState state)
			throws ParserException {
		
		if(state.isState("begin_class_declaration")) {
			return handleBeginDeclaration(state);
		}
		else if(state.isState("declare_property")) {
			return handleDeclareProperty(state);
		}
		else if(state.isState("declare_class_constant")) {
			return handleDeclareConstant(state);
		}
		else if(state.isState("begin_function_declaration")) {
			return handleFunctionDeclaration(state);
		}
		else if(state.isState("implements")) {
			return handleImplements(state);
		}
		else if(state.isState("use_trait")) {
			return handleUseTrait(state);
		}
		else if(state.isState("end_class_declaration")) {
			return parent;
		}
		
		return super.handleState(state);
	}
	
	private PHPParserHandler handleBeginDeclaration(PHPParserState state) throws ParserException {
		Literal classToken = (Literal) state.getNode("&class_token_val");					
		Literal className = (Literal) state.getNode("class_name");
		Literal parentName = (Literal) state.getNodeOptional("parent_class_name");
		
		classDecl = new ClassDeclaration(getClassType(classToken), className.getToken());
		
		if(parentName != null) {
			classDecl.addSuperClass(parentName.getToken(), ClassDeclaration.SuperClassModifier.SC_EXTENDS_PUBLIC);
		}
		
		setRootNode(classDecl);

		return this;
	}
	
	private PHPParserHandler handleDeclareProperty(PHPParserState state) throws ParserException {
		int accessType = state.getIntParam("access_type");
		Variable variable = (Variable) state.getNode("var_name");
		ASTNode value = state.getNodeOptional("value");
			
		VariableDeclaration varDecl = new VariableDeclaration(variable, new TypeName("mixed"), value);
				
		classDecl.addMember(varDecl, getMemberModifiers(accessType));
		
		return this;
	}
	
	private PHPParserHandler handleDeclareConstant(PHPParserState state) throws ParserException {
		List<ClassDeclaration.MemberModifier> modifierList = new ArrayList<ClassDeclaration.MemberModifier>();
		Literal varNameNode = (Literal) state.getNode("var_name");
		ASTNode value = state.getNode("value");
		
		Variable variable = new Variable(varNameNode.getToken());
		VariableDeclaration varDecl = new VariableDeclaration(variable, new TypeName("mixed"), value);
		
		modifierList.add(ClassDeclaration.MemberModifier.M_FINAL);
		modifierList.add(ClassDeclaration.MemberModifier.M_PUBLIC);
		varDecl.addTypeQualifier("const");
		
		classDecl.addMember(varDecl, modifierList);
		
		return this;
	}
	
	private PHPParserHandler handleFunctionDeclaration(PHPParserState state) throws ParserException {
		int functionFlags = state.getIntParam("fn_flags");
		
		PHPFunctionDeclaration phpFunctionDecl = new PHPFunctionDeclaration(this);
		PHPParserHandler newHandler = phpFunctionDecl.handleState(state);
		
		ASTNode functionDecl = newHandler.getRootNode();
		
		classDecl.addMember(functionDecl, getMemberModifiers(functionFlags));
		
		return newHandler;
	}
	
	private PHPParserHandler handleImplements(PHPParserState state) throws ParserException {
		Literal interfaceNameNode = (Literal) state.getNode("interface_name");
		
		classDecl.addSuperClass(interfaceNameNode.getToken(), 
								ClassDeclaration.SuperClassModifier.SC_IMPLEMENTS);
		
		return this;
	}
	
	private PHPParserHandler handleUseTrait(PHPParserState state) throws ParserException {
		Literal traitNameNode = (Literal) state.getNode("trait_name");
		
		classDecl.addSuperClass(traitNameNode.getToken(), 
								ClassDeclaration.SuperClassModifier.SC_USES_TRAIT);
		
		return this;
	}
	
	private List<ClassDeclaration.MemberModifier> getMemberModifiers(int modifier) {
		List<ClassDeclaration.MemberModifier> modifierList = new ArrayList<ClassDeclaration.MemberModifier>();
		
		if((modifier & ZEND_ACC_PUBLIC) == ZEND_ACC_PUBLIC) {
			modifierList.add(ClassDeclaration.MemberModifier.M_PUBLIC);
		}
		else if((modifier & ZEND_ACC_PROTECTED) == ZEND_ACC_PROTECTED) {
			modifierList.add(ClassDeclaration.MemberModifier.M_PROTECTED);
		}
		else if((modifier & ZEND_ACC_PRIVATE) == ZEND_ACC_PRIVATE) {
			modifierList.add(ClassDeclaration.MemberModifier.M_PRIVATE);
		}
		
		if((modifier & ZEND_ACC_STATIC) == ZEND_ACC_STATIC) {
			modifierList.add(ClassDeclaration.MemberModifier.M_STATIC);
		}
		if((modifier & ZEND_ACC_ABSTRACT) == ZEND_ACC_ABSTRACT) {
			modifierList.add(ClassDeclaration.MemberModifier.M_ABSTRACT);
		}
		if((modifier & ZEND_ACC_FINAL) == ZEND_ACC_FINAL) {
			modifierList.add(ClassDeclaration.MemberModifier.M_FINAL);
		}
		
		return modifierList;
	}
	
	private ClassDeclaration.Type getClassType(Literal classToken) {
		int classType = Integer.valueOf(classToken.getToken());
		
		if((classType & ZEND_ACC_INTERFACE) == ZEND_ACC_INTERFACE) {
			return ClassDeclaration.Type.CLASS_INTERFACE;
		}
		
		if((classType & ZEND_ACC_TRAIT) == ZEND_ACC_TRAIT) {
			return ClassDeclaration.Type.CLASS_TRAIT;
		}
		
		if((classType & ZEND_ACC_FINAL_CLASS) == ZEND_ACC_FINAL_CLASS) {
			return ClassDeclaration.Type.CLASS_FINAL;
		}
		
		if((classType & ZEND_ACC_ABSTRACT_CLASS) == ZEND_ACC_ABSTRACT_CLASS) {
			return ClassDeclaration.Type.CLASS_ABSTRACT;
		}
		
		return ClassDeclaration.Type.CLASS_NORMAL;
	}
}
