package utils;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class GfxUtils {
	public static void centeredString(Graphics g, String text, int x, int y) {
		FontMetrics metrics = g.getFontMetrics(g.getFont());
	    int halfWidth = metrics.stringWidth(text) / 2;
	    int halfHeight = (metrics.getHeight() / 2) - metrics.getAscent();
	    
	    g.drawString(text, x - halfWidth, y - halfHeight);
	}

	public static void centeredOval(Graphics g, int x, int y, int width, int height) {
		g.drawOval(x - (width/2), y - (height/2), width, height);
	}
}
