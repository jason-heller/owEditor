package assets;

import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class Model extends Asset {

	public int solidity;
	public boolean isCollisionMesh;
	public String collisionMesh;
	public String filename;
	
	public int id;
	private float[] vertices;
	private Vector3f max;
	private Vector3f min;
	public String associatedTexture = "";

	public static Model create() {
		int id = GL11.glGenLists(1);
		return new Model(id);
	}
	
	private Model(int id) {
		this.id = id;
	}

	public void clean() {
		GL11.glDeleteLists(id, 1);
	}

	public void setVertexData(List<Integer> indices, List<float[]> vertices) {
		this.vertices = new float[indices.size()*3];
		int j = 0;

		for(int i = 0; i < indices.size(); i++) {
			this.vertices[j] = vertices.get(indices.get(i))[0];
			this.vertices[j+1] = vertices.get((indices.get(i)))[1];
			this.vertices[j+2] = vertices.get((indices.get(i)))[2];
			j += 3;
		}
	}

	public void draw() {
		GL11.glCallList(id);
	}

	public Vector3f getMax() {
		return max;
	}
	
	public Vector3f getMin() {
		return min;
	}

	public void setMax(Vector3f max) {
		this.max = max;
	}
	
	public void setMin(Vector3f min) {
		this.min = min;
	}

	public float[] getVertices() {
		return vertices;
	}
}
