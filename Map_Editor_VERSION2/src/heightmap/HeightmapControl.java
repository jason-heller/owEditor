package heightmap;

import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import application.Globals;
import application.Tool;
import console.Console;
import opengl.GLWindow;
import utils.MousePicker;

public class HeightmapControl {
	private Heightmap heightmap;
	private MousePicker picker;
	private int radius = 5;
	
	public HeightmapControl() {
		heightmap = new Heightmap();
		picker = new MousePicker(GLWindow.camera, GLWindow.camera.getProjectionMatrix(), heightmap);
	}
	
	public void step() {
		heightmap.step(GLWindow.camera);
		
		picker.update();
		Vector3f mousePoint = picker.getCurrentTerrainPoint();
		if (mousePoint == null) return;
		
		switch(Globals.tool) {
		case HEIGHTTOOL:
			if (Mouse.isButtonDown(0)) {
				heightmap.addHeight(mousePoint.x, mousePoint.z, radius , 1f);
			}
			else if (Mouse.isButtonDown(1)) {
				heightmap.addHeight(mousePoint.x, mousePoint.z, radius , -1f);
			}
			break;
		case SMOOTHTOOL:
			if (Mouse.isButtonDown(0)) {
				heightmap.smooth(mousePoint.x, mousePoint.z, radius, .2f);
			}
			break;
		}
	}
	
	public void clean() {
		
	}
}
