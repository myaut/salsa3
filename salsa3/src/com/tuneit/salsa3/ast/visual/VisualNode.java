package com.tuneit.salsa3.ast.visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;

public class VisualNode {
	private static class VisualString {
		public enum Style {
			STYLE_NORMAL,
			STYLE_HEADER,
			STYLE_SMALL
		}
		
		public static final Font smallFont = new Font("Verdana", Font.PLAIN, 12);
		public static final Font normalFont = new Font("Verdana", Font.PLAIN, 14);
		public static final Font headerFont = new Font("Verdana", Font.BOLD, 16);
		
		public int x;
		public int y;
		public int width;
		public int height;
		
		public String string;
		
		public Style style;
		public Font font;
		
		public VisualString(Style style, String string) {
			this.x = 0;
			this.y = 0;
			this.height = 0;
			this.width = 0;
			
			this.style = style;
			switch(style) {
			case STYLE_NORMAL:
				this.font = normalFont;
				break;
			case STYLE_HEADER:
				this.font = headerFont;
				break;
			case STYLE_SMALL:
				this.font = smallFont;
				break;
			}
			
			this.string = string;
		}
		
		public void setBaseXY(Graphics graphics, int x, int y) {
			final FontMetrics metrics = graphics.getFontMetrics(font);
			
			this.height = metrics.getHeight();
			this.width = metrics.stringWidth(string);
			
			this.x = x;
			this.y = y + this.height;			
		}
		
		public void render(Graphics graphics) {
			graphics.setFont(font);
			graphics.drawString(string, x, y);
		}
	};
	
	public static final String PARAM_DELIMITER = " => "; 
	
	public static final int XMARGIN = 10;
	public static final int YMARGIN = 10;
	public static final int INDENT = 20;
	public static final int PADDING = 8;	
	
	private VisualString name;
	private VisualString key;
	
	private List<VisualString> params;
	
	private List<VisualNode> rightNodes;
	private List<VisualNode> bottomNodes;
	
	/* Graphics attributes */
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	/* Including all rightNodes and margin */
	private int extendedWidth = 0;
	private int extendedHeight = 0;
	
	private boolean isFakeNode;
	
	public VisualNode(String name) {		
		this.isFakeNode = (name.length() == 0);
		
		this.name = new VisualString(VisualString.Style.STYLE_HEADER, name);
		
		this.key = null;
		
		this.params = new ArrayList<VisualString>();
		
		this.rightNodes = new ArrayList<VisualNode>();
		this.bottomNodes = new ArrayList<VisualNode>();
	}
	
