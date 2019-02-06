package application.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import application.swing.SwingControl;
import opengl.GLWindow;

public class ViewMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		JMenuItem src = (JMenuItem)e.getSource();
		
		if (cmd.equals("WORLD VIEW"))
		{
			if (!SwingControl.glCanvas.isVisible() && GLWindow.isPoppedOut()) {
				GLWindow.popIn();
				GLWindow.togglePoppedInOrOut();
			}
			
			SwingControl.glCanvas.setVisible(!SwingControl.glCanvas.isVisible());
			
			if (GLWindow.isPoppedOut()) {
				handleWindowPop(src);
			}
			
			//SwingControl.updateViews();
			JCheckBoxMenuItem cbSrc = (JCheckBoxMenuItem)src;
			cbSrc.setSelected(SwingControl.glCanvas.isVisible());
		}
		else if (cmd.equals("BIOME VIEW"))
		{
			SwingControl.overheadCanvas.setVisible(!SwingControl.overheadCanvas.isVisible());
			JCheckBoxMenuItem cbSrc = (JCheckBoxMenuItem)src;
			cbSrc.setSelected(SwingControl.overheadCanvas.isVisible());
		}
		else if (cmd.equals("WORLD VIEW IN WINDOW")) {
			handleWindowPop(src);
		}
	}

	private void handleWindowPop(JMenuItem src) {
		GLWindow.togglePoppedInOrOut();
		if (GLWindow.isPoppedOut()) {
			SwingControl.glCanvas.setVisible(true);
			JCheckBoxMenuItem cbSrc = (JCheckBoxMenuItem) src.getComponent().getParent().getComponent(0);
			cbSrc.setSelected(SwingControl.glCanvas.isVisible());
		} else if (!SwingControl.glCanvas.isVisible()) {
			JCheckBoxMenuItem cbSrc = (JCheckBoxMenuItem) src.getComponent().getParent().getComponent(0);
			cbSrc.setSelected(SwingControl.glCanvas.isVisible());
		}
	}

}
