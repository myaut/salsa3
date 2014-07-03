package com.tuneit.salsa3.php;

import com.tuneit.salsa3.ParserException;
import com.tuneit.salsa3.ast.*;

public class PHPStatementHandler implements PHPParserHandler {
	private ASTStatement rootNode = null;
	
	private static final int ZEND_ASSIGN_ADD  = 23;
	private static final int ZEND_ASSIGN_SUB  = 24;
	private static final int ZEND_ASSIGN_MUL  = 25;
	private static final int ZEND_ASSIGN_DIV  = 26;
	private static final int ZEND_ASSIGN_MOD  = 27;
	private static final int ZEND_ASSIGN_SL  = 28;
	private static final int ZEND_ASSIGN_SR  = 29;
	private static final int ZEND_ASSIGN_CONCAT  = 30;
	private static final int ZEND_ASSIGN_BW_OR  = 31;
	private static final int ZEND_ASSIGN_BW_AND  = 32;
	private static final int ZEND_ASSIGN_BW_XOR  = 33;
	
	private static final int ZEND_EVAL = 1 << 0;
	private static final int ZEND_INCLUDE = 1 << 1;
	private static final int ZEND_INCLUDE_ONCE = 1 << 2;
	private static final int ZEND_REQUIRE = 1 << 3;
	private static final int ZEND_REQUIRE_ONCE = 1 << 4;

	@Override
	public PHPParserHandler handleState(PHPParserState state) throws ParserException {
		if(state.isState("echo")) {
			return this.handleEcho(state);
		}
		else if(state.isState("assign")) {
			/* SALSA considers assign and binary_assign_op as statement. Despite the fact
			 * they may be used in expression context, i.e:
			 * 		f($a = 1)
			 * It would nicely split into two statements:
			 * 		$a = 1
			 * 		f($a)
			 * which is equivalent to upper example. This may be hacked like function calls did,
			 * but this approach is easier. */
			return this.handleAssign(state);
		}
		else if(state.isState("binary_assign_op")) {
			return this.handleAssignWithBinaryOp(state);
		}
		else if(state.isState("if_cond")) {
			return this.handleIfCondition(state);
		}
		else if(state.isState("include_or_eval")) {
			return this.handleIncludeOrEval(state);
		}
		else if(state.isState("use")) {
			return this.handleUse(state);
		}
		else if(state.isState("try")) {
			return this.handleTry(state);
		}
		else if(state.isState("finally")) {
			return this.handleBeginFinally(state);
		}
		else if(state.isState("throw")) {
			return this.handleThrow(state);
		}
		else if(state.isState("end_finally")) {
			/* Finally was not started, spurious state - ignore */
		}
		
		return PHPExpressionHelper.handleState(state, this);
	}
	
	public PHPParserHandler handleEcho(PHPParserState state) throws ParserException {
		ASTNode arg = state.getNode("arg");
		
		/* Treat echo operator as a special function call */
		FunctionCall fcall = new FunctionCall("echo"); 			
		fcall.addArgument(arg, false);
		
		rootNode.addChild(fcall);
		
		return this;
	}
	
	public PHPParserHandler handleAssign(PHPParserState state) throws ParserException {
		ASTNode value = state.getNode("value");
		ASTNode variable = state.getNode("variable");
		
		Assign assign = new Assign(variable, value);
		rootNode.addChild(assign);
		
		return this;
	}
	
	public PHPParserHandler handleAssignWithBinaryOp(PHPParserState state) throws ParserException {
		int op = state.getIntParam("op");
		ASTNode value = state.getNode("op2");
		ASTNode variable = state.getNode("op1");
		
		AssignWithBinaryOperation assign = 
				new AssignWithBinaryOperation(PHPStatementHandler.getAssignBinaryOpType(op), 
											  variable, value);
		rootNode.addChild(assign);
		
		return this;
	}
	
