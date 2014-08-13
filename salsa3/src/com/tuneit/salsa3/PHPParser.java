package com.tuneit.salsa3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.JSONException;

import com.tuneit.salsa3.ast.*;
import com.tuneit.salsa3.php.*;

public final class PHPParser implements SourceParser {
	public static Logger log = Logger.getLogger(PHPParser.class.getName());
	
	public static String phpParserBinary = 
			System.getProperty("com.tuneit.salsa3.PHPParser.phpParserBinary");
	public static boolean traceStateHandlers = 
			Boolean.parseBoolean(System.getProperty("com.tuneit.salsa3.PHPParser.traceStateHandlers", "false"));
	
	private String filePath;
	private PHPParserHandler handler;
	
	private ZNode2AST zNode2AST;
	
	public PHPParser(String filePath) {
		this.filePath = filePath;
		this.handler = new PHPRootHandler();
		this.zNode2AST = new ZNode2AST();
	}
	
	public ASTStatement parse() throws ParserException {
		ProcessBuilder processBuilder = new ProcessBuilder(PHPParser.phpParserBinary, this.filePath);		
		processBuilder.redirectOutput(Redirect.PIPE);
		processBuilder.redirectError(Redirect.PIPE);
		
		String line = "<NOJSON>";
		
		try {
			Process process = processBuilder.start();			
			InputStream inStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			
			/*
			 * Parse line, provided by salsa3-php-parser from JSON
			 * Each line is a state (call to zend_compile.c):
			 * 		salsa3_dump_znode is parsed as PHPParser.Znode,
			 * 		salsa3_dump_int_param is parsed as Integer
			 */
			while((line = reader.readLine()) != null) {
				JSONObject object = new JSONObject(line);
				PHPParserState state = new PHPParserState();
				
				state.lineNo = object.getInt("lineno");
				state.state = object.getString("state");
				
				for(String key : JSONObject.getNames(object)) {
					if("lineno".equals(key) || "state".equals(key))
						continue;
					
					Object element = object.get(key);
					
					if(element instanceof JSONObject) {
						JSONObject jsonZNode = (JSONObject) element;
						ZNode zNode = new ZNode();
						
						zNode.id = jsonZNode.getInt("nodeid");
						zNode.type = jsonZNode.optInt("type", -1);
						zNode.value = jsonZNode.optString("value", "");
						
						if(zNode.type == -1) {
							continue;
						}
						
						ASTNode node = zNode2AST.convert(zNode);
						
						state.nodes.put(key, node);
					}
					else if(element instanceof Integer) {
						state.intParams.put(key, (Integer) element);
					}
					else {
						ParserException pe = new ParserException("Unexpected JSON object");
						throw pe;
					}
				}
				
				handler = handler.handleState(state);
				
				if(!state.isMatched()) {
					throw new ParserException("State " + state.state + 
								" at line " + state.lineNo + " wasn't matched! ");
				}
				
				if(traceStateHandlers) {
					log.finest(handler + " " + state.state + "@" + state.lineNo);
				}
			}
			
			int exitValue = process.waitFor();
			if(exitValue != 0) {
				StringWriter writer = new StringWriter();
				IOUtils.copy(process.getErrorStream(), writer);				
				
				throw new ParserException("Parser error: return code = " + exitValue + 
						" stderr: " + writer.toString());
			}
						
			ASTStatement root = (ASTStatement) handler.getRootNode();
			root.filterReused();
			
			return postProcessRoot(root);	
		}
		catch(InterruptedException ie) {
			ParserException pe = new ParserException("Parser process was interrupted", ie);
			throw pe;
		}
		catch(IOException ioe) {
			ParserException pe = new ParserException("Internal error", ioe);
			throw pe;
		}
		catch(JSONException je) {
			ParserException pe = new ParserException("JSON error at " + line, je);
			throw pe;
		}
		catch(ClassCastException cce) {
			ParserException pe = new ParserException("Class cast exception for at " + line, cce);
			throw pe;
		}
	}
	
	private ASTStatement postProcessRoot(ASTStatement root) throws ParserException {
		ASTStatement newRoot = new ASTStatement();
		FunctionDeclaration module = new FunctionDeclaration("__module__", new TypeName("mixed"));
		
		for(ASTNode node : root.getChildren()) {
			if(node instanceof ClassDeclaration 
					|| node instanceof FunctionDeclaration 
					|| node instanceof VariableDeclaration
					|| node instanceof IncludeStatement) {
				newRoot.addChild(node);
				
				continue;
			}
			else if(node instanceof FunctionCall) {
				FunctionCall fcall = (FunctionCall) node;
				ASTNode funcNode = fcall.getFunction();
				
				/* FIXME: Have to be Token */
				if(funcNode instanceof Literal) {
					String funcName = ((Literal) funcNode).getToken();
					
					if(funcName.equals("define")) {
						try {
							Literal constantNameNode = 
									(Literal) fcall.getArguments().get(0).getArgument();
							ASTNode constantValue = 
									fcall.getArguments().get(1).getArgument();
							
							String constantName = constantNameNode.getToken();
							
							TypeName typeName = new TypeName("mixed");
							typeName.addTypeQualifier("const");
							
							newRoot.addChild(new VariableDeclaration(new Variable(constantName), 
									typeName, constantValue));
						}
						catch(Exception e) {
							throw new ParserException("Invalid define arguments", e);
						}
						
						continue;
					}
				}
			}
			else if(node.getClass().equals(Assign.class)) {
				Assign assign = (Assign) node;
				ASTNode left = assign.getLeft();
				ASTNode right = assign.getRight();
				
				if(left instanceof Variable && right instanceof Literal) {
					Variable variable = (Variable) left;
					newRoot.addChild(new VariableDeclaration(variable, 
							new TypeName("mixed"), assign.getRight()));
					
					continue;
				}
			}
			
			module.addChild(node);
		}
		
		newRoot.addChild(module);
		
		return newRoot;
	}
}

