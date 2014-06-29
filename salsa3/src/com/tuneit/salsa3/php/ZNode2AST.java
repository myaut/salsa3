package com.tuneit.salsa3.php;

import java.util.HashMap;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class ZNode2AST {
	private static final int IS_STRING = 6;
	private static final int IS_CONSTANT = 8;
	
	private HashMap<Integer, ASTNode> astCache;
	
	public ZNode2AST() {
		astCache = new HashMap<Integer, ASTNode>();
	}
	
	public ASTNode convert(ZNode zNode) throws ParserException {
		ASTNode node = astCache.get(zNode.id);
		
		if(node == null) {
			node = doConvert(zNode);
			astCache.put(zNode.id, node);
		}
		
		return node;
	}
	
	private ASTNode doConvert(ZNode zNode) throws ParserException {
		switch(zNode.type) {
		case IS_STRING:
			return new Literal(Literal.Type.LIT_STRING, zNode.value, zNode.value);
		case IS_CONSTANT:
			return new Literal(Literal.Type.LIT_CONSTANT, zNode.value);
		}
		
		throw new ParserException("Couldn't convert to AST: " + zNode);
	}
}
