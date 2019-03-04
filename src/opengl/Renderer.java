package opengl;

import application.Application;
import editor.EditorControl;
import entity.EntityControl;
import heightmap.Heightmap;

public class Renderer {
	public static void step() {
		Application.getEditorControl().getEntityControl().drawEntities();
		EditorControl.draw();
		Heightmap.draw();
	}
}
