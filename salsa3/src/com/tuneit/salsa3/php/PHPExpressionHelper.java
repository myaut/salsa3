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

	private static final int ZEND_PRE_INC = 34;
	private static final int ZEND_PRE_DEC = 35;
	private static final int ZEND_POST_INC = 36;
	private static final int ZEND_POST_DEC = 37;
	
	private static final int ZEND_ISSET = 0x02000000;
	private static final int ZEND_ISEMPTY = 0x01000000;
	
	private static final int IS_NULL = 0;
	private static final int IS_LONG = 1;
	private static final int IS_DOUBLE = 2;
	private static final int IS_BOOL = 3;
	private static final int IS_ARRAY = 4;
	private static final int IS_OBJECT = 5;
	private static final int IS_STRING = 6;

	private static final int ZEND_BW_NOT = 12;
	private static final int ZEND_BOOL_NOT = 13;
	
	// private static ASTNode genericNode = new ASTNode();
	
	/**
	 * 
	 * @param state
	 * @return null if state was not handled, or created node
	 * @throws ParserException
	 */
	public static PHPParserHandler handleState(PHPParserState state, PHPParserHandler handler) throws ParserException {
		if(state.isState("binary_op")) {
			return PHPExpressionHelper.handleBinaryOperation(state, handler);
		}
		else if(state.isState("boolean_and_end")) {
			return PHPExpressionHelper.handleBooleanOperation(state, handler, BinaryOperation.Type.BOP_LOGICAL_AND);
		}
		else if(state.isState("boolean_or_end")) {
			return PHPExpressionHelper.handleBooleanOperation(state, handler, BinaryOperation.Type.BOP_LOGICAL_OR);
		}
		else if(state.isState("incdec")) {
			return PHPExpressionHelper.handleIncrementDecrement(state, handler);
		}
		else if(state.isState("add_function")) {
			return PHPExpressionHelper.handleUnaryAddSub(state, handler, UnaryOperation.Type.UOP_PLUS);
		}
		else if(state.isState("sub_function")) {
			return PHPExpressionHelper.handleUnaryAddSub(state, handler, UnaryOperation.Type.UOP_MINUS);
		}
		else if(state.isState("unary_op")) {
			return PHPExpressionHelper.handleUnaryOperation(state, handler);
		}
		else if(state.isState("qm_op")) {
			return PHPExpressionHelper.handleConditionalExpression(state, handler);
		}
		else if(state.isState("qm_true")) {
			return PHPExpressionHelper.handleConditionalExpressionTrue(state, handler);
		}
		else if(state.isState("qm_false")) {
			return PHPExpressionHelper.handleConditionalExpressionFalse(state, handler);
		}
		else if(state.isState("begin_function_call") || 
				state.isState("begin_class_member_function_call") || 
				state.isState("begin_method_call") ||
				state.isState("begin_new_object") ||
				state.isState("begin_dynamic_function_call")) {
			return PHPExpressionHelper.handleFunctionCall(state, handler);
		}
		else if(state.isState("array_dim")) {
			return PHPExpressionHelper.handleArrayIndexAccess(state, handler);
		}
		else if(state.isState("fetch_string_offset")) {
			return PHPExpressionHelper.handleStringIndexAccess(state, handler);
		}
		else if(state.isState("isset_or_isempty")) {
			return PHPExpressionHelper.handleIssetOrIsempty(state, handler);
		}
		else if(state.isState("fetch_constant")) {
			return PHPExpressionHelper.handleConstant(state, handler);
		}
		else if(state.isState("fetch_static_member")) {
			return PHPExpressionHelper.handleStaticMember(state, handler);
		}
		else if(state.isState("build_namespace_name")) {
			return PHPExpressionHelper.handleNamespaceName(state, handler);
		}
		else if(state.isState("print")) {
			return PHPExpressionHelper.handlePrint(state, handler);
		}
		else if(state.isState("exit")) {
			return PHPExpressionHelper.handleExit(state, handler);
		}
		else if(state.isState("init_array")) {
			/* ArrayLiteral already created in ZNode2AST */
			if(state.hasNode("expr")) {
				return PHPExpressionHelper.handleArrayElement(state, handler);
			}
		}
		else if(state.isState("add_array_element") || state.isState("add_static_array_element")) {
			return PHPExpressionHelper.handleArrayElement(state, handler);
		}
		else if(state.isState("cast")) {
			return PHPExpressionHelper.handleCast(state, handler);
		}
		else if(state.isState("fetch_class")) {
			return PHPExpressionHelper.handleFetchClass(state, handler);
		}
		else if(state.isState("pop_object")) {
			return PHPExpressionHelper.handlePopObject(state, handler);
		}
		else if(state.isState("fetch_property")) {
			return PHPExpressionHelper.handleFetchProperty(state, handler);
		}
		else if(state.isState("instanceof")) {
			return PHPExpressionHelper.handleInstanceOf(state, handler);
		}
		else if(state.isState("clone")) {
			return PHPExpressionHelper.handleClone(state, handler);
		}
		else if(state.isGenericState()) {
			return handler;
		}
		
		return handler;
	}

	private static PHPParserHandler handlePrint(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode arg = state.getNode("arg");		
		FunctionCall fcall = PHPExpressionHelper.handleSpecialFunction(state, handler, "print");
		
		fcall.addArgument(arg);
		
		return handler;
	}
	
	private static PHPParserHandler handleExit(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode message = state.getNodeOptional("message");		
		FunctionCall fcall = PHPExpressionHelper.handleSpecialFunction(state, handler, "exit");
		
		if(message != null) {
			fcall.addArgument(message);
		}
		
		return handler;
	}
	
	private static FunctionCall handleSpecialFunction(PHPParserState state, PHPParserHandler handler, String name) throws ParserException {
		ASTNode result = state.getNode("result");
		
		FunctionCall fcall = new FunctionCall(new FunctionName(name)); 				
		
		result.setNode(fcall);		
		addChildToHandler(fcall, handler);
		
		return fcall;
	}
	
	private static PHPParserHandler handleBinaryOperation(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode op1 = state.getNode("op1");
		ASTNode op2 = state.getNode("op2");
		
		int bopType = state.getIntParam("op");
		
		handleBinaryOperationHelper(getBinaryOpType(bopType), result, op1, op2);
		
		return handler;
	}
	
	private static PHPParserHandler handleBooleanOperation(PHPParserState state, PHPParserHandler handler, 
					BinaryOperation.Type opType) throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode expr1 = state.getNode("expr1");
		ASTNode expr2 = state.getNode("expr2");
		
		handleBinaryOperationHelper(opType, result, expr1, expr2);
		
		return handler;
	}
	
	private static void handleBinaryOperationHelper(BinaryOperation.Type opType, ASTNode result, ASTNode op1, ASTNode op2) throws ParserException {
		if(result == op1) {
			op1 = op1.cloneNode();
		}
		else if(result == op2) {
			op2 = op2.cloneNode();
		}
		
		BinaryOperation bop = new BinaryOperation(opType, op1, op2);
		
		result.setNode(bop);
	}
	
	private static PHPParserHandler handleIncrementDecrement(PHPParserState state, PHPParserHandler handler) throws ParserException {
		int incdecType = state.getIntParam("op");
		
		ASTNode op = state.getNode("op1");
		ASTNode result = state.getNode("result");
		
		op = op.cloneNode();
		
		UnaryOperation uop = new UnaryOperation(getIncdecOpType(incdecType), op);
		
		result.setNode(uop);
		addChildToHandler(uop, handler);
		
		return handler;
	}
	
	private static PHPParserHandler handleUnaryAddSub(PHPParserState state, PHPParserHandler handler, UnaryOperation.Type type) throws ParserException {
		ASTNode op = state.getNode("z_op1");
		ASTNode result = state.getNode("z_result");
		
		op = op.cloneNode();
		
		UnaryOperation uop = new UnaryOperation(type, op);
		
		result.setNode(uop);
		
		return handler;
	}
	
	private static PHPParserHandler handleUnaryOperation(PHPParserState state, PHPParserHandler handler) throws ParserException {
		int uopType = state.getIntParam("op");
		
		ASTNode op = state.getNode("op1");
		ASTNode result = state.getNode("result");
		
		op = op.cloneNode();
		
		UnaryOperation uop = new UnaryOperation(getUnaryOpType(uopType), op);
		
		result.setNode(uop);
		
		return handler;
	}
	
	private static PHPParserHandler handleConditionalExpression(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode qmToken = state.getNode("qm_token");
		ASTNode condition = state.getNode("cond");
		
		ConditionalExpression condExpr = new ConditionalExpression(condition);
		
		qmToken.setNode(condExpr);
		
		return handler;
	}
	
	private static PHPParserHandler handleConditionalExpressionTrue(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode qmToken = state.getNode("qm_token");
		ASTNode trueValue = state.getNode("true_value");
		
		ConditionalExpression condExpr = (ConditionalExpression) qmToken;
		
		condExpr.setExpressionTrue(trueValue);
		
		return handler;
	}

	private static PHPParserHandler handleConditionalExpressionFalse(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode qmToken = state.getNode("qm_token");
		ASTNode result = state.getNode("result");
		ASTNode falseValue = state.getNode("false_value");
		
		ConditionalExpression condExpr = (ConditionalExpression) qmToken;
		
		condExpr.setExpressionFalse(falseValue);
		
		result.setNode(condExpr);
		
		return handler;
	}
	
	private static PHPParserHandler handleFunctionCall(PHPParserState state, PHPParserHandler handler) throws ParserException {
		PHPFunctionCall phpFunctionCall = new PHPFunctionCall(handler);
		PHPParserHandler newHandler = phpFunctionCall.handleState(state);
		
		ASTNode fcall = phpFunctionCall.getRootNode();
		
		addChildToHandler(fcall, handler);
		
		return newHandler;
	}
	
	private static PHPParserHandler handleArrayIndexAccess(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode parent = state.getNode("parent");
		ASTNode dim = state.getNodeOptional("dim");
		
		if(dim != null) {
			ArrayIndex arrayIndex = new ArrayIndex(parent, dim);		
			result.setNode(arrayIndex);
		}
		else {
			PHPAddToArray addToArray = new PHPAddToArray(parent);
			result.setNode(addToArray);
		}
		
		return handler;
	}
	
	private static PHPParserHandler handleStringIndexAccess(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode parent = state.getNode("parent");
		ASTNode offset = state.getNode("offset");
		
		ArrayIndex arrayIndex = new ArrayIndex(parent, offset);		
		result.setNode(arrayIndex);
		
		return handler;
	}
	
	private static PHPParserHandler handleIssetOrIsempty(PHPParserState state, PHPParserHandler handler) throws ParserException {
		int type = state.getIntParam("type");
		
		ASTNode result = state.getNode("result");
		ASTNode var = state.getNode("variable");
		
		ASTNode function;
		
		/* Treat isset/empty operator as a special function call */
		if(type == ZEND_ISSET) {
			function = new FunctionName("isset");
		}
		else if(type == ZEND_ISEMPTY) {
			function = new FunctionName("empty");
		}
		else {
			throw new ParserException("Unknown isset/isempty type");
		}
		
		FunctionCall fcall = new FunctionCall(function); 			
		fcall.addArgument(var);
		
		result.setNode(fcall);
		
		return handler;
	}
	
	private static PHPParserHandler handleConstant(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		Literal constantNameNode = (Literal) state.getNode("constant_name");
		
		String constantName = constantNameNode.getToken();
		
		result.setNode(new Constant(constantName));
		
		return handler;
	}
	
	private static PHPParserHandler handleStaticMember(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		Variable varResult = (Variable) result;
		Literal classNameNode = (Literal) state.getNode("class_name");
		
		String className = classNameNode.getToken();
		
		StaticClassMember member = new StaticClassMember(varResult.getVarName());
		member.addClassName(className);
		
		result.setNode(member);		
		
		return handler;
	}
	
	private static PHPParserHandler handleNamespaceName(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		Literal name = (Literal) state.getNode("name");
		
		if(result instanceof NamespaceName) {
			NamespaceName nsn = (NamespaceName) result;
			
			nsn.addComponent(name.getToken());
		}
		else {
			/* First component of a namespace */
			NamespaceName nsn = new NamespaceName();
			Literal prefix = (Literal) state.getNode("prefix");
			
			nsn.addComponent(prefix.getToken());
			nsn.addComponent(name.getToken());
			
			result.setNode(nsn);
		}
		
		return handler;
	}
	
	private static void addChildToHandler(ASTNode child, PHPParserHandler handler) throws ParserException {
		ASTNode rootNode = handler.getRootNode();
		
		if(rootNode instanceof ASTStatement) {
			ASTStatement stmt = (ASTStatement) rootNode;
			stmt.addChild(child);
		} 
	}
	
	private static PHPParserHandler handleArrayElement(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode value = state.getNode("expr");
		ASTNode key = state.getNodeOptional("offset");
		ArrayLiteral array = null;
		
		if(result instanceof ArrayLiteral) {
			array = (ArrayLiteral) result;
		}
		else {
			array = new ArrayLiteral();
			result.setNode(array);
		}
		
		ArrayLiteral.Element el = new ArrayLiteral.Element(value, key);		
		array.addElement(el);
		
		return handler;
	}
	
	private static PHPParserHandler handleCast(PHPParserState state, PHPParserHandler handler) throws ParserException {
		int type = state.getIntParam("type");
		
		ASTNode expr = state.getNode("expr");
		ASTNode result = state.getNode("result");
		
		String typeName = PHPExpressionHelper.getCastType(type);
		Cast cast = new Cast(new TypeName(typeName), expr);
		
		result.setNode(cast);
		
		return handler;
	}
	
	private static PHPParserHandler handleFetchClass(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode className = state.getNode("class_name");
		
		result.setNode(className);
		
		return handler;
	}
	
	private static PHPParserHandler handlePopObject(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode object = state.getNode("object");
		
		if(object instanceof Variable) {
			Variable variable = (Variable) object;
			
			if(variable.getVarName().equals("this")) {
				VariableThis varThis = new VariableThis();
				variable.setNode(varThis);
			}
		}
		
		return handler;
	}
	
	private static PHPParserHandler handleFetchProperty(PHPParserState state, PHPParserHandler handler) throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode object = state.getNode("object");
		ASTNode propertyNode = state.getNode("property");
		
		if(propertyNode instanceof Literal) {
			Literal property = (Literal) propertyNode;
			InstanceMember instanceMember = new InstanceMember(object, property.getToken());
			result.setNode(instanceMember);
		}
		else {
			DynamicInstanceMember instanceMember = new DynamicInstanceMember(object, propertyNode);
			result.setNode(instanceMember);
		}
		
		return handler;
	}
	
	private static PHPParserHandler handleInstanceOf(PHPParserState state, PHPParserHandler handler)  throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode expression = state.getNode("expr");
		Literal classNameNode = (Literal) state.getNode("class_znode");
		
		String className = classNameNode.getToken();
		
		InstanceOf instanceOf = new InstanceOf(className, expression);
		
		result.setNode(instanceOf);
		
		return handler;
	}
	
	private static PHPParserHandler handleClone(PHPParserState state, PHPParserHandler handler)  throws ParserException {
		ASTNode result = state.getNode("result");
		ASTNode expression = state.getNode("expr");
		
		DynamicInstanceMember cloneFunction = 
				new DynamicInstanceMember(expression, new Literal(Literal.Type.LIT_STRING, "__clone"));
		FunctionCall cloneCall = new FunctionCall(cloneFunction);
		
		result.setNode(cloneCall);
		
		return handler;
	}
	
	private static UnaryOperation.Type getIncdecOpType(int uopType) throws ParserException {
		switch(uopType) {
		case ZEND_PRE_INC:
		        return UnaryOperation.Type.UOP_PRE_INCREMENT;
		case ZEND_PRE_DEC:
		        return UnaryOperation.Type.UOP_PRE_DECREMENT;
		case ZEND_POST_INC:
		        return UnaryOperation.Type.UOP_POST_INCREMENT;
		case ZEND_POST_DEC:
	        return UnaryOperation.Type.UOP_POST_DECREMENT;
		}
		
		throw new ParserException("Unsupported unary inc-dec type " + uopType + "!");
	}
	
	private static UnaryOperation.Type getUnaryOpType(int uopType) throws ParserException {
		switch(uopType) {
		case ZEND_BW_NOT:
		        return UnaryOperation.Type.UOP_BIT_NOT;
		case ZEND_BOOL_NOT:
		        return UnaryOperation.Type.UOP_LOGICAL_NOT;
		}
		
		throw new ParserException("Unsupported unary operation type " + uopType + "!");
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
	
	private static String getCastType(int type) throws ParserException  {
		switch(type) {
		case IS_LONG:
			return "int";
		case IS_DOUBLE:
			return "double";
		case IS_STRING:
			return "string";
		case IS_BOOL:
			return "bool";
		case IS_ARRAY:
			return "array";
		case IS_OBJECT:
			return "object";
		case IS_NULL:
			return "unset";
		}
		
		throw new ParserException("Unsupported cast type " + type + "!");
	}
}
