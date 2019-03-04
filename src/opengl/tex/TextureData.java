package opengl.tex;

import java.nio.ByteBuffer;

public class TextureData {

	private final int width;
	private final int height;
	private final ByteBuffer buffer;

	public TextureData(ByteBuffer buffer, int width, int height) {
		this.buffer = buffer;
		this.width = width;
		this.height = height;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

}
