package com.tuneit.salsa3.php;

import java.util.HashMap;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;

public final class PHPParserState {
	public int lineNo;
	public String state;
	
	public HashMap<String, Integer> intParams;
	public HashMap<String, ASTNode> nodes;
	
	public PHPParserState() {
		intParams = new HashMap<String, Integer>();
		nodes = new HashMap<String, ASTNode>(); 
	}
	
	public boolean isState(String state) {
		return this.state.equals(state);
	}
	
	public ASTNode getNode(String key) throws ParserException {
		ASTNode node = nodes.get(key);
		
		if(node == null) {
			throw new ParserException("State " + state + " doesn't contain znode " + key + "!");
		}
		
		return node.getNode();
	}
	
	public int getIntParam(String key) throws ParserException {
		Integer param = intParams.get(key);
		
		if(param == null) {
			throw new ParserException("State " + state + " doesn't contain integer param " + key + "!");
		}
		
		return param;
	}
}
