package overhead;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Globals;
import application.Tool;
import heightmap.Heightmap;
import io.SaveData;
import io.data.DataChunk;
import io.data.DataFormat;

public class OverheadControl extends SaveData {
	public static Map<Point, OverheadInstance> biomes = new HashMap<Point, OverheadInstance>();
	public static Map<Point, OverheadInstance> regions = new HashMap<Point, OverheadInstance>();
	public static Map<Point, OverheadInstance> paths = new HashMap<Point, OverheadInstance>();
	
	private OverheadFrame frame;
	private int radius = 0;
	private static OverheadInstance previousInstance = null;
	
	private Point mousePrev = null;
	final int REDRAW_SIZE = 128;
	
	public OverheadControl(OverheadFrame canvas) {
		super();
		this.frame = canvas;
		//canvas.paint(g);
		
		canvas.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				tick();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				tick();
			}
		});
		
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent e) {
				Point mPos = canvas.getPanel().getMousePosition();
				
				boolean lmb = e.getButton()==1;
				boolean rmb = e.getButton()==3;
				switch(Globals.tool) {
				case BIOMETOOL:
					if (rmb) {
						add(biomes, mPos);
					}
					else if (lmb)
						remove(biomes, mPos);
					
					break;
				case REGIONTOOL:
					if (rmb)
						add(regions, mPos, radius);
					else if (lmb)
						remove(regions, mPos);
					
					break;
				case PATHTOOL:
					if (rmb) {
						add(paths, mPos, previousInstance);
					} else if (lmb) {
						if (previousInstance != null) {
							endPathBuilding();
							canvas.repaint();
							
						} else {
							remove(paths, mPos);
						}
						
					}
					break;
				default:
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
	}
	
	private void tick() {
		radius = Math.max(2,Globals.brushWidth*16);

		if (frame.getPanel().getMousePosition() != null) {
			Point p = frame.getPanel().getMousePosition();
			
			if (Globals.tool == Tool.PATHTOOL && previousInstance != null) {
				frame.repaint();
			}
			else if (p != null) {
				frame.repaint(p.x-REDRAW_SIZE,p.y-REDRAW_SIZE,REDRAW_SIZE*2,REDRAW_SIZE*2);
				if (mousePrev != null) {
					frame.repaint(mousePrev.x-REDRAW_SIZE,mousePrev.y-REDRAW_SIZE,REDRAW_SIZE*2,REDRAW_SIZE*2);
				}
				
				mousePrev = p;
			}
			//canvas.repaint();
		}
	}
	
	public static void endPathBuilding() {
		previousInstance = null;
	}
	
	private void remove(Map<Point, OverheadInstance> map, Point pos) {
		if (pos == null) return;
		List<Point> pointsToRemove = new ArrayList<Point>();
		for(Point pt : map.keySet()) {
			if (pt.distance(pos) < 32) {
				pointsToRemove.add(pt);
			}
		}
		
		for(Point pt : pointsToRemove) {
			map.get(pt).detach();
			map.remove(pt);
		}
		frame.repaint();
	}

	private void add(Map<Point, OverheadInstance> map, Point pos) {
		OverheadItem item = frame.getSelected();
		if (item == null) return;
		
		map.put(pos, new OverheadInstance(item, pos));
		frame.repaint();
	}
	
	private void add(Map<Point, OverheadInstance> map, Point pos, int radius) {
		OverheadItem item = frame.getSelected();
		if (item == null) return;
		
		map.put(pos, new OverheadInstance(item, pos, radius));
		frame.repaint();
	}
	
	private void add(Map<Point, OverheadInstance> map, Point pos, OverheadInstance prev) {
		OverheadItem item = frame.getSelected();
		if (item == null) return;
		
		OverheadInstance itemInstance = new OverheadInstance(item, pos, prev);
		map.put(pos, itemInstance);
		previousInstance = itemInstance;
		frame.repaint();
	}

	public static OverheadInstance getPreviousInstance() {
		return previousInstance;
	}
	
	public static Point inWorldspace(Point point) {
		float scale = Heightmap.CHUNK_SIZE/(float)OverheadCanvas.CHUNK_OVERHEAD_WIDTH;
		return new Point((int)(point.x*scale), (int)(point.y*scale));
	}
	
	@Override
	public boolean createDataChunks() {
		for(OverheadInstance biome : biomes.values()) {
			DataChunk chunk = new DataChunk(DataFormat.FORMAT_BIOME);
			basicSaveData(chunk, biome);
			addDataChunk(chunk);
		}
		
		for(OverheadInstance region : regions.values()) {
			DataChunk chunk = new DataChunk(DataFormat.FORMAT_REGION);
			basicSaveData(chunk, region);
			chunk.put("radius", Short.toString((short)region.getRadius()));
			addDataChunk(chunk);
		}
		
		for(OverheadInstance path : paths.values()) {
			DataChunk chunk = new DataChunk(DataFormat.FORMAT_PATH);
			basicSaveData(chunk, path);
			chunk.put("index", Integer.toString(getPathIndex(path)));
			chunk.put("next", Integer.toString(
					path.hasNext()?getPathIndex(path.getNext()):-1
					));
			addDataChunk(chunk);
		}
		
		return true;
		
	}

	private int getPathIndex(OverheadInstance target) {
		int i = 0;
		boolean found = false;
		
		for(OverheadInstance path : paths.values()) {
			if (path == target) {
				found = true;
				break;
			} else {
				i++;
			}
		}

		return found ? i : -1;
	}

	private void basicSaveData(DataChunk chunk, OverheadInstance instance) {
		Point point = inWorldspace(instance.point);
		chunk.put("x", Integer.toString(point.x));
		chunk.put("z", Integer.toString(point.y));
		chunk.put("id", instance.toString());
	}

	@Override
	public void clear() {
		biomes.clear();
		regions.clear();
		paths.clear();
		frame.getPanel().clearVoronoi();
		frame.getPanel().repaint();
	}
}