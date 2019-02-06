package opengl;

public class GLControl {
	private GLWindow window;
	
	public GLControl() {
		window = new GLWindow();
		window.init();
	}
	
	public void step() {
		window.step();
	}
	
	public void clean() {
		window.clean();
	}
}
