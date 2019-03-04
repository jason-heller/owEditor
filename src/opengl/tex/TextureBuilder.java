package opengl.tex;

public class TextureBuilder {

	private boolean clampEdges = false;
	private boolean mipmap = true;
	private boolean anisotropic = true;
	private boolean nearest = false;
	private final String material;
	private final boolean transparent;
	private float bias = 1f;
	private int numRows = 1;

	private final String file;

	protected TextureBuilder(String textureFile, String material, boolean transparent, int numRows) {
		this.file = textureFile;
		this.material = material;
		this.transparent = transparent;
		this.numRows = numRows;
	}

	public TextureBuilder anisotropic() {
		this.mipmap = true;
		this.anisotropic = true;
		return this;
	}

	/*
	 * public Texture create(byte[][] data){ int textureId =
	 * TextureUtils.loadArrayToOpenGL(data); return new Texture(textureId,
	 * data.length, material, transparent, numRows); }
	 */

	public TextureBuilder anisotropic(float bias) {
		this.mipmap = true;
		this.bias = bias;
		this.anisotropic = true;
		return this;
	}

	public TextureBuilder clampEdges() {
		this.clampEdges = true;
		return this;
	}

	public Texture create() {
		final TextureData textureData = TextureUtils.decodeTextureFile(file);
		final int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
		return new Texture(textureId, textureData.getWidth(), material, transparent, numRows);
	}

	protected float getBias() {
		return bias;
	}

	protected boolean isAnisotropic() {
		return anisotropic;
	}

	protected boolean isClampEdges() {
		return clampEdges;
	}

	protected boolean isMipmap() {
		return mipmap;
	}

	protected boolean isNearest() {
		return nearest;
	}

	public TextureBuilder nearestFiltering() {
		this.mipmap = false;
		this.anisotropic = false;
		this.nearest = true;
		return this;
	}

	public TextureBuilder normalMipMap() {
		this.mipmap = true;
		this.anisotropic = false;
		return this;
	}

}
