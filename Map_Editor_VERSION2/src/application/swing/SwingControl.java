package application.swing;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import application.swing.menu.EditorMenuBar;
import console.Console;
import opengl.GLWindow;

public class SwingControl {
	public static JFrame jFrame;
	
	private static JLayeredPane canvasPanel;
	private static JPanel multiCanvasPanel;
	public static Canvas glCanvas;
	public static Canvas overheadCanvas;
	public static Canvas fullscreenCanvas;
	
	private static EditorToolbar toolbar;
	private static EditorList lists;
	
	private static int viewWidth, viewHeight;
	
	public static void init(int width, int height) {
		jFrame = new JFrame();
		glCanvas = new Canvas();
		overheadCanvas = new Canvas();
		fullscreenCanvas = new Canvas();
		
		viewWidth = width;
		viewHeight = height;

		try {
			initViews();
			
			//jFrame.setSize(width, height + 130);
			jFrame.setTitle("Map Editor V2.0");
			jFrame.setVisible(true);
			
			final EditorCmdBox cmdBox = new EditorCmdBox();
			final EditorMenuBar menuBar = new EditorMenuBar();
			lists = new EditorList();
			toolbar = new EditorToolbar(cmdBox);

			jFrame.add(toolbar, BorderLayout.WEST);
			jFrame.add(cmdBox, BorderLayout.SOUTH);
			jFrame.setJMenuBar(menuBar);
			jFrame.add(lists, BorderLayout.EAST);
			jFrame.getContentPane().setBackground(Color.DARK_GRAY);
			jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			jFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
			
			jFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					Console.send("quit");
				}
			});
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initViews() {
		Dimension dimension = new Dimension(viewWidth, viewHeight);
		canvasPanel = new JLayeredPane();
		canvasPanel.setPreferredSize(dimension);
		//canvasPanel.setLayout(new GridLayout(1,0));
		canvasPanel.setLayout(new OverlayLayout(canvasPanel));
		
		multiCanvasPanel = new JPanel();
		//all.setPreferredSize(dimension);
		multiCanvasPanel.setLayout(new GridLayout(2,2,4,4));
		
		jFrame.add(canvasPanel, BorderLayout.CENTER);
		
		glCanvas.setVisible(true);
		glCanvas.setPreferredSize(dimension);
		glCanvas.setBackground(Color.BLACK);
		
		overheadCanvas.setPreferredSize(dimension);
		overheadCanvas.setVisible(false);
		overheadCanvas.setBackground(Color.DARK_GRAY);
		
		fullscreenCanvas.setVisible(true);
		fullscreenCanvas.setPreferredSize(dimension);
		fullscreenCanvas.setBackground(Color.BLACK);
		
		multiCanvasPanel.add(glCanvas, BorderLayout.CENTER, -1);
		multiCanvasPanel.add(overheadCanvas, BorderLayout.CENTER, -1);

		canvasPanel.add(multiCanvasPanel, BorderLayout.CENTER, 1);
		canvasPanel.add(fullscreenCanvas, BorderLayout.CENTER, 1);

		JLabel placeholder = new JLabel("");
		placeholder.setBackground(Color.BLACK);
		multiCanvasPanel.add(placeholder);
	}
	
	public static void updateSwing() {
		if ((!glCanvas.isVisible()) && overheadCanvas.isVisible()) {
			copyCanvas(overheadCanvas, fullscreenCanvas);
			//fullscreenCanvas.setVisible(true);
			multiCanvasPanel.setVisible(false);

			GLWindow.parent = glCanvas;
		}
		else if (glCanvas.isVisible() && !overheadCanvas.isVisible()) {	
			//BufferedImage img = GLWindow.asImage();
			//if (img != null) {
				//fullscreenCanvas.setVisible(true);
				multiCanvasPanel.setVisible(false);
				GLWindow.parent = fullscreenCanvas;
				//fullscreenCanvas.getGraphics().drawImage(img, 0, 0, fullscreenCanvas.getWidth(), fullscreenCanvas.getHeight(), null);
			//}
		} else {
			//fullscreenCanvas.setVisible(false);
			multiCanvasPanel.setVisible(true);
			GLWindow.parent = glCanvas;
		}
		// NOTE: Here we need a workaround for the HW/LW Mixing feature to work
		// correctly due to yet unfixed bug 6852592.
		// The JTextField is a validate root, so the revalidate() method called
		// during the setText() operation does not validate the parent of the
		// component. Therefore, we need to force validating its parent here.
		final Container parent = toolbar.getParent();
		if (parent instanceof JComponent) {
			((JComponent) parent).revalidate();
		}
		/*
		 * // ... and just in case, call validate() on the top-level window as well
		 * Window window = SwingUtilities.getWindowAncestor(toolbar); if (window !=
		 * null) { window.validate(); }
		 */
	}

	private static void copyCanvas(Canvas src, Canvas dest) {
		//dest.getGraphics().setColor(Color.WHITE);
		Image buffer = src.createImage(src.getWidth(), src.getHeight());
		dest.getGraphics().drawImage(buffer, 0, 0, dest.getWidth(), dest.getHeight(), jFrame);
		//dest.getGraphics().setColor(Color.RED);
		//dest.getGraphics().fillRect(0, 0, 500, 500);
	}
}
