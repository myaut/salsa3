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
	
	private static final int ZEND_FETCH_STATIC = 0x20000000;
	
	private static final int ZEND_BRK  = 50;
	private static final int ZEND_CONT  = 51;

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
		else if(state.isState("declare_constant")) {
			return this.handleConstant(state);
		}
		else if(state.isState("binary_assign_op")) {
			return this.handleAssignWithBinaryOp(state);
		}
		else if(state.isState("if_cond")) {
			return this.handleIfCondition(state);
		}
		else if(state.isState("switch_cond")) {
			return this.handleSwitch(state);
		}
		else if(state.isState("while_cond")) {
			return this.handleWhile(state);
		}
		else if(state.isState("do_while_begin")) {
			return this.handleDoWhile(state);
		}
		else if(state.isState("for_begin")) {
			return this.handleFor(state);
		}
		else if(state.isState("foreach_begin")) {
			return this.handleForeach(state);
		}
		else if(state.isState("brk_cont")) {
			return this.handleBreakContinue(state);
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
			PHPFinallyHandler finallyHandler = new PHPFinallyHandler(this);
			return finallyHandler.handleState(state);
		}
		else if(state.isState("throw")) {
			return this.handleThrow(state);
		}
		else if(state.isState("end_finally")) {
			/* Finally was not started, spurious state - ignore */
		}
		else if(state.isState("fetch_static_variable")) {
			return this.handleFetchStaticVariable(state);
		}
		
		return PHPExpressionHelper.handleState(state, this);
	}
	
	public PHPParserHandler handleEcho(PHPParserState state) throws ParserException {
		ASTNode arg = state.getNode("arg");
		
		/* Treat echo operator as a special function call */
		FunctionCall fcall = new FunctionCall(new FunctionName("echo")); 			
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
	
	public PHPParserHandler handleConstant(PHPParserState state) throws ParserException {
		Literal nameNode = (Literal) state.getNode("name");
		ASTNode value = state.getNode("value");
		
		String name = nameNode.getToken();
		Variable variable = new Variable(name);
		
		VariableDeclaration varDecl = new VariableDeclaration(variable, new TypeName("mixed"), value);
		varDecl.addTypeQualifier("const");
		rootNode.addChild(varDecl);
		
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
	
	private PHPParserHandler handleIfCondition(PHPParserState state) throws ParserException {
		PHPIfHandler ifHandler = new PHPIfHandler(this);
		PHPParserHandler newHandler = ifHandler.handleState(state);
		
		ASTNode ifNode = ifHandler.getRootNode();
		
		rootNode.addChild(ifNode);
		
		return newHandler;
	}
	
	private PHPParserHandler handleWhile(PHPParserState state) throws ParserException {
		PHPWhileHandler whileHandler = new PHPWhileHandler(this);
		PHPParserHandler newHandler = whileHandler.handleState(state);
		
		ASTNode whileNode = whileHandler.getRootNode();
		
		rootNode.addChild(whileNode);
		
		return newHandler;
	}
	
	private PHPParserHandler handleDoWhile(PHPParserState state) throws ParserException {
		PHPDoWhileHandler doWhileHandler = new PHPDoWhileHandler(this);
		PHPParserHandler newHandler = doWhileHandler.handleState(state);
		
		ASTNode doWhileNode = doWhileHandler.getRootNode();
		
		rootNode.addChild(doWhileNode);
		
		return newHandler;
	}
	
	private PHPParserHandler handleBreakContinue(PHPParserState state) throws ParserException {
		int op = state.getIntParam("op");
		ASTNode nestingNode = state.getNodeOptional("expr");
		ASTNode stmt = null;
		int nesting = 1;
		
		if(nestingNode != null) {
			Literal litNesting = (Literal) nestingNode;
			try {
				nesting = Integer.valueOf(litNesting.getToken());
			}
			catch(NumberFormatException e) {
				throw new ParserException("Invalid nesting expression: " + litNesting.getToken() + "!", e);
			}
		}
		
		if(op == ZEND_BRK) {
			if(nestingNode == null) {
				stmt = new BreakStatement();
			}
			else {				
				stmt = new BreakStatement(nesting);
			}
		}
		else if(op == ZEND_CONT) {
			if(nestingNode == null) {
				stmt = new ContinueStatement();
			}
			else {
				stmt = new ContinueStatement(nesting);
			}
		} 
		else {
			throw new ParserException("Unknown break/continue op " + op + "!");
		}
		
		rootNode.addChild(stmt);
		
		return this;
	}
	
	private PHPParserHandler handleFor(PHPParserState state) throws ParserException {
		PHPForHandler forHandler = new PHPForHandler(this);
		PHPParserHandler newHandler = forHandler.handleState(state);
		
		ASTNode forNode = forHandler.getRootNode();
		
		rootNode.addChild(forNode);
		
		return newHandler;
	}
	
	private PHPParserHandler handleForeach(PHPParserState state) throws ParserException {
		PHPForeachHandler foreachHandler = new PHPForeachHandler(this);
		PHPParserHandler newHandler = foreachHandler.handleState(state);
		
		ASTNode forNode = foreachHandler.getRootNode();
		
		rootNode.addChild(forNode);
		
		return newHandler;
	}
	
	private PHPParserHandler handleSwitch(PHPParserState state) throws ParserException {
		PHPSwitchHandler switchHandler = new PHPSwitchHandler(this);
		PHPParserHandler newHandler = switchHandler.handleState(state);
		
		ASTNode switchNode = switchHandler.getRootNode();
		
		rootNode.addChild(switchNode);
		
		return newHandler;
	}
	
	private PHPParserHandler handleUse(PHPParserState state) throws ParserException {
		ASTNode nsNameNode = state.getNode("ns_name");
		ASTNode nsAliasNode = state.getNodeOptional("new_name");
		
		UseStatement useNode = new UseStatement(toNamespaceName(nsNameNode), 
											    toNamespaceName(nsAliasNode));
		
		rootNode.addChild(useNode);
		
		return this;
	}
	
	private NamespaceName toNamespaceName(ASTNode node) {
		if(node != null && node instanceof Literal) {
			String nsName = ((Literal) node).getToken();
			NamespaceName nsNode = new NamespaceName();
			
			nsNode.addComponent(nsName);
			
			return nsNode;
		}
		
		return (NamespaceName) node;
	}

	private PHPParserHandler handleTry(PHPParserState state) throws ParserException {
		PHPTryHandler tryHandler = new PHPTryHandler(this);
		PHPParserHandler newHandler = tryHandler.handleState(state);
		
		ASTNode tryNode = tryHandler.getRootNode();
		
		rootNode.addChild(tryNode);
		
		return newHandler;
	}
	
	private PHPParserHandler handleThrow(PHPParserState state) throws ParserException {
		ASTNode throwObject = state.getNode("expr");		
		Throw throwStatement = new Throw(throwObject);
		
		rootNode.addChild(throwStatement);
		
		return this;
	}
	
	private PHPParserHandler handleFetchStaticVariable(PHPParserState state) throws ParserException {
		int fetchType = state.getIntParam("fetch_type");
		
		Variable varName = (Variable) state.getNode("varname");
		ASTNode staticAssignment = state.getNodeOptional("static_assignment");
	
		VariableDeclaration varDecl = null;
		
		if(fetchType != ZEND_FETCH_STATIC) {
			throw new ParserException("Unknown static variable fetch type " + fetchType + "!");
		}
		
		if(staticAssignment == null) {
			varDecl = new VariableDeclaration(varName, new TypeName("mixed"));
		}
		else {
			varDecl = new VariableDeclaration(varName, new TypeName("mixed"), staticAssignment);
		}
		
		varDecl.addTypeQualifier("static");
		
		rootNode.addChild(varDecl);
		
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
