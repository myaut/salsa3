package com.tuneit.salsa3.php;

import java.util.HashMap;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class ZNode2AST {
	/* Syncronous with zend.h */
	private static final int IS_NULL = 0;
	private static final int IS_LONG = 1;
	private static final int IS_DOUBLE = 2;
	private static final int IS_BOOL = 3;
	private static final int IS_ARRAY = 4;
	private static final int IS_STRING = 6;
	private static final int IS_CONSTANT = 8;
	private static final int IS_CALLABLE = 10;
	private static final int IS_VARIABLE = 12;
	private static final int IS_CONSTANT_ARRAY = 11;
	private static final int IS_TOKEN = 13;
	
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
			return new Literal(Literal.Type.LIT_STRING, zNode.value);
		case IS_LONG:
			return new Literal(Literal.Type.LIT_LONG, zNode.value);
		case IS_BOOL:
			return new Literal(Literal.Type.LIT_BOOLEAN, zNode.value);
		case IS_DOUBLE:
			return new Literal(Literal.Type.LIT_FLOAT, zNode.value);
		case IS_NULL:
			return new Literal(Literal.Type.LIT_NULL, zNode.value);			
		case IS_CONSTANT:
			return new Constant(zNode.value);
		case IS_VARIABLE:
			return new Variable(zNode.value);
		case IS_CONSTANT_ARRAY:
			return new ArrayLiteral();
		case IS_TOKEN:
			return new ASTNode();
		
		/* Callable and array znodes are used only in type-hint 
		 * expressions, so create a typename */
		case IS_ARRAY:
			return new TypeName("array");
		case IS_CALLABLE:
			return new TypeName("callable");
		}
		
		throw new ParserException("Couldn't convert to AST: " + zNode);
	}
}
