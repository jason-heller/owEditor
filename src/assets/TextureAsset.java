package assets;

import application.Profile;
import opengl.tex.Texture;

public class TextureAsset extends Asset {
	public boolean isTransparent;
	public String material;
	public String filename;
	
	public Texture texture;
	
	public void load() {
		texture = Texture.create(Profile.texturesDir + "/" + filename, material, isTransparent);
	}
}
