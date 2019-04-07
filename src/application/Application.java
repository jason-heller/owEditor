package application;

import org.lwjgl.opengl.Display;

import application.swing.SwingControl;
import application.swing.windows.SplashFrame;
import editor.EditorControl;
import opengl.GLControl;
import opengl.tex.Texture;
import utils.AppUtils;
import utils.Input;

public class Application {
	
	private static EditorControl editor;
	private static GLControl glControl;
	
	private static boolean closed = false;
	
	public static void main(String[] args) {
		System.setProperty("sun.java2d.opengl", "true");
		
		SwingControl.init(1280,720);
		
		glControl = new GLControl();
		editor = new EditorControl();
		
		SwingControl.glInit();
		
		Globals.defaultTexture = Texture.create("*/res/default.png", "NOMATERIAL", false);
		
		AppUtils.open(SplashFrame.class);
		
		loop();
		
		editor.clean();
		glControl.clean();
		
		System.out.println("Application exited without errors");
		System.exit(0);
	}
	
	public static void close() {
		closed = true;
	}
	
	public static void loop() {
		while (!Display.isCloseRequested() && !closed) {
			Input.poll();
			SwingControl.updateSwing();
			glControl.step();
			editor.step();
		}
	}
	
	public static void loadProfile() {
		Profile.load(Profile.defaultProfileLocation);
	}

	public static EditorControl getEditorControl() {
		return editor;
	}
}
