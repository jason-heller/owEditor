package heightmap;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import application.Globals;
import application.ToolShape;
import opengl.Camera;
import opengl.GLWindow;
import opengl.tex.Texture;
import utils.MathUtils;

public class Heightmap {
	public static int CHUNK_RENDER_VERTEX_SIZE = 96;
	public static int CHUNK_VERTEX_SIZE = 32;
	public static int POLYGON_WIDTH = 8;
	public static int CHUNK_SIZE = (CHUNK_RENDER_VERTEX_SIZE-1) * POLYGON_WIDTH;
	private static List<Float> base = new ArrayList<Float>();
	private static int baseInd = 0;

	//public static int CHUNK_RENDER_RADIUS = 3;
	private int loadX = Integer.MAX_VALUE, loadZ = Integer.MAX_VALUE;
	
	private static int yPlaneList = buildYPlaneList();
	private static int heightmapDisplayList = -1;
	private static boolean updateDisplayList = false;
	
	private static Texture yPlaneTex = Texture.create("*/res/yPlane.png", "NOMATERIAL", false);

	private HeightData heightData = new HeightData();

	public void addHeight(float x, float z, int radius, float amount, ToolShape shape) {
		int vertexX = ((int)x) / POLYGON_WIDTH;
		int vertexZ = ((int)z) / POLYGON_WIDTH;
		int radiusSquared = radius * radius;
		int amtSign = (int) Math.signum(amount);
		
		switch(shape) {
		case SQUARE:
			for (int i = -radius; i <= radius; i++) {
			    for (int j = -radius; j <= radius; j++) {
			    	if (Globals.smoothBrush) amount = (1f - Math.min(((float)Math.abs(i)/radius + (float)Math.abs(j)/radius), 1f)) * amtSign;
			    	addHeight(vertexX+i, vertexZ+j, amount);
			    }
			}
			break;
		default: // Circle
			for (int i = -radius; i <= radius; i++) {
			    for (int j = 0; (j*j) + (i*i) <= radiusSquared; j--) {
			    	if (Globals.smoothBrush) amount = (1f - ((j*j) + (i*i))/(float)radiusSquared) * amtSign;
			        addHeight(vertexX+i, vertexZ+j, amount);
			    }
			    for (int j = 1; (j*j) + (i*i) <= radiusSquared; j++) {
			    	if (Globals.smoothBrush) amount = (1f - ((j*j) + (i*i))/(float)radiusSquared) * amtSign;
			    	addHeight(vertexX+i, vertexZ+j, amount);
			    }
			}
		}
	}
	
	public void setHeight(float x, float z, int radius, float amount, ToolShape shape) {
		int vertexX = ((int)x) / POLYGON_WIDTH;
		int vertexZ = ((int)z) / POLYGON_WIDTH;
		int radiusSquared = radius * radius;
		baseInd = 0;
		
		switch(shape) {
		case SQUARE:
			for (int i = -radius; i <= radius; i++) {
			    for (int j = -radius; j <= radius; j++) {
			    	float scale = 1f;
			    	if (Globals.smoothBrush) scale = 1f - Math.min(((float)Math.abs(i)/radius + (float)Math.abs(j)/radius), 1f);
			    	setHeight(vertexX+i, vertexZ+j, scale*amount);
			    }
			}
			break;
		default: // Circle
			float scale = 1f;
			for (int i = -radius; i <= radius; i++) {
			    for (int j = 0; (j*j) + (i*i) <= radiusSquared; j--) {
			    	if (Globals.smoothBrush) scale = 1f - ((j*j) + (i*i))/(float)radiusSquared;
			        setHeight(vertexX+i, vertexZ+j, scale*amount);
			    }
			    for (int j = 1; (j*j) + (i*i) <= radiusSquared; j++) {
			    	if (Globals.smoothBrush) scale = 1f - ((j*j) + (i*i))/(float)radiusSquared;
			    	setHeight(vertexX+i, vertexZ+j, scale*amount);
			    }
			}
		}
	}
	
