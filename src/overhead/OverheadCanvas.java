package overhead;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;

import application.Globals;
import application.Tool;
import be.humphreys.simplevoronoi.GraphEdge;
import be.humphreys.simplevoronoi.Voronoi;
import utils.GfxUtils;

public class OverheadCanvas extends JPanel {
	private static final long serialVersionUID = 1L;
	public static final int CHUNK_OVERHEAD_WIDTH = 64;
	
	private static List<GraphEdge> graphEdges = new ArrayList<GraphEdge>();
	private Voronoi voronoi;
	private OverheadFrame frame;
	
	public OverheadCanvas(OverheadFrame frame) {
		voronoi = new Voronoi(1f);
		this.frame = frame;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Globals.COLOR_GENERAL_DARK);
		for(int i = 0; i < getWidth(); i += CHUNK_OVERHEAD_WIDTH) {
			for(int j = 0; j < getHeight(); j += CHUNK_OVERHEAD_WIDTH) {
				g.drawRect(i, j, CHUNK_OVERHEAD_WIDTH, CHUNK_OVERHEAD_WIDTH);
			}
		}
		if (Globals.tool == Tool.PATHTOOL && OverheadControl.getPreviousInstance() != null) {
			Point mousePos = getMousePosition();
			if (mousePos != null) {
				Point prevPos = OverheadControl.getPreviousInstance().point;
				g.setColor(frame.getSelected().getColor());
				g.drawLine(prevPos.x, prevPos.y, mousePos.x, mousePos.y);
			}
		}
		
		g.setColor(Globals.COLOR_GENERAL_LIGHT);
		Point mousePos = getMousePosition();
		if (mousePos != null) {
			GfxUtils.centeredOval(g, mousePos.x, mousePos.y, Globals.brushWidth * 16,
					Globals.brushWidth * 16);
		}
		
		/// Items
		
		for(Point point : OverheadControl.biomes.keySet()) {
			OverheadInstance itemInstance = OverheadControl.biomes.get(point);
			g.setColor(itemInstance.item.getColor());
			GfxUtils.centeredString(g, itemInstance.toString(), point.x, point.y);
		}
		
		buildVoronoi();
		drawVoronoi(g);
		
		for(Point point : OverheadControl.regions.keySet()) {
			OverheadInstance itemInstance = OverheadControl.regions.get(point);
			g.setColor(itemInstance.item.getColor());
			GfxUtils.centeredString(g, itemInstance.toString(), point.x, point.y);
			GfxUtils.centeredOval(g, point.x, point.y, itemInstance.getRadius(), itemInstance.getRadius());
		}
		
		for(Point point : OverheadControl.paths.keySet()) {
			OverheadInstance itemInstance = OverheadControl.paths.get(point);
			g.setColor(itemInstance.item.getColor());
			GfxUtils.centeredOval(g, point.x, point.y, 4, 4);
			
			if (itemInstance.hasNext()) {
				OverheadInstance next = itemInstance.getNext();
				g.drawLine(point.x, point.y, next.point.x, next.point.y);
			}
		}
	}

	public void buildVoronoi() {
		// TODO: can just read keyset
		Collection<OverheadInstance> biomes = OverheadControl.biomes.values();
		double[] xin = new double[biomes.size()];
		double[] yin = new double[biomes.size()];
		
		int i = 0;
		for(OverheadInstance biome : biomes) {
			xin[i] = biome.point.x;
			yin[i++] = biome.point.y;
		}
		
		if (xin.length == 0) return;
		
		clearVoronoi();
		graphEdges = voronoi.generateVoronoi(xin, yin, -4000, 4000, -4000, 4000);
	}
	
	private void drawVoronoi(Graphics g) {
		g.setColor(Color.WHITE);
		for(GraphEdge edge : graphEdges) {
			g.drawLine((int)edge.x1, (int)edge.y1, (int)edge.x2, (int)edge.y2);
		}
	}

	public void clearVoronoi() {
		graphEdges.clear();
	}
}