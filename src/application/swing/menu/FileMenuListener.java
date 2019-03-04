package application.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import application.Profile;
import application.swing.SwingControl;
import io.format.MapFile;

public class FileMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		//JMenuItem src = (JMenuItem)e.getSource();
		
		if (cmd.equals("SAVE")) {
			save();
		}
		else if (cmd.equals("OPEN")) {
			if (!handleUnsavedData()) return;
			JFileChooser chooser = new JFileChooser(Profile.prefferedDirectory);
			chooser.setDialogTitle("Open");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int approved = chooser.showOpenDialog(null);
			if (approved == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				//if (file.isDirectory()) {
					MapFile.open(file);
				//}
			}
		}
		else if (cmd.equals("NEW")) {
			if (!handleUnsavedData()) return;
			MapFile.clear();
		}
	}
	
	private boolean save() {
		JFileChooser chooser = new JFileChooser(Profile.prefferedDirectory);
		chooser.setDialogTitle("Save");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int approved = chooser.showSaveDialog(null);
		if (approved == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			//if (file.isDirectory()) {
				MapFile.save(file);
				return true;
			//}
		}
		
		return false;
	}

	private boolean handleUnsavedData() {
		int result = JOptionPane.showConfirmDialog(null, "Do you want to save changes to " + MapFile.name, "Warning",
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (result == JOptionPane.YES_OPTION) {

			return save();
		} else if (result == JOptionPane.NO_OPTION) {
			return true;
		}
		return false;
	}
}
