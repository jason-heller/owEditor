package heightmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import console.Console;

public class HeightData {
	private Map<Integer, Map<Integer, float[][]>> array;
	
	public HeightData() {
		array = new HashMap<Integer, Map<Integer, float[][]>>();
	}
	
	public void addHeight(int x, int z, float amount) {
		int chunkX = Math.floorDiv(x, Heightmap.CHUNK_VERTEX_SIZE);
		int chunkZ = Math.floorDiv(z, Heightmap.CHUNK_VERTEX_SIZE);
		int chunkOrigVertX = chunkX * Heightmap.CHUNK_VERTEX_SIZE;
		int chunkOrigVertZ = chunkZ * Heightmap.CHUNK_VERTEX_SIZE;
		
		int chunkRealX = chunkX*Heightmap.CHUNK_SIZE;
		int chunkRealZ = chunkZ*Heightmap.CHUNK_SIZE;
		float[][] data = getChunk(chunkRealX, chunkRealZ);
		
		int dx = x - chunkOrigVertX;
		int dz = z - chunkOrigVertZ;
		
		data[dx][dz] += amount;
		
		float resultingHeight = data[dx][dz];
		
		
		if (dx == 0) {
			data = getChunk(chunkRealX-Heightmap.CHUNK_SIZE,chunkRealZ);
			data[(Heightmap.CHUNK_VERTEX_SIZE-1)][dz] = resultingHeight;
		}
		
		if (dx == (Heightmap.CHUNK_VERTEX_SIZE-1)) {
			data = getChunk(chunkRealX+Heightmap.CHUNK_SIZE,chunkRealZ);
			data[0][dz] = resultingHeight - amount;
		}
		
		if (dz == 0) {
			data = getChunk(chunkRealX,chunkRealZ-Heightmap.CHUNK_SIZE);
			data[dx][(Heightmap.CHUNK_VERTEX_SIZE-1)] = resultingHeight;
		}
		
		if (dz == (Heightmap.CHUNK_VERTEX_SIZE-1)) {
			data = getChunk(chunkRealX,chunkRealZ+Heightmap.CHUNK_SIZE);
			data[dx][0] = resultingHeight - amount;
		}
	}
	
	public void smooth(int x, int z, float amount) {
		int chunkX = Math.floorDiv(x, Heightmap.CHUNK_VERTEX_SIZE);
		int chunkZ = Math.floorDiv(z, Heightmap.CHUNK_VERTEX_SIZE);
		int chunkOrigVertX = chunkX * Heightmap.CHUNK_VERTEX_SIZE;
		int chunkOrigVertZ = chunkZ * Heightmap.CHUNK_VERTEX_SIZE;
		
		int chunkRealX = chunkX*Heightmap.CHUNK_SIZE;
		int chunkRealZ = chunkZ*Heightmap.CHUNK_SIZE;
		float[][] data = getChunk(chunkRealX, chunkRealZ);
		
		int dx = x - chunkOrigVertX;
		int dz = z - chunkOrigVertZ;
		
		data[dx][dz] += amount;
		
		float resultingHeight = data[dx][dz];
		
		
		if (dx == 0) {
			data = getChunk(chunkRealX-Heightmap.CHUNK_SIZE,chunkRealZ);
			data[(Heightmap.CHUNK_VERTEX_SIZE-1)][dz] = resultingHeight;
		}
		
		if (dx == (Heightmap.CHUNK_VERTEX_SIZE-1)) {
			data = getChunk(chunkRealX+Heightmap.CHUNK_SIZE,chunkRealZ);
			data[0][dz] = resultingHeight - amount;
		}
		
		if (dz == 0) {
			data = getChunk(chunkRealX,chunkRealZ-Heightmap.CHUNK_SIZE);
			data[dx][(Heightmap.CHUNK_VERTEX_SIZE-1)] = resultingHeight;
		}
		
		if (dz == (Heightmap.CHUNK_VERTEX_SIZE-1)) {
			data = getChunk(chunkRealX,chunkRealZ+Heightmap.CHUNK_SIZE);
			data[dx][0] = resultingHeight - amount;
		}
	}
	
	public float[][] getChunk(int x, int z) {
		
		Map<Integer, float[][]> inner = array.get(x);
		if (inner == null) {
			return addChunk(x,z);
		}
		
		float[][] data = inner.get(z);
		
		if (data == null) {
			return addChunk(x,z);
		}
		
		return data;
	}

	private float[][] addChunk(int x, int z) {
		float[][] data = new float[Heightmap.CHUNK_VERTEX_SIZE][Heightmap.CHUNK_VERTEX_SIZE];
		
		Map<Integer, float[][]> inner = array.get(x);
		if (inner == null) {
			Map<Integer, float[][]> newMap = new HashMap<Integer, float[][]>();
			array.put(x, newMap);
			newMap.put(z, data);
			return data;
		}
		
		inner.put(z, data);
		return data;
	}

	public List<float[][]> getAll() {
		List<float[][]> data = new ArrayList<float[][]>();
		
		for(Map<Integer, float[][]> inner : array.values()) {
			data.addAll(inner.values());
		}
		
		return data;
	}
}
