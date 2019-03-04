package opengl.tex;

import org.lwjgl.opengl.GL11;

public class Texture {
	public int id;
	private String material;
	private int size, atlasRows;
	private boolean transparent;
	
	public static Texture create(String textureFile, String material, boolean transparent) {
		TextureBuilder tb = new TextureBuilder(textureFile, material, transparent, 1);
		return tb.create();
	}

	public static Texture create(String textureFile, String material, boolean transparent, int rows) {
		TextureBuilder tb = new TextureBuilder(textureFile, material, transparent, rows);
		return tb.create();
	}
	
	protected Texture(int id, int size, String material, boolean transparent, int atlasRows) {
		this.id = id;
		this.size = size;
		
		this.material = material;
		this.transparent = transparent;
		this.atlasRows = atlasRows;
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}
	
	public void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	public void clean() {
		GL11.glDeleteTextures(id);
	}
	
	public int getWidth() {
		return size;
	}
	
	public boolean isTransparent() {
		return transparent;
	}
	
	public int getNumAtlasRows() {
		return atlasRows;
	}
	
	public String getMaterial() {
		return material;
	}
}
