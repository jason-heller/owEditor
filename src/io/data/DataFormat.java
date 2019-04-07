package io.data;

public enum DataFormat {
	FORMAT_UNKNOWN("Unknown"),
	
	FORMAT_HEIGHTMAP("Heightmap", 
			new DataElement("x",Integer.class),
			new DataElement("z",Integer.class),
			new DataElement("heights",float[][].class)
			),
	
	FORMAT_BIOME("Biome", 
			new DataElement("x",Integer.class),
			new DataElement("z",Integer.class),
			new DataElement("id",String.class)
			),
	
	FORMAT_REGION("Region",
			new DataElement("x",Integer.class),
			new DataElement("z",Integer.class),
			new DataElement("id",String.class),
			new DataElement("radius",Short.class)
			),
	
	FORMAT_PATH("Path",
			new DataElement("x",int[].class),
			new DataElement("z",int[].class),
			new DataElement("id",String.class),
			new DataElement("index", Integer.class),
			new DataElement("next",Integer.class)
			),
	
	FORMAT_ENTITY("Entity",
			new DataElement("name", String.class),
			new DataElement("id", String.class),
			new DataElement("isVisible", Boolean.class),
			new DataElement("pos", String.class),
			new DataElement("rot", String.class),
			new DataElement("scale", Float.class),
			new DataElement("model", String.class),
			new DataElement("texture", String.class),
			new DataElement("properties", String[].class)
			),
	
	FORMAT_MAP_DATA("MapData",
			new DataElement("mapName", String.class),
			new DataElement("mapVersion", String.class),
			new DataElement("profile", String.class),
			new DataElement("flagValues",boolean[].class)
			),
	
	/// PROFILE DATA FORMATS ///
	
	FORMAT_PROFILE_FLAGS("Flags",
			new DataElement("flags",String[].class),
			new DataElement("values",boolean[].class)
			),
	
	FORMAT_PROFILE_MATERIALS("Materials",
			new DataElement("names",String[].class),
			new DataElement("ids",Integer[].class)
			),
	
	FORMAT_PROFILE_MAP_STRUCTURES("Mapstructures",
			new DataElement("biomes",String[].class),
			new DataElement("regions",String[].class),
			new DataElement("paths",String[].class)
			),
	
	FORMAT_PROFILE_GAMEDATA("Gameinfo",
			new DataElement("name",String.class),
			new DataElement("version",String.class),
			new DataElement("modelsDir",String.class),
			new DataElement("texturesDir",String.class),
			new DataElement("prefferedDir",String.class)
			),
	
	FORMAT_PROFILE_MODEL("Model",
			new DataElement("name",String.class),
			new DataElement("solidity",Byte.class),
			new DataElement("isCollisionMesh",Boolean.class),
			new DataElement("collisionMesh",String.class),
			new DataElement("filename", String.class),
			new DataElement("associatedTexture", String.class),
			new DataElement("tags", String[].class),
			new DataElement("type", String.class)
			),
	
	FORMAT_PROFILE_TEXTURE("Texture",
			new DataElement("name", String.class),
			new DataElement("isTransparent", Boolean.class),
			new DataElement("material", String.class),
			new DataElement("filename", String.class),
			new DataElement("tags", String[].class),
			new DataElement("type", String.class)
			),
	
	FORMAT_PROFILE_ENTITY("Mapentity",
			new DataElement("name", String.class),
			new DataElement("isVisible", Boolean.class),
			new DataElement("model", String.class),
			new DataElement("texture", String.class),
			new DataElement("scale", Integer.class),
			new DataElement("material", String.class),
			new DataElement("properties", String[].class),
			new DataElement("tags", String[].class),
			new DataElement("type", String.class)
			);

	private DataElement[] elements;
	private String name;
	
	public DataElement[] getElements() {
		return elements;
	}
	
	public String getName() {
		return name;
	}
	
	DataFormat(String name, DataElement ... elements) {
		this.name = name;
		this.elements = elements;
	}

	public int getElementIndex(String name) {
		for(int i = 0; i < elements.length; i++) {
			if (elements[i].getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}

	public static DataFormat getFormat(String name) {
		String nameLowerCase = name.toLowerCase();
		for(int i = 0; i < DataFormat.values().length; i++) {
			if (DataFormat.values()[i].getName().toLowerCase().equals(nameLowerCase)) {
				return DataFormat.values()[i];
			}
		}
		
		return FORMAT_UNKNOWN;
	}
}
