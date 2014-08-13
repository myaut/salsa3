package com.tuneit.salsa3.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;

import com.tuneit.salsa3.ast.serdes.annotations.NodeParameter;
import com.tuneit.salsa3.ast.serdes.annotations.Parameter;


/**
 * <strong>SwitchStatement</strong> is an AST compound statement 
 * <ul>
 *   <li> expression -- 
 *   <li> cases -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class SwitchStatement extends ASTStatement {
	
	/**
	 * <strong>Case</strong> is an AST  
	 * <ul>
	 *   <li> pattern -- 
	 *   <li> iterator -- 
	 * </ul>
	 * 
	 * @author Sergey Klyaus
	 */
	public static class Case {
		public ASTNode pattern;
		public ListIterator<ASTNode> iterator;
	};
	

	@Parameter(offset = 0, optional = false)
	@NodeParameter
	private ASTNode expression;
	private List<Case> cases;
	
	public SwitchStatement(ASTNode expression) {
		this.expression = expression;
		this.cases = new ArrayList<Case>();
		
		expression.reuseInExpression(this);
	}
	
	public void addCase(ASTNode pattern) {
		List<ASTNode> children = getChildren(); 
		ListIterator<ASTNode> iterator = children.listIterator(children.size());
		
		Case kase = new Case();
		
		kase.pattern = pattern;
		kase.iterator = iterator;
		
		cases.add(kase);
	}

	public ASTNode getExpression() {
		return expression;
	}

	public List<Case> getCases() {
		return cases;
	}

	
	@Override
	public Object serializeStatement(ASTStatementSerializer serializer) throws ASTNodeSerdesException {
		Object switchStatement = serializer.createStatement(this);
		
		ListIterator<Case> caseIterator = cases.listIterator();
		ListIterator<ASTNode> nodeIterator = getChildren().listIterator();
		
		/* Iterate over nodes in this statement.
		 * If case references current node through iterator, print case
		 * until it wouldn't reference other node */
		while(nodeIterator.hasNext()) {
			ASTNode node = nodeIterator.next();
			
			while(caseIterator.hasNext()) {						
				Case kase = caseIterator.next();
				
				/* Node already picked next, so substitute one from index */
				if(kase.iterator.previousIndex() != (nodeIterator.previousIndex() - 1)) {
					/* Case references other expression, go back and print nodes */
					caseIterator.previous();
					break;					
				}
				
				if(kase.pattern == null) {
					serializer.addSpecialNode(switchStatement, "default", null);
				}
				else {
					serializer.addSpecialNode(switchStatement, "case", kase.pattern);
				}
			}
			
			serializeStatementNode(serializer, switchStatement, node);
		}
		
		return switchStatement;
	}
	
	@Override
	public void deserializeState(String state, ASTNode node) throws ASTNodeSerdesException {
		if(state.equals("default")) {
			addCase(null);
		}
		else if(state.equals("case")) {
			addCase(node);
		}
		else {
			super.deserializeState(state, node);
		}
	}
	
}
