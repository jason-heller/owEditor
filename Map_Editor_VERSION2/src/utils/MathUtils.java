package utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MathUtils {
	public static float barycentric(float x, float y, Vector3f p1, Vector3f p2, Vector3f p3) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (x - p3.x) + (p3.x - p2.x) * (y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (x - p3.x) + (p1.x - p3.x) * (y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Vector3f getDirection(Matrix4f matrix) {
		Matrix4f inverse = new Matrix4f();
		matrix.invert(inverse);
		
		return new Vector3f(inverse.m20, inverse.m21, inverse.m22);
	}
}
