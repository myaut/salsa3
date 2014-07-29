package com.tuneit.salsa3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
	
	public static ASTStatement serdesStatement(ASTStatement root) throws ASTNodeSerdesException {
		ASTStatementSerializer serializer = new ASTStatementJSONSerializer();

		JSONObject jso = (JSONObject) root.serializeStatement(serializer);

		ASTStatement newRoot = ASTStatementSerdes.deserializeStatement(new ASTNodeJSONDeserializer(),
												new ASTStatementJSONDeserializer(), jso);

		return newRoot;		
	}
}
