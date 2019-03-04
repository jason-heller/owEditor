package application.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import application.swing.SwingControl;
import application.swing.frames.FlagFrame;

public class EditMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		JMenuItem src = (JMenuItem)e.getSource();
		
		if (cmd.equals("LOAD GAME PROFILE"))
		{
			
		}
	}
}
