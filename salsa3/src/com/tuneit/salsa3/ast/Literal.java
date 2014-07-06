package com.tuneit.salsa3.ast;

import com.tuneit.salsa3.ast.serdes.ASTNodeSerdes;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesPlan;

public class Literal extends ASTNode {
	public enum Type {
		LIT_NULL,
		LIT_BOOLEAN,
		LIT_LONG,
		LIT_FLOAT,
		LIT_STRING,
		LIT_CHARACTER
	};
	
	private String token;
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

	@Override
	public String toString() {
		return "Literal [token=" + getEscapedToken() + ", type=" + type + "]";
	}
	
	private String getEscapedToken() {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < token.length(); ++i) {
			char c = token.charAt(i);
			
			switch(c) {
			case '\n':
				sb.append("\\n");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '"':
				sb.append("\\\"");
				break;
			case '\'':
				sb.append("\\\'");
				break;
			default:
				sb.append(c);
				break;
			}
		}
		
		return sb.toString();
	}
	
	/* Serialization code */
	static {
		ASTNodeSerdesPlan plan = ASTNodeSerdes.newPlan(Literal.class);
		plan.addEnumParam(0, "type", false, Type.class);
		plan.addStringParam(1, "token", false);
	}
}
