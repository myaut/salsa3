package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

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
	
	public static class SuperClass extends ASTNode {
		private List<SuperClassModifier> modifiers;
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
		
		/* Serialization code */
		static {
			ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(SuperClass.class);
			plan.addStringParam(0, "name", false);
			plan.addEnumListParam(1, "modifiers", false, SuperClassModifier.class);
		}
	}
	
	public static class Member extends ASTStatement {
		private List<MemberModifier> modifiers;
		
		public Member(List<MemberModifier> modifiers) {
			this.modifiers = modifiers;
		}
		
		public List<MemberModifier> getModifiers() {
			return modifiers;
		}
		
		/* Serialization code */
		static {
			ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Member.class);
			plan.addEnumListParam(0, "modifiers", false, MemberModifier.class);
		}
	}
	
	private Type type;
	private String className;
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
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(ClassDeclaration.class);
		plan.addEnumParam(0, "type", false, Type.class);
		plan.addStringParam(1, "className", false);
		plan.addNodeListParam(2, "superClasses", false);
	}

	public void addMember(ASTNode declaration, List<MemberModifier> modifiers) {
		Member member = new Member(modifiers); 
		
		addChild(member);
		member.addChild(declaration);
	}
}
