package editor;

import heightmap.HeightmapControl;

public class EditorControl {
	private HeightmapControl heightmapControl;

	public EditorControl() {
		heightmapControl = new HeightmapControl();
	}
	
	public void step() {
		heightmapControl.step();
	}
	
	public void clean() {
		heightmapControl.clean();
	}
}
