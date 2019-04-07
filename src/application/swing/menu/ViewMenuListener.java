package application.swing.menu;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import application.Application;
import application.Globals;
import application.swing.CanvasFrame;
import application.swing.SwingControl;
import opengl.GLWindow;
import overhead.OverheadFrame;

public class ViewMenuListener implements ActionListener {

	private static CanvasFrame glCanvas = SwingControl.glCanvas;
	private static OverheadFrame overheadCanvas = SwingControl.overheadCanvas;
	private static JPanel container = SwingControl.canvasPane;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		JMenuItem src = (JMenuItem)e.getSource();
		
		if (cmd.equals("WORLD VIEW"))
		{
			toggleCanvas(glCanvas, (JCheckBoxMenuItem)src);
		}
		else if (cmd.equals("OVERHEAD VIEW"))
		{
			toggleCanvas(overheadCanvas, (JCheckBoxMenuItem)src);
		}
		else if (cmd.equals("SHOW UTILITY ASSETS"))
		{
			Globals.showUtilities = src.isSelected();
			SwingControl.populateAssetList();
		}
		
		container.revalidate();
		overheadCanvas.repaint();
		GLWindow.requestRefresh();
	}

	private void toggleCanvas(CanvasFrame frame, JCheckBoxMenuItem src) {
		handleMultipleWindowsVisible();
		boolean containsFrame = container.isAncestorOf(frame);
		
		if (containsFrame) {
			if (frame != glCanvas) {
				container.remove(frame);
			}
		} else {
			container.add(frame);
		}
		
		src.setSelected(container.isAncestorOf(frame));
	}

	private void handleMultipleWindowsVisible() {
		if (container.isAncestorOf(glCanvas) ^ container.isAncestorOf(overheadCanvas)) {
			int w = container.getWidth()/2;
			int h = container.getHeight()/2;
			
			glCanvas.setBounds(0,0,w,h);
			overheadCanvas.setBounds(w,0,w, h);
			
			container.setLayout(new GridLayout(1,2));
			GLWindow.xRatio = 2f;//1567f/783f;
		} else {
			container.setLayout(new GridLayout(1,0));
			GLWindow.xRatio = 1f;
		}
	}
}
