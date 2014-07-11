package com.tuneit.salsa3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.ASTStatement;
import com.tuneit.salsa3.ast.serdes.ASTNodeJSONDeserializer;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTStatementJSONDeserializer;
import com.tuneit.salsa3.ast.serdes.ASTStatementJSONSerializer;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerdes;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;
import com.tuneit.salsa3.ast.visual.ASTStatementVisualizer;
import com.tuneit.salsa3.ast.visual.VisualNode;
import com.tuneit.salsa3.php.*;

public final class PHPParser {
	static String phpParserBinary = "/pool/devel/salsa3/projects/parsers/php-parser/build/salsa3-php-parser";
	
	private String filePath;
	private PHPParserHandler handler;
	
	private ZNode2AST zNode2AST;
	
	public PHPParser(String filePath) {
		this.filePath = filePath;
		this.handler = new PHPRootHandler();
		this.zNode2AST = new ZNode2AST();
	}
	
	public void parse() throws ParserException, ASTNodeSerdesException {
		ProcessBuilder processBuilder = new ProcessBuilder(PHPParser.phpParserBinary, this.filePath);		
		processBuilder.redirectOutput(Redirect.PIPE);
		processBuilder.redirectError(Redirect.INHERIT);
		
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
					System.err.println("State " + state.state + " wasn't matched! ");
				}
				
				// System.out.println(handler + " " + state.state); 
			}
			
			int exitValue = process.waitFor();
			if(exitValue != 0) {
				throw new ParserException("Parser error: return code = " + exitValue);
			}
			
			
			ASTStatement root = (ASTStatement) handler.getRootNode();
			visualizeStatement(root);			
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
			ParserException pe = new ParserException("JSON error: " + line, je);
			throw pe;
		}
	}
	
	public void visualizeStatement(ASTStatement root) throws ASTNodeSerdesException, IOException {		
		ASTStatementVisualizer visualizer = new ASTStatementVisualizer();
		VisualNode vn = (VisualNode) root.serializeStatement(visualizer);
		
		Image tmpImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		vn.setBaseXY(tmpImage.getGraphics(), 0, 0);
		
		BufferedImage image = new BufferedImage(vn.getExtendedWidth(), vn.getExtendedHeight(), 
												BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics(); 
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, vn.getExtendedWidth(), vn.getExtendedHeight());
		g.setColor(Color.BLACK);
		vn.render(image.getGraphics());
		
		File outputfile = new File("ast.png");
		ImageIO.write(image, "png", outputfile);
	}
	
	public static void main(String[] args) throws ASTNodeSerdesException {
		PHPParser parser = new PHPParser(args[0]);
		
		try {
			parser.parse();
		}
		catch(ParserException pe) {
			pe.printStackTrace();
		}
	}
}
