package application;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import assets.Entity;
import assets.Material;
import assets.Model;
import assets.TextureAsset;
import io.DataFileReader;
import io.data.DataFile;
import io.format.ProfileFileParser;
import overhead.OverheadItem;

public class Profile {
	public static Flag[] flags;
	public static String defaultProfileLocation = new File("").getAbsolutePath() + "/profiles/profile.txt";
	
	public static Model[] models;
	public static TextureAsset[] textures;
	public static Entity[] entities;
	public static Material[] materials;
	
	public static String name;
	public static String version;
	public static String texturesDir;
	public static String modelsDir;
	public static boolean[] flagDefaults;
	public static OverheadItem[] biomes;
	public static OverheadItem[] regions;
	public static OverheadItem[] paths;
	public static File prefferedDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
	
	public static void load(String location) {
		DataFile file = DataFileReader.read(new File(location));
		ProfileFileParser.parse(file);
	}

	public static void resetFlags() {
		for(int i = 0; i < flags.length; i++) {
			flags[i].value = flagDefaults[i];
		}
	}

	public static OverheadItem getBiome(String id) {
		for(OverheadItem item : biomes) {
			if (item.toString().equals(id)) {
				return item;
			}
		}
		
		return null;
	}
	
	public static OverheadItem getRegion(String id) {
		for(OverheadItem item : regions) {
			if (item.toString().equals(id)) {
				return item;
			}
		}
		
		return null;
	}
	
	public static OverheadItem getPath(String id) {
		for(OverheadItem item : paths) {
			if (item.toString().equals(id)) {
				return item;
			}
		}
		
		return null;
	}

	public static Model getModel(String id) {
		for(Model item : models) {
			if (item.toString().equals(id)) {
				return item;
			}
		}
		
		return null;
	}
	
	public static TextureAsset getTexture(String id) {
		for(TextureAsset item : textures) {
			if (item.toString().equals(id)) {
				return item;
			}
		}
		
		return null;
	}
	
	public static Entity getEntity(String id) {
		for(Entity item : entities) {
			if (item.toString().equals(id)) {
				return item;
			}
		}
		
		return null;
	}
}
