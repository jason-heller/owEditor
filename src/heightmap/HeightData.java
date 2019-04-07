package heightmap;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import application.Application;
import console.Memento;
import io.SaveData;
import io.data.DataChunk;
import io.data.DataFormat;
import utils.MathUtils;

public class HeightData extends SaveData {
	private Map<Integer, Map<Integer, Float>> array;
	
	public HeightData() {
		super();
		array = new TreeMap<Integer, Map<Integer, Float>>();
	}
	
	public void smooth(int x, int z) {
		float total = get(x,z);
		total += get(x-1, z  );
		total += get(x-1, z-1);
		total += get(x  , z-1);
		total += get(x+1, z-1);
		total += get(x+1, z  );
		total += get(x+1, z+1);
		total += get(x  , z+1);
		total += get(x-1, z+1);
		total /= 9f;
		
		set(x,z, total);
	}
	
	public float get(int x, int z) {
		
		Map<Integer, Float> inner = array.get(x);
		if (inner == null) {
			return getDefaultHeight(x,z);
		}
		
		Float data = inner.get(z);
		
		if (data == null) {
			return getDefaultHeight(x,z);
		}
		
		return data;
	}

	public void set(int x, int z, float amt) {

		Map<Integer, Float> inner = array.get(x);
		if (inner == null) {
			inner = new HashMap<Integer, Float>();
			array.put(x, inner);
			amt += getDefaultHeight(x,z);
		}

		inner.put(z, amt);
	}

	private float getDefaultHeight(int x, int z) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void add(int x, int z, float amt) {

		Map<Integer, Float> inner = array.get(x);
		if (inner == null) {
			inner = new HashMap<Integer, Float>();
			array.put(x, inner);
		}
		else if (inner.get(z) != null) {
			amt += inner.get(z);
		}

		inner.put(z, amt);
	}
	
	public List<Float> getAll() {
		List<Float> data = new ArrayList<Float>();
		
		for(Map<Integer, Float> inner : array.values()) {
			data.addAll(inner.values());
		}
		
		return data;
	}
	
	public boolean createDataChunks() {
		//int leftmost = array.keySet().iterator().next();
		//int topmost = array.get(leftmost).keySet().iterator().next();
		
		Map<Long, float[][]> chunks = new HashMap<Long, float[][]>();
		
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		
		int lastX = Integer.MAX_VALUE, lastZ = Integer.MAX_VALUE;
		float[][] currentHeights = null;
		DataChunk currentChunk = null;
		
		for(Integer x : array.keySet()) {
			int xPartition = Math.floorDiv(x, Heightmap.CHUNK_VERTEX_SIZE);
			
			for(Integer z : array.get(x).keySet()) {
				float height = 0;
				
				if (array.get(x) != null) {
					Float d = array.get(x).get(z);
					if (d != null) height = d;
				}
				
				int zPartition = Math.floorDiv(z, Heightmap.CHUNK_VERTEX_SIZE);
				
				if (xPartition != lastX || zPartition != lastZ) {
					
					if (currentChunk != null) {
						String heightStr = "";
						String last = "";
						int repeats = 1;
						
						for(float[] stripe : currentHeights) {
							for(float heightInStripe : stripe) {
								String value = df.format(heightInStripe);
								if (value.equals(last)) {
									repeats++;
								} else {
									if (repeats > 1) {
										heightStr += "*" + repeats;
										repeats = 1;
									}
									heightStr += ',' + value;
									
								}
								
								last = value;
							}
						}
						
						if (repeats > 1) {
							heightStr += "*" + repeats;
							repeats = 1;
						}
						currentChunk.put("heights", heightStr);
					}
					
					lastX = xPartition;
					lastZ = zPartition;
					long index = MathUtils.cantorReal(xPartition, zPartition);
					
					float[][] chunk = chunks.get(index);
					if (chunk != null) {
						currentHeights = chunk;
					} else {
						currentHeights = new float[Heightmap.CHUNK_VERTEX_SIZE][Heightmap.CHUNK_VERTEX_SIZE];
						currentChunk = new DataChunk(DataFormat.FORMAT_HEIGHTMAP);
						currentChunk.put("x", Integer.toString(xPartition));
						currentChunk.put("z", Integer.toString(zPartition));
						this.addDataChunk(currentChunk);
						chunks.put(index, currentHeights);
					}
				}
				
				int dx = xPartition*Heightmap.CHUNK_VERTEX_SIZE;
				int dz = zPartition*Heightmap.CHUNK_VERTEX_SIZE;
				currentHeights[x-dx][z-dz] = height;
			}
		}
		
		return true;
	}

	@Override
	public void clear() {
		array.clear();
		Application.getEditorControl().getHeightmapControl().getHeightmap().redraw();
	}
}
