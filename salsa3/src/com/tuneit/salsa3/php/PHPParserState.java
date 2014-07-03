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
	
	private boolean isMatched;
	
	public HashMap<String, Integer> intParams;
	public HashMap<String, ASTNode> nodes;
	
	private static final String genericStateArray[] = {
		"extended_info",
		"begin_variable_parse",
		"end_variable_parse",
		"free",
		"check_writable_variable",
		"fetch_simple_variable",
		"extended_fcall_end",
		"boolean_and_begin",
		"boolean_or_begin",
		"initialize_try_catch_element",
		"first_catch",
		"bind_catch"
	};
	public static final Set<String> genericStates = 
			new HashSet<String>(Arrays.asList(genericStateArray));
	
	public PHPParserState() {
		intParams = new HashMap<String, Integer>();
		nodes = new HashMap<String, ASTNode>(); 
		
		isMatched = false;
	}
	
	public boolean isState(String state) {
		boolean isState = this.state.equals(state);
		
		if(!isMatched && isState)
			isMatched = true;
		
		return isState;
	}
	
	public boolean isMatched() {
		return isMatched;
	}
	
	public boolean isGenericState() {
		boolean isState = genericStates.contains(state);
		
		if(!isMatched && isState)
			isMatched = true;
		
		return isState;
	}
	
	public ASTNode getNode(String key) throws ParserException {
		ASTNode node = nodes.get(key);
		
		if(node == null) {
			throw new ParserException("State " + state + " doesn't contain znode " + key + "!");
		}
		
		// System.out.println(key + ": " + node + " -> " + node.getNode());
		
		return node.getNode();
	}
	
	public ASTNode getNodeOptional(String key) throws ParserException {
		ASTNode node = nodes.get(key);
		
		if(node == null) {
			return null;
		}
		
		// System.out.println(key + ": " + node + " -> " + node.getNode());
		
		return node.getNode();
	}
	
	public boolean hasNode(String key) {
		return nodes.containsKey(key);
	}
	
	public int getIntParam(String key) throws ParserException {
		Integer param = intParams.get(key);
		
		if(param == null) {
			throw new ParserException("State " + state + " doesn't contain integer param " + key + "!");
		}
		
		return param;
	}
}
