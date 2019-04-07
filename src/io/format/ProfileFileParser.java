package io.format;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.Flag;
import application.Profile;
import application.swing.SwingControl;
import assets.Entity;
import assets.Material;
import assets.Model;
import assets.TextureAsset;
import io.data.DataChunk;
import io.data.DataFile;
import overhead.OverheadItem;
import utils.EntryType;

public class ProfileFileParser {
	public static void parse(DataFile file) {
		
		List<Model> models = new ArrayList<Model>();
		List<TextureAsset> textures = new ArrayList<TextureAsset>();
		List<Entity> entities = new ArrayList<Entity>();
		
		for(DataChunk chunk : file.getChunks()) {
			Map<String, String> data = chunk.data;
			
			switch(chunk.format) {
			case FORMAT_PROFILE_GAMEDATA:
				Profile.name = data.get("name");
				Profile.version = data.get("version");
				
				Profile.modelsDir = data.get("modelsDir").replace("*", file.getLocation());
				Profile.texturesDir = data.get("texturesDir").replace("*", file.getLocation());
				Profile.prefferedDirectory = new File(data.get("prefferedDir").replace("*", file.getLocation()));
				break;
				
			case FORMAT_PROFILE_FLAGS:
				String[] flags = data.get("flags").replaceAll(" ",  "").split(",");
				String[] values = data.get("values").replaceAll(" ",  "").split(",");
				
				Profile.flags = new Flag[flags.length];
				Profile.flagDefaults = new boolean[flags.length];
				for(int i = 0; i < flags.length; i++) {
					Profile.flags[i] = new Flag(flags[i], values[i].equals("TRUE")?true:false);
					Profile.flagDefaults[i] = Profile.flags[i].value;
				}
				break;
				
			case FORMAT_PROFILE_MATERIALS:
				String[] names = data.get("names").replaceAll(" ",  "").split(",");
				String[] ids = data.get("ids").replaceAll(" ",  "").split(",");
				
				Profile.materials = new Material[names.length];
				for(int i = 0; i < names.length; i++) {
					Profile.materials[i] = new Material(names[i], Integer.parseInt(ids[i]));
				}
				break;
				
			case FORMAT_PROFILE_MAP_STRUCTURES:
				String[] biomes = data.get("biomes").split(",");
				String[] regions = data.get("regions").split(",");
				String[] paths = data.get("paths").split(",");
				
				Profile.biomes = new OverheadItem[biomes.length];
				for(int i = 0; i < biomes.length; i++) {
					String[] split = biomes[i].split("\\(");
					String name = split[0];
					
					String[] colorData = split[1].substring(0, split[1].indexOf(")")).split(" ");
					Color rgb = new Color(Float.parseFloat(colorData[0]), Float.parseFloat(colorData[1]), Float.parseFloat(colorData[2]));
					Profile.biomes[i] = new OverheadItem(name, rgb);
				}
				
				Profile.regions = new OverheadItem[regions.length];
				for(int i = 0; i < regions.length; i++) {
					String[] split = regions[i].split("\\(");
					String name = split[0];
					
					String[] colorData = split[1].substring(0, split[1].indexOf(")")).split(" ");
					Color rgb = new Color(Float.parseFloat(colorData[0]), Float.parseFloat(colorData[1]), Float.parseFloat(colorData[2]));
					Profile.regions[i] = new OverheadItem(name, rgb);
				}
				
				Profile.paths = new OverheadItem[paths.length];
				for(int i = 0; i < paths.length; i++) {
					String[] split = paths[i].split("\\(");
					String name = split[0];
					
					String[] colorData = split[1].substring(0, split[1].indexOf(")")).split(" ");
					Color rgb = new Color(Float.parseFloat(colorData[0]), Float.parseFloat(colorData[1]), Float.parseFloat(colorData[2]));
					Profile.paths[i] = new OverheadItem(name, rgb);
				}
				break;
				
			case FORMAT_PROFILE_MODEL:
				String filename = data.get("filename");
				Model model = ObjFileParser.loadObj(Profile.modelsDir + "/" + filename, false);
				
				model.name = data.get("name");
				model.solidity = Integer.parseInt(data.get("solidity"));
				model.isCollisionMesh = data.get("isCollisionMesh").equals("TRUE")?true:false;
				model.collisionMesh = data.get("collisionMesh");
				model.filename = filename;
				model.associatedTexture = data.get("associatedTexture");
				model.type = EntryType.get(data.get("type"));
				model.tags = getTags(data);
				
				models.add(model);
				break;
				
			case FORMAT_PROFILE_TEXTURE:
				// TODO: This is retrofitted from old code, refactor
				TextureAsset texture = new TextureAsset();
				
				texture.name = data.get("name");
				texture.isTransparent = data.get("isTransparent").equals("TRUE")?true:false;
				texture.material = data.get("material");
				texture.filename = data.get("filename");
				texture.type = EntryType.get(data.get("type"));
				texture.tags = getTags(data);
				
				textures.add(texture);
				
				texture.load();
				break;
				
			case FORMAT_PROFILE_ENTITY:
				Entity entity = new Entity();
				
				entity.name = data.get("name");
				entity.isVisible = data.get("isVisible").equals("TRUE")?true:false;
				entity.model = data.get("model");
				entity.texture = data.get("texture");
				entity.scale = Integer.parseInt(data.get("scale"));
				entity.material = data.get("material");
				entity.properties = data.get("properties");
				entity.type = EntryType.get(data.get("type"));
				entity.tags = getTags(data);
				
				entities.add(entity);
				break;
			
			default:
				break;
			}
		}
		
		Profile.models = models.toArray(new Model[0]);
		Profile.textures = textures.toArray(new TextureAsset[0]);
		Profile.entities = entities.toArray(new Entity[0]);
		
		SwingControl.populateAssetList();
	}

	private static String[] getTags(Map<String, String> data) {
		String str = data.get("tags");
		
		if (str == null) return new String[] {};
		
		return str.replaceAll(", ", ",").split(",");
	}
}
