package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.annotations.Parameter;
import com.tuneit.salsa3.ast.serdes.annotations.EnumParameter;


/**
 * <strong>Literal</strong> is an AST node 
 * <ul>
 *   <li> token -- 
 *   <li> type -- 
 * </ul>
 * 
 * @author Sergey Klyaus
 */
public class Literal extends ASTNode {
	public enum Type {
		LIT_NULL,
		LIT_BOOLEAN,
		LIT_LONG,
		LIT_FLOAT,
		LIT_STRING,
		LIT_CHARACTER
	};
	

	@Parameter(offset = 1, optional = false)
	private String token;

	@Parameter(offset = 0, optional = false)
	@EnumParameter(enumClass = Type.class)
	private Type type;
	
	public Literal(Type type, String token) {
		super();
		this.token = token;
		this.type = type;
	}
	
	public ASTNode clone() {
		return new Literal(type, token);
	}

	public String getToken() {
		return token;
	}

	public Type getType() {
		return type;
	}
}
