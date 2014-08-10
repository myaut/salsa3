package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

import com.tuneit.salsa3.ast.serdes.annotations.ListParameter;
import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;
import com.tuneit.salsa3.ast.serdes.annotations.EnumParameter;


/**
 * <strong>ClassDeclaration</strong> is an AST compound statement 
 * <ul>
 *   <li> type -- 
 *   <li> className -- 
 *   <li> superClasses -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class ClassDeclaration extends ASTStatement {
	public enum SuperClassModifier {
		SC_EXTENDS_PUBLIC,
		SC_EXTENDS_PROTECTED,
		SC_EXTENDS_PRIVATE,
		SC_IMPLEMENTS,
		SC_USES_TRAIT
	}
	
	public enum MemberModifier {
		M_PUBLIC,
		M_PROTECTED,
		M_PRIVATE,
		
		M_STATIC,
		M_ABSTRACT,
		M_FINAL,
		M_CONST		
	}
	
	public enum Type {
		CLASS_NORMAL,
		CLASS_ABSTRACT,
		CLASS_FINAL,
		CLASS_INTERFACE,
		CLASS_TRAIT
	}
	
	
	/**
	 * <strong>SuperClass</strong> is an AST node 
	 * <ul>
	 *   <li> modifiers -- 
	 *   <li> name -- 
	 * </ul>
	 * 
	 * @author Sergey Klyaus
	 */
	public static class SuperClass extends ASTNode {

		@Parameter(offset = 1, optional = false)
		@ListParameter
		@EnumParameter(enumClass = SuperClassModifier.class)
		private List<SuperClassModifier> modifiers;

		@Parameter(offset = 0, optional = false)
		private String name;

		public SuperClass(String name, List<SuperClassModifier> modifiers) {
			super();
			this.modifiers = modifiers;
			this.name = name;
		}
		
		public List<SuperClassModifier> getModifiers() {
			return modifiers;
		}

		public String getName() {
			return name;
		}
		
	}
	
	
	/**
	 * <strong>Member</strong> is an AST compound statement 
	 * <ul>
	 *   <li> modifiers -- 
	 * </ul>
	 * 
	 * @author Sergey Klyaus
	 */
	public static class Member extends ASTStatement {

		@Parameter(offset = 0, optional = false)
		@ListParameter
		@EnumParameter(enumClass = MemberModifier.class)
		private List<MemberModifier> modifiers;
		
		public Member(List<MemberModifier> modifiers) {
			this.modifiers = modifiers;
		}
		
		public List<MemberModifier> getModifiers() {
			return modifiers;
		}
		
	}
	

	@Parameter(offset = 0, optional = false)
	@EnumParameter(enumClass = Type.class)
	private Type type;

	@Parameter(offset = 1, optional = false)
	private String className;

	@Parameter(offset = 2, optional = false)
	@ListParameter
	@NodeParameter
	private List<SuperClass> superClasses;
	
	public ClassDeclaration(Type type, String className,
			List<SuperClass> superClasses) {
		super();
		this.type = type;
		this.className = className;
		this.superClasses = superClasses;
	}
	
	public ClassDeclaration(Type type, String className) {
		super();
		this.type = type;
		this.className = className;
		this.superClasses = new ArrayList<SuperClass>();
	}
	
	public void addSuperClass(String superClassName, List<SuperClassModifier> modifiers) {
		superClasses.add(new SuperClass(superClassName, modifiers));
	}
	
	public void addSuperClass(String superClassName, SuperClassModifier... modifiers) {
		superClasses.add(new SuperClass(superClassName, Arrays.asList(modifiers)));
	}

	public Type getType() {
		return type;
	}

	public String getClassName() {
		return className;
	}

	public List<SuperClass> getSuperClasses() {
		return superClasses;
	}
	

	public void addMember(ASTNode declaration, List<MemberModifier> modifiers) {
		Member member = new Member(modifiers); 
		
		addChild(member);
		member.addChild(declaration);
	}
}
