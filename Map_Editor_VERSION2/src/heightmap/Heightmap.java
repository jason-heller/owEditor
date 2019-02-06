package heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import console.Console;
import opengl.Camera;

public class Heightmap {
	public static int CHUNK_VERTEX_SIZE = 32;
	public static int POLYGON_WIDTH = 8;
	public static int CHUNK_SIZE = (CHUNK_VERTEX_SIZE-1) * POLYGON_WIDTH;

	public static int CHUNK_RENDER_RADIUS = 5;
	private int loadX = Integer.MAX_VALUE, loadZ = Integer.MAX_VALUE;
	
	private static Map<Vector2f, Integer> displayLists = new HashMap<Vector2f, Integer>();
	private static HashMap<Vector2f, Boolean> markedChunkPosForUpdate = new HashMap<Vector2f, Boolean>();

	private HeightData heightData = new HeightData();

	public void addHeight(float x, float z, int radius, float amount) {
		int radiusSquared = radius * radius;
		
		int vertexX = ((int)x) / POLYGON_WIDTH;
		int vertexZ = ((int)z) / POLYGON_WIDTH;
		
		for (int i = -radius; i <= radius; i++) {
		    for (int j = 0; (j*j) + (i*i) <= radiusSquared; j--) {
		        addHeight(vertexX+i, vertexZ+j, amount);
		    }
		    for (int j = 1; (j*j) + (i*i) <= radiusSquared; j++) {
		    	addHeight(vertexX+i, vertexZ+j, amount);
		    }
		}
	}
	
	public void smooth(float x, float z, int radius, float amount) {
		int radiusSquared = radius * radius;
		
		int vertexX = ((int)x) / POLYGON_WIDTH;
		int vertexZ = ((int)z) / POLYGON_WIDTH;
		
		for (int i = -radius; i <= radius; i++) {
		    for (int j = 0; (j*j) + (i*i) <= radiusSquared; j--) {
		        smooth(vertexX+i, vertexZ+j, amount);
		    }
		    for (int j = 1; (j*j) + (i*i) <= radiusSquared; j++) {
		    	smooth(vertexX+i, vertexZ+j, amount);
		    }
		}
	}

	public void addHeight(int x, int z, float amount) {
		heightData.addHeight(x, z, amount);
		int chunkX = Math.floorDiv(x, CHUNK_VERTEX_SIZE);
		int chunkZ = Math.floorDiv(z, CHUNK_VERTEX_SIZE);
		int chunkRealX = chunkX*CHUNK_SIZE;
		int chunkRealZ = chunkZ*CHUNK_SIZE;
		
		for(Vector2f v : markedChunkPosForUpdate.keySet()) {
			if (v.x == chunkRealX && v.y == chunkRealZ) return;
		}
		
		markedChunkPosForUpdate.put(new Vector2f(chunkRealX, chunkRealZ), true);
		
		if (x-chunkX == 0) {
			markedChunkPosForUpdate.put(new Vector2f(chunkRealX-CHUNK_SIZE, chunkRealZ), true);
		}
		
		if (x-chunkX == CHUNK_VERTEX_SIZE-1) {
			markedChunkPosForUpdate.put(new Vector2f(chunkRealX+CHUNK_SIZE, chunkRealZ), true);
		}
		
		if (z-chunkZ == 0) {
			markedChunkPosForUpdate.put(new Vector2f(chunkRealX, chunkRealZ-CHUNK_SIZE), true);
		}
		
		if (z-chunkZ == CHUNK_VERTEX_SIZE-1) {
			markedChunkPosForUpdate.put(new Vector2f(chunkRealX, chunkRealZ+CHUNK_SIZE), true);
		}
	}
	
	public void smooth(int x, int z, float amount) {
		heightData.smooth(x, z, amount);
		int chunkX = Math.floorDiv(x, CHUNK_VERTEX_SIZE);
		int chunkZ = Math.floorDiv(z, CHUNK_VERTEX_SIZE);
		int chunkRealX = chunkX*CHUNK_SIZE;
		int chunkRealZ = chunkZ*CHUNK_SIZE;
		
		for(Vector2f v : markedChunkPosForUpdate.keySet()) {
			if (v.x == chunkRealX && v.y == chunkRealZ) return;
		}
		
		markedChunkPosForUpdate.put(new Vector2f(chunkRealX, chunkRealZ), true);
		
		if (x-chunkX == 0) {
			markedChunkPosForUpdate.put(new Vector2f(chunkRealX-CHUNK_SIZE, chunkRealZ), true);
		}
		
		if (x-chunkX == CHUNK_VERTEX_SIZE-1) {
			markedChunkPosForUpdate.put(new Vector2f(chunkRealX+CHUNK_SIZE, chunkRealZ), true);
		}
		
		if (z-chunkZ == 0) {
			markedChunkPosForUpdate.put(new Vector2f(chunkRealX, chunkRealZ-CHUNK_SIZE), true);
		}
		
		if (z-chunkZ == CHUNK_VERTEX_SIZE-1) {
			markedChunkPosForUpdate.put(new Vector2f(chunkRealX, chunkRealZ+CHUNK_SIZE), true);
		}
	}

