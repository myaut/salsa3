package com.tuneit.salsa3.php;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.ASTNode;

public final class PHPParserState {
	public int lineNo;
	public String state;
	
	public HashMap<String, Integer> intParams;
	public HashMap<String, ASTNode> nodes;
	
	private static final String genericStateArray[] = {
		"extended_info",
		"begin_variable_parse",
		"end_variable_parse",
		"free",
		"check_writable_variable",
		"fetch_simple_variable",
		"extended_fcall_end"
	};
	public static final Set<String> genericStates = 
			new HashSet<String>(Arrays.asList(genericStateArray));
	
	public PHPParserState() {
		intParams = new HashMap<String, Integer>();
		nodes = new HashMap<String, ASTNode>(); 
	}
	
	public boolean isState(String state) {
		return this.state.equals(state);
	}
	
	public boolean isGenericState() {
		return genericStates.contains(state);
	}
	
	public ASTNode getNode(String key) throws ParserException {
		ASTNode node = nodes.get(key);
		
		if(node == null) {
			throw new ParserException("State " + state + " doesn't contain znode " + key + "!");
		}
		
		// System.out.println(key + ": " + node + " -> " + node.getNode());
		
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
