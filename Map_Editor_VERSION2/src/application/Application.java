package application;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import application.swing.SwingControl;
import editor.EditorControl;
import opengl.GLControl;
import opengl.GLWindow;

public class Application {
	
	private static EditorControl editor;
	private static GLControl glControl;
	
	private static boolean closed = false;
	
	public static void main(String[] args) {
		SwingControl.init(1280,720);
		
		glControl = new GLControl();
		editor = new EditorControl();
		
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
			SwingControl.updateSwing();
			glControl.step();
			editor.step();
		}
	}
}
