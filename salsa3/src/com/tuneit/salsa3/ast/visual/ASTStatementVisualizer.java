package com.tuneit.salsa3.ast.visual;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;

public class ASTStatementVisualizer implements ASTStatementSerializer {
	private static ASTNodeVisualizer nodeVisualizer = new ASTNodeVisualizer();
	
	@Override
	public Object createStatement(ASTNode node) throws ASTNodeSerdesException {
		return ASTNodeSerdes.serializeNode(nodeVisualizer, node);
	}

	@Override
	public void addNode(Object stmt, ASTNode node)
			throws ASTNodeSerdesException {
		VisualNode vn = (VisualNode) stmt;
		VisualNode vn2 = (VisualNode) ASTNodeSerdes.serializeNode(nodeVisualizer, node);
		
		vn.addNodeToBottom(vn2);
	}

	@Override
	public void addStatement(Object stmt, ASTStatement node, Object subStatement)
			throws ASTNodeSerdesException {
		VisualNode vn = (VisualNode) stmt;
		VisualNode vn2 = (VisualNode) subStatement;
		
		vn.addNodeToBottom(vn2);
	}

	@Override
	public void addSpecialNode(Object stmt, String stateName, ASTNode node)
			throws ASTNodeSerdesException {
		VisualNode vn = (VisualNode) stmt;
		VisualNode vn2 = new VisualNode(stateName);
		
		vn.addNodeToBottom(vn2);
		
		if(node != null) {
			VisualNode vn3 = (VisualNode) ASTNodeSerdes.serializeNode(nodeVisualizer, node);
			
			vn2.addNodeToRight("", vn3);
		}
	}
}