	public PHPParserHandler handleIncludeOrEval(PHPParserState state) throws ParserException {
		int type = state.getIntParam("type");
		ASTNode op1 = state.getNode("op1");
		
		switch(type) {
		case ZEND_EVAL:
			ForeignCode foreignCode = new ForeignCode(op1);
			rootNode.addChild(foreignCode);
			break;
		case ZEND_INCLUDE:
		case ZEND_REQUIRE:
			IncludeStatement includeStatement = new IncludeStatement(op1, false);
			rootNode.addChild(includeStatement);
			break;
		case ZEND_INCLUDE_ONCE:
		case ZEND_REQUIRE_ONCE:
			IncludeStatement includeStatementOnce = new IncludeStatement(op1, true);
			rootNode.addChild(includeStatementOnce);
			break;
		default:
			throw new ParserException("Unknown include_or_eval type: " + type + "!");
		}
		
		return this;
	}
	
	public PHPParserHandler handleIfCondition(PHPParserState state) throws ParserException {
		PHPIfHandler ifHandler = new PHPIfHandler(this);
		PHPParserHandler newHandler = ifHandler.handleState(state);
		
		ASTNode ifNode = ifHandler.getRootNode();
		
		rootNode.addChild(ifNode);
		
		return newHandler;
	}
	
	public PHPParserHandler handleUse(PHPParserState state) throws ParserException {
		ASTNode nsName = state.getNode("ns_name");
		ASTNode nsAlias = state.getNodeOptional("new_name");
		
		UseStatement useNode = new UseStatement((NamespaceName) nsName, (NamespaceName) nsAlias);
		
		rootNode.addChild(useNode);
		
		return this;
	}

	public PHPParserHandler handleTry(PHPParserState state) throws ParserException {
		PHPTryHandler tryHandler = new PHPTryHandler(this);
		PHPParserHandler newHandler = tryHandler.handleState(state);
		
		ASTNode tryNode = tryHandler.getRootNode();
		
		rootNode.addChild(tryNode);
		
		return newHandler;
	}
	
	public PHPParserHandler handleBeginFinally(PHPParserState state) throws ParserException {
		PHPFinallyHandler finallyHandler = new PHPFinallyHandler(this);
		PHPParserHandler newHandler = finallyHandler.handleState(state);
		
		return newHandler;
	}
	
	public PHPParserHandler handleThrow(PHPParserState state) throws ParserException {
		ASTNode throwObject = state.getNode("expr");		
		Throw throwStatement = new Throw(throwObject);
		
		rootNode.addChild(throwStatement);
		
		return this;
	}
	
	@Override
	public ASTNode getRootNode() {
		return rootNode;
	}	
	
	public void setRootNode(ASTStatement rootNode) {
		this.rootNode = rootNode;
	}
	
	private static BinaryOperation.Type getAssignBinaryOpType(int bopType) throws ParserException {
		switch(bopType) {
		case ZEND_ASSIGN_ADD:
		        return BinaryOperation.Type.BOP_ADD;
		case ZEND_ASSIGN_SUB:
		        return BinaryOperation.Type.BOP_SUB;
		case ZEND_ASSIGN_MUL:
		        return BinaryOperation.Type.BOP_MULTIPLY;
		case ZEND_ASSIGN_DIV:
		        return BinaryOperation.Type.BOP_DIVIDE;
		case ZEND_ASSIGN_MOD:
		        return BinaryOperation.Type.BOP_MODULO;
		case ZEND_ASSIGN_SL:
		        return BinaryOperation.Type.BOP_SHIFT_LEFT;
		case ZEND_ASSIGN_SR:
		        return BinaryOperation.Type.BOP_SHIFT_RIGHT;
		case ZEND_ASSIGN_CONCAT:
		        return BinaryOperation.Type.BOP_ADD;
		case ZEND_ASSIGN_BW_OR:
		        return BinaryOperation.Type.BOP_BIT_OR;
		case ZEND_ASSIGN_BW_AND:
		        return BinaryOperation.Type.BOP_BIT_AND;
		case ZEND_ASSIGN_BW_XOR:
		        return BinaryOperation.Type.BOP_BIT_XOR;	
		}
		
		throw new ParserException("Unsupported binary optype " + bopType + "!");
	}
}
