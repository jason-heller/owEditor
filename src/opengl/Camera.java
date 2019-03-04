package opengl;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import utils.Input;
import utils.MathUtils;

public class Camera {
	private Vector3f position;
	private Matrix4f projection, view;
	
	private float yaw = 0, pitch = 45;
	private float turnSpeed = 4.5f;
	private boolean freecam = false;
	
	public static final float NEAR_PLANE = 1f;
	public static final float FAR_PLANE = 2000f;
	public static final int FOV = 80;
	
	public Camera() {
		position = new Vector3f(0,10,0);
		view = new Matrix4f();
		createProjection();
	}
	
	public void step() {
		Vector3f foward = MathUtils.getDirection(view);
		Vector3f strafe = Vector3f.cross(foward, Vector3f.UP_VECTOR);
		
		float speed = (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))?6f:2f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			foward.mul(-speed);
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			foward.mul(speed);
		else
			foward.zero();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			strafe.mul(-speed);
		else if (Keyboard.isKeyDown(Keyboard.KEY_A))
			strafe.mul(speed);
		else
			strafe.zero();
		
		if (freecam) {
			freecamLook();
		} else {
			defaultLook();
		}

		if (Input.isPressed(Keyboard.KEY_F)) {
			toggleFreecam();
		}
		
		position.add(foward).add(strafe);
		
		view.identity();
		view.rotateX(pitch);
		view.rotateY(yaw);
		view.translate(-position.x, -position.y, -position.z);
	}
	
	private void freecamLook() {
		float mouseDx = Input.getMouseDX();
		float mouseDy = Input.getMouseDY();
		
		pitch -= mouseDy/2f;
		yaw += mouseDx;
	}

	private void defaultLook() {
		if (Keyboard.isKeyDown(Keyboard.KEY_UP) && Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {}
		else if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			pitch = Math.max(pitch-turnSpeed , -89.9f);
		else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			pitch = Math.min(pitch+turnSpeed, 89.9f);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {}
		else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			yaw-=turnSpeed;
		else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			yaw+=turnSpeed;
	}

	private void createProjection() {
		projection = new Matrix4f();
		float aspectRatio = (float) GLWindow.viewportWidth / (float) GLWindow.viewportHeight;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projection.m00 = x_scale;
		projection.m11 = y_scale;
		projection.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projection.m23 = -1;
		projection.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projection.m33 = 0;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public Matrix4f getViewMatrix() {
		return view;
	}
	
	public Matrix4f getProjectionMatrix() {
		return projection;
	}

	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}

	public void toggleFreecam() {
		freecam = !freecam;
		
		if (freecam) {
			Mouse.setGrabbed(true);
		} else {
			Mouse.setGrabbed(false);
		}
	}
}