	public VisualString getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = new VisualString(VisualString.Style.STYLE_SMALL, key);
	}
	
	public void addParam(String key, String value) {
		params.add(new VisualString(VisualString.Style.STYLE_NORMAL, key + PARAM_DELIMITER + value));
	}
	
	public void addNodeToRight(String key, VisualNode node) {
		node.setKey(key);
		rightNodes.add(node);
	}
	
	public void addNodeToBottom(VisualNode node) {
		bottomNodes.add(node);
	}
	
	public int getExtendedWidth() {
		return extendedWidth;
	}
	
	public int getExtendedHeight() {
		return extendedHeight;
	}
	
	public boolean isFakeNode() {
		return isFakeNode;
	}
	
	public int getLeftX() {
		if(isFakeNode) {
			return getMiddleX();
		}
		
		return x;
	}
	
	public int getTopY() {
		return y;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getMiddleX() {
		return x + width / 2;
	}
	
	public int getMiddleY() {
		return y + height / 2;
	}
	
	public void setBaseXY(Graphics graphics, int baseX, int baseY) {
		/* Set position of this node and it's subnodes (via recursive
		 * call of setBaseXY in subnodes). */
		
		if(isFakeNode) {
			/* Merge fake nodes with only one subnode */
			if(rightNodes.isEmpty() && bottomNodes.size() == 1) {
				VisualNode vn = bottomNodes.get(0);
				
				name = vn.name;
				key = vn.key;
				
				params = vn.params;
				
				rightNodes = vn.rightNodes;
				bottomNodes = vn.bottomNodes;
				
				isFakeNode = false;
			}
		}
		
		this.x = baseX; this.y = baseY;
		
		baseX += PADDING; baseY += PADDING;
		name.setBaseXY(graphics, baseX, baseY);
		
		/* Calculate width and height */		
		width = name.width;
		baseY += name.height;
		
		for(VisualString param : params) {			
			param.setBaseXY(graphics, baseX, baseY);
			
			if(param.width > width) {
				width = param.width;
			}
			
			baseY += param.height;
		}
		
		baseY += PADDING;
		width += 2 * PADDING;
		height = baseY - this.y;	
		
		// System.out.println(name.string + "@" + x + ":" + y + " w:" + width  + " h:" + height);
		
		/* Calculate extended (with subnodes) w/h */	
		int tmpW = 0;
		int baseNodeX = 0;
		
		extendedWidth = width + XMARGIN;
		extendedHeight = height + YMARGIN;
		
		baseX = this.x + extendedWidth;
		baseY = this.y;
		
		for(VisualNode rightNode : rightNodes) {
			// System.out.println("> " + "@" + baseX + ":" + baseY);
			
			baseNodeX = baseX + XMARGIN;
			
			if(rightNode.getKey() != null) {
				rightNode.getKey().setBaseXY(graphics, baseX, baseY);
				baseNodeX += rightNode.getKey().width;
			}
			
			rightNode.setBaseXY(graphics, baseNodeX, baseY);
			
			tmpW = baseNodeX - x + XMARGIN + rightNode.getExtendedWidth();			
			if(tmpW > extendedWidth) {		
				extendedWidth = tmpW;
			}
			
			baseY += rightNode.getExtendedHeight() + YMARGIN;
		}
		
		if(baseY < (this.y + extendedHeight)) {
			baseY = this.y + extendedHeight;
		}
		
		baseX = this.x + INDENT;
		
		for(VisualNode bottomNode : bottomNodes) {
			// System.out.println("v " + "@" + baseX + ":" + baseY);
			
			bottomNode.setBaseXY(graphics, baseX, baseY);
				
			if(bottomNode.getExtendedWidth() > extendedWidth) {				
				extendedWidth = bottomNode.getExtendedWidth();
			}
			
			baseY += bottomNode.getExtendedHeight() + YMARGIN;
		}
		
		extendedHeight = baseY - this.y;
		
		// System.out.println(name.string + "@" + x + ":" + y);
		// System.out.println("\text: " + this.extendedWidth + ":" + this.extendedHeight);
	}
	
	public void render(Graphics graphics) {		
		if(!isFakeNode) {
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillRect(x, y, width, height);
			
			graphics.setColor(Color.BLACK);
			graphics.drawRect(x, y, width, height);
			name.render(graphics);
		}
		
		if(key != null) {
			key.render(graphics);
		}
		
		for(VisualString param : params) {
			param.render(graphics);
		}
		
		if(!rightNodes.isEmpty()) {
			renderRightNodes(graphics);
		}
		
		if(!bottomNodes.isEmpty()) {
			renderBottomNodes(graphics);
		}
	}
	
	public void renderRightNodes(Graphics graphics) {
		/* x1  x2  x3
		 * ----|	   y1		
		 *     |
		 *     |___    y2
		 */
		
		int y1 = getMiddleY();
		int y2 = 0;
		int y3 = 0;
		
		int x1 = x + width;
		int x2 = x1 + XMARGIN / 2;
		int x3 = 0;	
		
		boolean haveLinesAtY1 = false;
		
		for(VisualNode rightNode : rightNodes) {
			x3 = rightNode.getLeftX();
			y2 = rightNode.getMiddleY();
			
			/* Render a pretty line from current node to right node
			 * If they are at the same level, pick lowest middle Y
			 * and render that line,
			 * otherwise, render glyph '----' (see above), and two glyphs,
			 * that are forming L (see above). 
			 * 
			 * Glyph '----' is rendered once that is controlled through 
			 * haveLinesAtY1 variable.
			 * */
			if(rightNode.getTopY() < y1) {
				/* same level */
				if(y1 < y2) {
					y3 = y1;
					graphics.drawLine(x2, y1, x3, y1);
					haveLinesAtY1 = true;
				}
				else {
					y3 = y2;
					graphics.drawLine(x1, y2, x3, y2);
				}
			}
			else {
				y3 = y2;
				graphics.drawLine(x2, y1, x2, y2);
				graphics.drawLine(x2, y2, x3, y2);
				haveLinesAtY1 = true;
			}
			
			if(rightNode.getKey() != null) {
				rightNode.getKey().setBaseXY(graphics, x2 + XMARGIN, y3);
			}
			
			rightNode.render(graphics);
		}
		
		if(haveLinesAtY1) {
			graphics.drawLine(x1, y1, x2, y1);
		}
	}
	
	public void renderBottomNodes(Graphics graphics) {
		/* x1   x2
		 * |      y1
		 * |_____ y2
		 * */
		
		int x1 = this.x + INDENT / 2;
		int x2 = this.x + INDENT;
		int y1 = 0;
		int y2 = 0;
		
		if(isFakeNode) {
			y1 = getMiddleY();
		}
		else {
			y1 = this.y + this.height;
		}
		
		for(VisualNode bottomNode : bottomNodes) {
			y2 = bottomNode.getMiddleY();

			graphics.drawLine(x1, y1, x1, y2);
			graphics.drawLine(x1, y2, x2, y2);
			
			y1 = y2;
			
			bottomNode.render(graphics);
			
		}
	}
}
