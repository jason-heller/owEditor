package application.swing;

import java.awt.Canvas;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

public class CanvasFrame extends JInternalFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final boolean RESIZABLE = false;
	private static final boolean CLOSABLE = false;
	private static final boolean MAXIMIZABLE = false;
	private static final boolean ICONIFIABLE = false;
	private Canvas canvas;

	public CanvasFrame(String title) {
		super(title, RESIZABLE, CLOSABLE, MAXIMIZABLE, ICONIFIABLE);
		
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		init();
	}
	
	protected void init() {
		canvas = new Canvas();
		canvas.setPreferredSize(this.getSize());
		canvas.setBackground(Color.black);
		add(canvas);
		pack();
		setVisible(true);
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		if (canvas != null)
			canvas.setBounds(getX(), getY(), width, height);
	}

	public Canvas getCanvas() {
		return canvas;
	}
}
