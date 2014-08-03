package com.tuneit.salsa3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

import com.tuneit.salsa3.ast.ASTStatement;
import com.tuneit.salsa3.ast.serdes.ASTNodeJSONDeserializer;
import com.tuneit.salsa3.ast.serdes.ASTNodeSerdesException;
import com.tuneit.salsa3.ast.serdes.ASTStatementJSONDeserializer;
import com.tuneit.salsa3.ast.serdes.ASTStatementJSONSerializer;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerdes;
import com.tuneit.salsa3.ast.serdes.ASTStatementSerializer;
import com.tuneit.salsa3.ast.visual.ASTStatementVisualizer;
import com.tuneit.salsa3.ast.visual.VisualNode;

public class ASTStatementUtils {
	public static void visualizeStatement(ASTStatement root, String destination) throws ASTNodeSerdesException, IOException {		
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
		
		File outputfile = new File(destination);
		ImageIO.write(image, "png", outputfile);
	}
	
	public static String serializeStatement(ASTStatement root, int indent, boolean useShortNames) throws ASTNodeSerdesException {
		ASTStatementSerializer serializer = new ASTStatementJSONSerializer(useShortNames);

		JSONObject jso = (JSONObject) root.serializeStatement(serializer);
		
		try {
			return jso.toString(indent);
		} catch (JSONException e) {
			throw new ASTNodeSerdesException("JSON error", e);
		}		
	}
	
	public static String serializeStatement(ASTStatement root) throws ASTNodeSerdesException {
		return serializeStatement(root, 0, true);
	}
	
	public static ASTStatement deserializeStatement(String ast, boolean useShortNames) throws ASTNodeSerdesException {
		try {
			JSONObject jso = new JSONObject(ast);
			
			ASTStatement newRoot = ASTStatementSerdes.deserializeStatement(new ASTNodeJSONDeserializer(useShortNames),
					new ASTStatementJSONDeserializer(), jso);

			return newRoot;	
		}
		catch(JSONException jse) {
			throw new ASTNodeSerdesException("JSON error", jse);
		}	
	}
}
