package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPExpressionHelper {	
	private static final int ZEND_ADD = 1;
	private static final int ZEND_SUB = 2;
	private static final int ZEND_MUL = 3;
	private static final int ZEND_DIV = 4;
	private static final int ZEND_MOD = 5;
	private static final int ZEND_SL = 6;
	private static final int ZEND_SR = 7;
	private static final int ZEND_CONCAT = 8;
	private static final int ZEND_BW_OR = 9;
	private static final int ZEND_BW_AND = 10;
	private static final int ZEND_BW_XOR = 11;
	private static final int ZEND_IS_IDENTICAL = 15;
	private static final int ZEND_IS_NOT_IDENTICAL = 16;
	private static final int ZEND_IS_EQUAL = 17;
	private static final int ZEND_IS_NOT_EQUAL = 18;
	private static final int ZEND_IS_SMALLER = 19;
	private static final int ZEND_IS_SMALLER_OR_EQUAL = 20;
	
	private static final int ZEND_ISSET = 0x02000000;
	private static final int ZEND_ISEMPTY = 0x01000000;

	private static ASTNode genericNode = new ASTNode();
	
	/**
	 * 
	 * @param state
	 * @return null if state was not handled, or created node
	 * @throws ParserException
	 */
	public static ASTNode handleState(PHPParserState state) throws ParserException {
		if(state.isState("binary_op")) {
			ASTNode result = state.getNode("result");
			ASTNode op1 = state.getNode("op1");
			ASTNode op2 = state.getNode("op2");
			
			int bopType = state.getIntParam("op");
			
			if(result == op1) {
				op1 = op1.cloneNode();
			}
			else if(result == op2) {
				op2 = op2.cloneNode();
			}
			
			BinaryOperation bop = new BinaryOperation(getBinaryOpType(bopType), op1, op2);
			
			result.setNode(bop);
			
			/* Result will be re-used in statement, so do not return it this time */
			return result;
		}
		else if(state.isState("array_dim")) {
			ASTNode result = state.getNode("result");
			ASTNode parent = state.getNode("parent");
			ASTNode dim = state.getNode("dim");
			
			ArrayElement arrayElement = new ArrayElement(parent, dim);
			
			result.setNode(arrayElement);
			
			return result;
		}
		else if(state.isState("isset_or_isempty")) {
			int type = state.getIntParam("type");
			
			ASTNode result = state.getNode("result");
			ASTNode var = state.getNode("variable");
			
			String functionName;
			
			/* Treat isset/empty operator as a special function call */
			if(type == ZEND_ISSET) {
				functionName = "isset";
			}
			else if(type == ZEND_ISEMPTY) {
				functionName = "empty";
			}
			else {
				throw new ParserException("Unknown isset/isempty type");
			}
			
			FunctionCall fcall = new FunctionCall(functionName); 			
			fcall.addArgument(var, false);
			
			result.setNode(fcall);
			
			return result;
		}
		else if(state.isGenericState()) {
			return genericNode;
		}
		
		return null;
	}
	
	private static BinaryOperation.Type getBinaryOpType(int bopType) throws ParserException {
		switch(bopType) {
		case ZEND_ADD:
		        return BinaryOperation.Type.BOP_ADD;
		case ZEND_SUB:
		        return BinaryOperation.Type.BOP_SUB;
		case ZEND_MUL:
		        return BinaryOperation.Type.BOP_MULTIPLY;
		case ZEND_DIV:
		        return BinaryOperation.Type.BOP_DIVIDE;
		case ZEND_MOD:
		        return BinaryOperation.Type.BOP_MODULO;
		case ZEND_SL:
		        return BinaryOperation.Type.BOP_SHIFT_LEFT;
		case ZEND_SR:
		        return BinaryOperation.Type.BOP_SHIFT_RIGHT;
		case ZEND_CONCAT:
		        return BinaryOperation.Type.BOP_ADD;
		case ZEND_BW_OR:
		        return BinaryOperation.Type.BOP_BIT_OR;
		case ZEND_BW_AND:
		        return BinaryOperation.Type.BOP_BIT_AND;
		case ZEND_BW_XOR:
		        return BinaryOperation.Type.BOP_BIT_XOR;
		case ZEND_IS_IDENTICAL:
		        return BinaryOperation.Type.BOP_EQUALS;
		case ZEND_IS_NOT_IDENTICAL:
		        return BinaryOperation.Type.BOP_NOT_EQUALS;
		case ZEND_IS_EQUAL:
		        return BinaryOperation.Type.BOP_EQUALS;
		case ZEND_IS_NOT_EQUAL:
		        return BinaryOperation.Type.BOP_NOT_EQUALS;
		case ZEND_IS_SMALLER:
		        return BinaryOperation.Type.BOP_LESS;
		case ZEND_IS_SMALLER_OR_EQUAL:
		        return BinaryOperation.Type.BOP_EQUALS_OR_LESS;

		}
		
		throw new ParserException("Unsupported binary optype " + bopType + "!");
	}
}
