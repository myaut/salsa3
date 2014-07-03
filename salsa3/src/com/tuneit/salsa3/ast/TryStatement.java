package com.tuneit.salsa3.ast;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TryStatement extends ASTStatement {
	public static class CatchStatement extends ASTStatement {
		public VariableDeclaration varDecl;
		
		public CatchStatement(VariableDeclaration varDecl) {
			super();
			this.varDecl = varDecl;
		}
	};
	
	private List<CatchStatement> catches;
	private ASTStatement finallyStatement;
	
	public TryStatement() {
		super();
		
		catches = new ArrayList<CatchStatement>();
		finallyStatement = null;
	}
	
	public void addCatchStatement(CatchStatement katch) {
		this.catches.add(katch);
	}
	
	public void setFinallyStatement(ASTStatement finallyStatement) {
		this.finallyStatement = finallyStatement;
	}
	
	@Override 
	public void dumpStatement(OutputStream os, String indent) {
		PrintStream s = new PrintStream(os);
		
		s.print(indent);
		s.println("try ");
		super.dumpStatement(os, indent + ASTStatement.TABSTOP);
		
		for(CatchStatement katch : catches) {
			s.print(indent);
			s.print("catch ");
			if(katch.varDecl != null) {
				s.println(katch.varDecl.toString());
			}
			
			katch.dumpStatement(os, indent + ASTStatement.TABSTOP);
		}
	
		if(finallyStatement != null) {
			s.print(indent);
			s.println("finally");
			
			finallyStatement.dumpStatement(os, indent + ASTStatement.TABSTOP);
		}
	}
	
	@Override
	public String toString() {
		return "TryStatement";
	}
}