	public void maxHeight(float x, float z, int radius, float amount, ToolShape shape) {
		int vertexX = ((int)x) / POLYGON_WIDTH;
		int vertexZ = ((int)z) / POLYGON_WIDTH;
		int radiusSquared = radius * radius;
		base.clear();
		baseInd = 0;
		
		switch(shape) {
		case SQUARE:
			for (int i = -radius; i <= radius; i++) {
			    for (int j = -radius; j <= radius; j++) {
			    	float scale = 1f;
			    	if (Globals.smoothBrush) scale = 1f - Math.min(((float)Math.abs(i)/radius + (float)Math.abs(j)/radius), 1f);
			    	setHeight(vertexX+i, vertexZ+j, Math.max(heightData.get(vertexX+i, vertexZ+j), scale*amount));
			    }
			}
			break;
		default: // Circle
			float scale = 1f;
			for (int i = -radius; i <= radius; i++) {
			    for (int j = 0; (j*j) + (i*i) <= radiusSquared; j--) {
			    	if (Globals.smoothBrush) scale = 1f - ((j*j) + (i*i))/(float)radiusSquared;
			        setHeight(vertexX+i, vertexZ+j, Math.max(heightData.get(vertexX+i, vertexZ+j), scale*amount));
			    }
			    for (int j = 1; (j*j) + (i*i) <= radiusSquared; j++) {
			    	if (Globals.smoothBrush) scale = 1f - ((j*j) + (i*i))/(float)radiusSquared;
			    	setHeight(vertexX+i, vertexZ+j, Math.max(heightData.get(vertexX+i, vertexZ+j), scale*amount));
			    }
			}
		}
	}
	
