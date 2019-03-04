package io.format;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import application.Application;
import application.Flag;
import application.Globals;
import application.Profile;
import application.swing.SwingControl;
import assets.Model;
import assets.TextureAsset;
import editor.EditorControl;
import entity.ModelEntity;
import entity.PlacedEntity;
import heightmap.Heightmap;
import io.DataFileReader;
import io.SaveData;
import io.data.DataChunk;
import io.data.DataFile;
import io.data.DataFormat;
import overhead.OverheadControl;
import overhead.OverheadInstance;

public class MapFile {
	public static String name = "Untitled";
	
	public static List<SaveData> data = new ArrayList<SaveData>();
	
	public static void clear() {
		
		SwingControl.getOverheadControl().clear();
		Application.getEditorControl().getEntityControl().clear();
		Application.getEditorControl().getHeightmapControl().getHeightData().clear();
		Profile.resetFlags();
	}

	public static void save(File destination) {
		DataFile dataFile = build(destination);
		
		try {
			dataFile.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static DataFile build(File file) {
		List<DataChunk> chunks = new ArrayList<DataChunk>();
		
		// Map Data
		System.out.println("Writing Map Info");
		DataChunk chunk = new DataChunk(DataFormat.FORMAT_MAP_DATA);
		chunk.put("mapName", "Map");
		chunk.put("mapVersion", "1");
		chunk.put("profile", Profile.name);
		
		String flagData = "";
		for(Flag f : Profile.flags)
			flagData += f.value?"T":"F";
		
		chunk.put("flagValues", flagData);
		chunks.add(chunk);
		
		System.out.println("Writing Heightmap");
		// Heightmap
		EditorControl editor = Application.getEditorControl();
		SaveData saveData = editor.getHeightmapControl().getHeightData();
		add(chunks, saveData);
		
		System.out.println("Entities Heightmap");
		// Entities
		saveData = editor.getEntityControl();
		add(chunks, saveData);
		
		System.out.println("Writing Biomes/Regions/Paths");
		// Biomes/Regions/Paths
		saveData = SwingControl.getOverheadControl();
		add(chunks, saveData);
		
		return new DataFile(file, chunks.toArray(new DataChunk[0]));
	}

	private static void add(List<DataChunk> chunks, SaveData saveData) {
		boolean hasData = saveData.createDataChunks();
		if (hasData)
			chunks.addAll(saveData.getSaveData());
		
		saveData.flush();
	}
	
	public static void open(File file) {
		clear();
		DataFile dataFile = DataFileReader.read(file);
		int x, z;
		String id;
		Point p;
		
		for(DataChunk chunk : dataFile.getChunks()) {
			Map<String, String> data = chunk.data;
			switch(chunk.format) {
			case FORMAT_MAP_DATA:
				Globals.mapName = data.get("mapName");
				Globals.mapVersion = data.get("mapVersion");
				String f = data.get("flagValues");
				for(int i = 0; i < f.length(); i++) {
					Profile.flags[i].value = (f.charAt(i) == 'T');
				}
				
				break;
			
			case FORMAT_HEIGHTMAP:
				float[][] heights = parseHeights(data.get("heights"));
				x = Integer.parseInt(data.get("x")) * Heightmap.CHUNK_VERTEX_SIZE;
				z = Integer.parseInt(data.get("z")) * Heightmap.CHUNK_VERTEX_SIZE;
				
				for(int i = 0; i < Heightmap.CHUNK_VERTEX_SIZE; i++) {
					for(int j = 0; j < Heightmap.CHUNK_VERTEX_SIZE; j++) {
						Application.getEditorControl().getHeightmapControl().getHeightData().set(x+i, z+j, heights[i][j]);
					}
				}
				break;
				
			case FORMAT_BIOME:
				x = Integer.parseInt(data.get("x"));
				z = Integer.parseInt(data.get("z"));
				id = data.get("name");
				p = new Point(x, z);
				
				OverheadControl.biomes.put(p, new OverheadInstance(Profile.getBiome(id), p));
				break;
				
			case FORMAT_REGION:
				x = Integer.parseInt(data.get("x"));
				z = Integer.parseInt(data.get("z"));
				id = data.get("name");
				p = new Point(x, z);
				int radius = Integer.parseInt(data.get("radius"));
				
				OverheadControl.regions.put(p, new OverheadInstance(Profile.getRegion(id), p, radius));
				break;
				
			case FORMAT_PATH:
				x = Integer.parseInt(data.get("x"));
				z = Integer.parseInt(data.get("z"));
				id = data.get("name");
				p = new Point(x, z);
				
				OverheadControl.paths.put(p, new OverheadInstance(Profile.getPath(id), p));
				break;
				
			case FORMAT_ENTITY:
				
				PlacedEntity e;
				String name = data.get("name");
				
				Model model = Profile.getModel(data.get("model"));
				TextureAsset texture = Profile.getTexture(data.get("texture"));
				
				if (name.equals("Static Prop")) {
					e = new ModelEntity(model, texture);
				} else {
					e = new PlacedEntity(Profile.getEntity(name));
					e.setModel(model);
					e.setTexture(texture);
				}
				
				e.id = data.get("id");
				e.isVisible = Boolean.parseBoolean(data.get("isVisible"));
				e.position = parseVec3(data.get("pos"));
				e.rotation = parseVec3(data.get("rot"));
				e.scale = Float.parseFloat(data.get("scale"));
				e.setProperties(data.get("properties"));
				
				Application.getEditorControl().getEntityControl().addEntity(e);
				break;
			default:
				System.out.println("Warning: Unknown data format "+chunk.format.getName());
			}
		}
	}

	private static Vector3f parseVec3(String line) {
		String[] data = line.split(",");
		return new Vector3f(Float.parseFloat(data[0]), Float.parseFloat(data[1]), Float.parseFloat(data[2]));
	}

	private static float[][] parseHeights(String line) {
		String[] heights = line.split(",");
		float[][] data = new float[Heightmap.CHUNK_VERTEX_SIZE][Heightmap.CHUNK_VERTEX_SIZE];
		
		int ind = 1;
		int repeats = 0;
		float repeatingValue = 0;
		
		for(int i = 0; i < Heightmap.CHUNK_VERTEX_SIZE; i++) {
			for(int j = 0; j < Heightmap.CHUNK_VERTEX_SIZE; j++) {
				if (repeats > 0) {
					data[i][j] = repeatingValue;
					repeats--;
				} else {
					String[] subdata = heights[ind].split("*");
					
					if (subdata.length == 1) {
						data[i][j] = Float.parseFloat(subdata[0]);
					} else {
						repeatingValue = Float.parseFloat(subdata[0]);
						data[i][j] = repeatingValue;
						repeats = Integer.parseInt(subdata[1])-1;
					}
				}
			}
		}
		
		return data;
	}
}
