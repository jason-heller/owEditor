package assets;

import application.Profile;
import opengl.tex.Texture;

public class TextureAsset {
	public String name;
	public boolean isTransparent;
	public String material;
	public String filename;
	
	public Texture texture;
	
	public void load() {
		texture = Texture.create(Profile.texturesDir + "/" + filename, material, isTransparent);
	}
	
	public String toString() {
		return name;
	}
}