	private static int buildYPlaneList() {
		int list = GL11.glGenLists(1);
		GL11.glNewList(list, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glNormal3f(0, 1, 0);
		
		final int s = 320/8;
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(-320, .25f, -320);
		GL11.glTexCoord2f(0, s);
		GL11.glVertex3f(-320, .25f, 320);
		GL11.glTexCoord2f(s, s);
		GL11.glVertex3f(320, .25f, 320);
		GL11.glTexCoord2f(s, 0);
		GL11.glVertex3f(320, .25f, -320);
		
		GL11.glEnd();
		GL11.glEndList();
		
		return list;
	}

	public void smooth(float x, float z, int radius, ToolShape shape) {
		int vertexX = ((int)x) / POLYGON_WIDTH;
		int vertexZ = ((int)z) / POLYGON_WIDTH;
		int radiusSquared = radius * radius;
		
		switch(shape) {
		case SQUARE:
			for (int i = -radius; i <= radius; i++) {
			    for (int j = -radius; j <= radius; j++) {
			    	smooth(vertexX+i, vertexZ+j);
			    }
			}
			break;
		default: // Circle
			for (int i = -radius; i <= radius; i++) {
			    for (int j = 0; (j*j) + (i*i) <= radiusSquared; j--) {
			        smooth(vertexX+i, vertexZ+j);
			    }
			    for (int j = 1; (j*j) + (i*i) <= radiusSquared; j++) {
			    	smooth(vertexX+i, vertexZ+j);
			    }
			}
		}
	}

	public void addHeight(int x, int z, float amount) {
		heightData.add(x, z, amount);
		updateDisplayList = true;
	}
	
	public void setHeight(int x, int z, float amount) {
		if (base.size() > 0) {
			heightData.set(x, z, base.get(baseInd++) + amount);
			updateDisplayList = true;
		} else {
			heightData.set(x, z, amount);
			updateDisplayList = true;
		}
	}
	
	public void smooth(int x, int z) {
		heightData.smooth(x, z);
		updateDisplayList = true;
	}

	public void export() {
		// TODO
	}

	public void step(Camera camera) {
		int dx = (Math.floorDiv((int) camera.getPosition().x, (POLYGON_WIDTH*2)) - (CHUNK_RENDER_VERTEX_SIZE/4));
		int dz = (Math.floorDiv((int) camera.getPosition().z, (POLYGON_WIDTH*2)) - (CHUNK_RENDER_VERTEX_SIZE/4));
		if (loadX != dx || loadZ != dz) {
			
			loadX = dx;
			loadZ = dz;
			
			populateList();
		}
		
		terrainModificationUpdate();
	}

	private void terrainModificationUpdate() {
		if (updateDisplayList) {
			populateList();
			updateDisplayList = false;
		}
	}

	private void populateList() {
		destroyList();
		createList(loadX*POLYGON_WIDTH*2, loadZ*POLYGON_WIDTH*2);
	}

	private void createList(int x, int z) {
		heightmapDisplayList = GL11.glGenLists(1);
		
		int dx = loadX*2;
		int dz = loadZ*2;
		
		GL11.glNewList(heightmapDisplayList, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUADS);
		
		for(int i = 0; i < CHUNK_RENDER_VERTEX_SIZE-1; i++) {
			for(int j = 0; j < CHUNK_RENDER_VERTEX_SIZE-1; j++) {
				Vector3f p1 = new Vector3f(x + ((i + 1) * POLYGON_WIDTH), heightData.get(dx+i+1, dz+j), z + (j * POLYGON_WIDTH));
				Vector3f p2 = new Vector3f(x + (i * POLYGON_WIDTH), heightData.get(dx+i, dz+j), z + (j * POLYGON_WIDTH));
				Vector3f p3 = new Vector3f(x + (i * POLYGON_WIDTH), heightData.get(dx+i, dz+j+1), z + ((j + 1) * POLYGON_WIDTH));
				
				Vector3f normal = Vector3f.cross(Vector3f.sub(p2, p1), Vector3f.sub(p3, p1)).normalize();
				
				GL11.glTexCoord2f((i+1)%2, 1-j%2);
				GL11.glNormal3f(normal.x, normal.y, normal.z);
				GL11.glVertex3f(p1.x, p1.y, p1.z);
				GL11.glTexCoord2f(i%2, 1-j%2);
				GL11.glVertex3f(p2.x, p2.y, p2.z);
				GL11.glTexCoord2f(i%2, 1-(j+1)%2);
				GL11.glVertex3f(p3.x, p3.y, p3.z);
				GL11.glTexCoord2f((i+1)%2, 1-(j+1)%2);
				GL11.glVertex3f(x+((i+1)*POLYGON_WIDTH), heightData.get(dx+i+1, dz+j+1), z+((j+1)*POLYGON_WIDTH));
				
			}
		}
		
		GL11.glEnd();
		GL11.glEndList();
	}

	private void destroyList() {
		GL11.glDeleteLists(heightmapDisplayList, 1);
	}
	
	public void clean() {
		destroyList();
		GL11.glDeleteLists(yPlaneList, 1);
	}

	public static void draw() {
		Globals.defaultTexture.bind();
		GL11.glPushMatrix();
		GL11.glCallList(heightmapDisplayList);
		
		if (Globals.yPlaneVisible) {
			yPlaneTex.bind();
			GL11.glTranslatef(
					GLWindow.camera.getPosition().x - (GLWindow.camera.getPosition().x%8),
					Globals.yPlane,
					GLWindow.camera.getPosition().z - (GLWindow.camera.getPosition().z%8));
			GL11.glCallList(yPlaneList);
		}
		GL11.glPopMatrix();
	}

	public void setDragBaseHeights(float x, float z, int radius) {
		base.clear();
		int vertexX = ((int)x) / POLYGON_WIDTH;
		int vertexZ = ((int)z) / POLYGON_WIDTH;
		int radiusSquared = radius * radius;
		
		switch(Globals.toolShape) {
		case SQUARE:
			for (int i = -radius; i <= radius; i++) {
			    for (int j = -radius; j <= radius; j++) {
			    	base.add(heightData.get(vertexX+i, vertexZ+j));
			    }
			}
			break;
		default: // Circle
			for (int i = -radius; i <= radius; i++) {
			    for (int j = 0; (j*j) + (i*i) <= radiusSquared; j--) {
			    	base.add(heightData.get(vertexX+i, vertexZ+j));
			    }
			    for (int j = 1; (j*j) + (i*i) <= radiusSquared; j++) {
			    	base.add(heightData.get(vertexX+i, vertexZ+j));
			    }
			}
		}
	}

	public void clearDragBaseHeights() {
		base.clear();
	}

	public float heightAt(float x, float z) {
		int vertexX = ((int)x) / POLYGON_WIDTH;
		int vertexZ = ((int)z) / POLYGON_WIDTH;
		
		if ((x%POLYGON_WIDTH) > (z%POLYGON_WIDTH)) {
			Vector3f p1 = new Vector3f(0, heightData.get(vertexX, vertexZ), 0);
			Vector3f p2 = new Vector3f(POLYGON_WIDTH, heightData.get(vertexX+1, vertexZ), 0);
			Vector3f p3 = new Vector3f(0, heightData.get(vertexX,  vertexZ+1), POLYGON_WIDTH);
			
			return MathUtils.barycentric(x%POLYGON_WIDTH, z%POLYGON_WIDTH, p1, p2, p3);
		} else {
			Vector3f p1 = new Vector3f(POLYGON_WIDTH, heightData.get(vertexX+1, vertexZ), 0);
			Vector3f p2 = new Vector3f(0, heightData.get(vertexX, vertexZ+1), POLYGON_WIDTH);
			Vector3f p3 = new Vector3f(POLYGON_WIDTH, heightData.get(vertexX+1,  vertexZ+1), POLYGON_WIDTH);
			
			return MathUtils.barycentric(x%POLYGON_WIDTH, z%POLYGON_WIDTH, p1, p2, p3);
		}
	}

	public HeightData getData() {
		return heightData;
	}

	public void redraw() {
		updateDisplayList = true;
	}
}