	private void redrawList(int x, int z) {
		Set<Vector2f> origCollection = displayLists.keySet();
		
		for(Vector2f v : origCollection) {
			if (v.x == x && v.y == z) {
				GL11.glDeleteLists(displayLists.get(v), 1);
				displayLists.remove(v);
				createList(x,z);
				return;
			}
		}
	}

	public void export() {
		// TODO
	}

	/*public float heightAt(float x, float z) {
		int chunkX = Math.floorDiv((int)x, CHUNK_VERTEX_SIZE);
		int chunkZ = Math.floorDiv((int)z, CHUNK_VERTEX_SIZE);
		
		float[][] data = heightData.get(chunkX, chunkZ);
		
		int chunkVertexOriginX = chunkX * POLYGON_WIDTH;
		int chunkVertexOriginZ = chunkZ * POLYGON_WIDTH;
		
		return data[((int)x/POLYGON_WIDTH)-chunkVertexOriginX][((int)z/POLYGON_WIDTH)-chunkVertexOriginZ];
	}*/

	public void step(Camera camera) {
		int dx = (Math.floorDiv((int) camera.getPosition().x, CHUNK_SIZE) - (CHUNK_RENDER_RADIUS/2));
		int dz = (Math.floorDiv((int) camera.getPosition().z, CHUNK_SIZE) - (CHUNK_RENDER_RADIUS/2));
		if (loadX != dx || loadZ != dz) {
			
			loadX = dx;
			loadZ = dz;
			
			populateLists();
		}
		
		terrainModificationUpdate();
	}

	private void terrainModificationUpdate() {
		for(Vector2f v : markedChunkPosForUpdate.keySet()) {
			redrawList((int)v.x, (int)v.y);
		}
		
		markedChunkPosForUpdate.clear();
	}

	private void populateLists() {
		destroyLists();
		
		for (int i = 0; i < CHUNK_RENDER_RADIUS; i++) {
			for (int j = 0; j < CHUNK_RENDER_RADIUS; j++) {
				createList(loadX*CHUNK_SIZE + (i*CHUNK_SIZE), loadZ*CHUNK_SIZE + (j*CHUNK_SIZE));
			}
		}
	}

	private void createList(int x, int z) {
		int list = GL11.glGenLists(1);
		
		float[][] heights = heightData.getChunk(x, z);
		
		GL11.glNewList(list, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUADS);
		
		for(int i = 0; i < CHUNK_VERTEX_SIZE-1; i++) {
			for(int j = 0; j < CHUNK_VERTEX_SIZE-1; j++) {
				Vector3f p1 = new Vector3f(x + ((i + 1) * POLYGON_WIDTH), heights[i + 1][j], z + (j * POLYGON_WIDTH));
				Vector3f p2 = new Vector3f(x + (i * POLYGON_WIDTH), heights[i][j], z + (j * POLYGON_WIDTH));
				Vector3f p3 = new Vector3f(x + (i * POLYGON_WIDTH), heights[i][j + 1], z + ((j + 1) * POLYGON_WIDTH));
				
				Vector3f normal = Vector3f.cross(Vector3f.sub(p2, p1), Vector3f.sub(p3, p1)).normalize();
				
				GL11.glTexCoord2f((i+1)%2, 1-j%2);
				GL11.glNormal3f(normal.x, normal.y, normal.z);
				GL11.glVertex3f(p1.x, p1.y, p1.z);
				GL11.glTexCoord2f(i%2, 1-j%2);
				GL11.glVertex3f(p2.x, p2.y, p2.z);
				GL11.glTexCoord2f(i%2, 1-(j+1)%2);
				GL11.glVertex3f(p3.x, p3.y, p3.z);
				GL11.glTexCoord2f((i+1)%2, 1-(j+1)%2);
				GL11.glVertex3f(x+((i+1)*POLYGON_WIDTH), heights[i+1][j+1], z+((j+1)*POLYGON_WIDTH));
				
			}
		}
		
		GL11.glEnd();
		GL11.glEndList();
		
		displayLists.put(new Vector2f(x, z), list);
	}

	private void destroyLists() {
		for(int list : displayLists.values()) {
			GL11.glDeleteLists(list, 1);
		}
		
		displayLists.clear();
	}
	
	public void clean() {
		destroyLists();
	}

	public static void draw() {
		GL11.glPushMatrix();
		for(int list : displayLists.values()) {
			GL11.glCallList(list);
		}
		GL11.glPopMatrix();
	}
}