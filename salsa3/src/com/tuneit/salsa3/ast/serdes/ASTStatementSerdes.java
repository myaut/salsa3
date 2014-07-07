package com.tuneit.salsa3.ast.serdes;

import org.json.JSONException;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;

public class ASTStatementSerdes {
	public static ASTStatement deserializeStatement(ASTStatementDeserializer deserializer, 
			Object o) throws ASTNodeSerdesException {
		ASTStatement stmt = deserializer.getStatementNode(o);
		ASTStatementDeserializer.StatementIterator iterator = deserializer.getFirstStatement(o);
		
		while(!iterator.isLastStatement()) {
			ASTStatementDeserializer.Node node = iterator.getNode();
			
			if(node instanceof ASTStatementDeserializer.Statement) {
				ASTStatement astStmt = deserializeStatement(deserializer, node.getNodeObject());
				stmt.addChild(astStmt);
			} 
			else if(node instanceof ASTStatementDeserializer.SpecialState) {
				ASTNode astNode = ASTNodeSerdes.deserializeNode(node.getNodeObject());
				ASTStatementDeserializer.SpecialState specialState = (ASTStatementDeserializer.SpecialState) node;
				
				stmt.deserializeState(specialState.getStateName(), astNode);
			}
			else {
				ASTNode astNode = ASTNodeSerdes.deserializeNode(node.getNodeObject());
				stmt.addChild(astNode);
			}
			
			iterator = deserializer.getNextStatement(o, iterator);
		}
		
		return stmt;
	}
}
