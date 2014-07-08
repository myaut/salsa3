package com.tuneit.salsa3.ast.serdes;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;

public class ASTStatementSerdes {
	public static ASTStatement deserializeStatement(ASTNodeDeserializer nodeDeserializer, 
			ASTStatementDeserializer deserializer, 
			Object o) throws ASTNodeSerdesException {
		ASTStatement stmt = deserializer.getStatementNode(nodeDeserializer, o);
		ASTStatementDeserializer.StatementIterator iterator = deserializer.getFirstStatement(o);
		
		while(!iterator.isLastStatement()) {
			ASTStatementDeserializer.Node node = iterator.getNode();
			
			if(node instanceof ASTStatementDeserializer.Statement) {
				ASTStatement astStmt = deserializeStatement(nodeDeserializer, deserializer, node.getNodeObject());
				stmt.addChild(astStmt);
			} 
			else if(node instanceof ASTStatementDeserializer.SpecialState) {
				Object nodeObject = node.getNodeObject();
				ASTNode astNode =  null;			
				if(nodeObject != null) {
					astNode = ASTNodeSerdes.deserializeNode(nodeDeserializer, nodeObject);
				}
				
				ASTStatementDeserializer.SpecialState specialState = (ASTStatementDeserializer.SpecialState) node;
				
				stmt.deserializeState(specialState.getStateName(), astNode);
			}
			else {
				ASTNode astNode = ASTNodeSerdes.deserializeNode(nodeDeserializer, node.getNodeObject());
				stmt.addChild(astNode);
			}
			
			iterator = deserializer.getNextStatement(o, iterator);
		}
		
		return stmt;
	}
}
