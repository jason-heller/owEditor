package heightmap;

import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import application.Globals;
import console.Console;
import entity.EntityControl;
import entity.PlacedEntity;
import opengl.GLWindow;
import utils.Input;
import utils.MousePicker;

public class HeightmapControl {
	private Heightmap heightmap;
	public static MousePicker picker;
	
	public HeightmapControl() {
		heightmap = new Heightmap();
		picker = new MousePicker(GLWindow.camera, GLWindow.camera.getProjectionMatrix(), heightmap);
	}
	
	public void step() {
		if (Input.isMouseReleased(0) || Input.isMouseReleased(1)) {
			heightmap.clearDragBaseHeights();
		}
		
		heightmap.step(GLWindow.camera);
		
		picker.update();
		Vector3f mousePoint = picker.getCurrentTerrainPoint();

		if (Globals.dragTerrain && mousePoint != null && (Input.isMousePressed(0) || Input.isMousePressed(1))) {
			Globals.dragInitHeight.set(mousePoint.x, Mouse.getY(), mousePoint.z);
			heightmap.setDragBaseHeights(Globals.dragInitHeight.x, Globals.dragInitHeight.z, Globals.brushWidth);
		}
		
		if (Input.isMouseDown(0) && Input.isDown(Keyboard.KEY_LSHIFT)) {
			heightmap.setDragBaseHeights(Globals.dragInitHeight.x, Globals.dragInitHeight.z, Globals.brushWidth);
		}
		
		switch(Globals.tool) {
		case HEIGHTTOOL:
			if (Input.isMouseDown(0)) {
				if (Globals.dragTerrain) {
					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
						heightmap.clearDragBaseHeights();
						heightmap.setHeight(Globals.dragInitHeight.x, Globals.dragInitHeight.z, Globals.brushWidth,Mouse.getY() - Globals.dragInitHeight.y, Globals.toolShape);
					} else {
						heightmap.setHeight(Globals.dragInitHeight.x, Globals.dragInitHeight.z, Globals.brushWidth,Mouse.getY() - Globals.dragInitHeight.y, Globals.toolShape);
					}

				} else if (mousePoint != null) {
					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
						heightmap.clearDragBaseHeights();
						heightmap.setHeight(mousePoint.x, mousePoint.z, Globals.brushWidth, Globals.yPlane, Globals.toolShape);
					} else {
						heightmap.addHeight(mousePoint.x, mousePoint.z, Globals.brushWidth, 1f, Globals.toolShape);
					}
				}
			}
			else if (Input.isMouseDown(1)) {
				if (Globals.dragTerrain) {
					heightmap.setHeight(Globals.dragInitHeight.x, Globals.dragInitHeight.z, Globals.brushWidth,Mouse.getY() - Globals.dragInitHeight.y, Globals.toolShape);
				} else if (mousePoint != null) {
					if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
						heightmap.maxHeight(mousePoint.x, mousePoint.z, Globals.brushWidth, Globals.yPlane, Globals.toolShape);
					} else {
						heightmap.addHeight(mousePoint.x, mousePoint.z, Globals.brushWidth , -1f, Globals.toolShape);
					}
				}
			}
			break;
		case SMOOTHTOOL:
			if (Mouse.isButtonDown(0) && mousePoint != null) {
				heightmap.smooth(mousePoint.x, mousePoint.z, Globals.brushWidth, Globals.toolShape);
			}
			break;
			default:
		}
		
		if (Globals.yPlaneVisible) {
			if (Input.isPressed(Keyboard.KEY_EQUALS)) {
				raiseYPlane(Globals.gridSize);
			}
			if (Input.isPressed(Keyboard.KEY_MINUS)) {
				raiseYPlane(-Globals.gridSize);
			}
		}
	}
	
	public static void raiseYPlane(int amt) {
		Globals.yPlane += amt;
		for(PlacedEntity e : EntityControl.selected) {
			e.position.y += amt;
		}
	}
	
	public static void resetYPlane() {
		for(PlacedEntity e : EntityControl.selected) {
			e.position.y -= Globals.yPlane;
		}
		Globals.yPlane = 0;
	}

	public void clean() {
		heightmap.clean();
	}

	public HeightData getHeightData() {
		return heightmap.getData();
	}

	public Heightmap getHeightmap() {
		return heightmap;
	}

}
