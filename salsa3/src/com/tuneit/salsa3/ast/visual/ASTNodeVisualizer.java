package com.tuneit.salsa3.ast.visual;

import java.util.List;

import com.tuneit.salsa3.ast.Literal;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerializer;

public class ASTNodeVisualizer implements ASTNodeSerializer {

	@Override
	public Object createNode(String className) throws ASTNodeSerdesException {
		return new VisualNode(className);
	}

	@Override
	public void addToNode(Object node, String paramName, String paramShortName, Object value)
			throws ASTNodeSerdesException {
		VisualNode vn = (VisualNode) node;

		if(value instanceof VisualNode) {
			VisualNode vn2 = (VisualNode) value;
			vn.addNodeToRight(paramName, vn2);
		}
		else {
			vn.addParam(paramName, value.toString());
		}		
	}

	@Override
	public Object createList() throws ASTNodeSerdesException {
		return new VisualNode("");
	}

	@Override
	public void addToList(Object list, Object value)
			throws ASTNodeSerdesException {
		VisualNode vn = (VisualNode) list;
		
		if(value instanceof VisualNode) {
			VisualNode vn2 = (VisualNode) value;
			vn.addNodeToBottom(vn2);
		}
		else {
			vn.addParam("", value.toString());
		}
	}

	@Override
	public Object serializeLiteral(Literal literal)
			throws ASTNodeSerdesException {
		VisualNode vn = new VisualNode(literal.getType().toString());
		
		vn.addParam("", literal.getToken());
		
		return vn;
	}

}
