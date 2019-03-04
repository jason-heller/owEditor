package utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import application.Globals;
import application.swing.SwingControl;
import heightmap.Heightmap;
import opengl.Camera;
import opengl.GLWindow;

public class MousePicker {

	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600;

	private Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	private Vector3f currentTerrainPoint;
	private Heightmap heightmap;

	public MousePicker(Camera cam, Matrix4f projection, Heightmap heightmap) {
		camera = cam;
		projectionMatrix = projection;
		viewMatrix = camera.getViewMatrix();
		this.heightmap = heightmap;
	}

	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update() {
		viewMatrix = camera.getViewMatrix();
		currentRay = calculateMouseRay();
		if (Globals.yPlaneVisible && currentRay.y < 0) {
			currentTerrainPoint = MathUtils.rayPlaneIntersectPt(camera.getPosition(), currentRay, Globals.yPlane, 0, 1, 0); 
		} else {
			if (intersectionInRange(0, RAY_RANGE, currentRay)) {
				currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
			} else {
				currentTerrainPoint = null;
			}
		}
	}

	private Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();// / GLWindow.mouseScale;
		
		if (Mouse.isGrabbed()) {
			mouseX = GLWindow.viewportWidth/2f;
			mouseY = GLWindow.viewportHeight/2f;
		}
		
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = new Matrix4f(viewMatrix).invert();
		Vector4f rayWorld = new Vector4f(eyeCoords).mul(invertedView);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = new Matrix4f(projectionMatrix).invert();
		Vector4f eyeCoords = new Vector4f(clipCoords).mul(invertedProjection);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = ((2.0f * mouseX) / Display.getWidth()) - 1f;
		float y = ((2.0f * mouseY) / Display.getHeight()) - 1f;
		x /= GLWindow.xRatio;
		y /= GLWindow.yRatio;

		return new Vector2f(x, y);
	}

	// **********************************************************

	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = new Vector3f(camera.getPosition());
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay);
	}

	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			Heightmap terrain = getHeightmap(endPoint.x, endPoint.z);
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint) {
		float height = 0;
		Heightmap terrain = getHeightmap(testPoint.x, testPoint.z);
		
		if (terrain != null) {
			height = terrain.heightAt(testPoint.x, testPoint.z);
		}
		
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}

	private Heightmap getHeightmap(float worldX, float worldZ) {
		return heightmap;
	}

}
