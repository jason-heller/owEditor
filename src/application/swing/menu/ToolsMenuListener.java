package application.swing.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import application.Application;
import application.swing.windows.DaisyChainDialog;
import application.swing.windows.FlagFrame;
import application.swing.windows.SplashFrame;
import entity.EntityControl;
import entity.PlacedEntity;
import utils.AppUtils;

public class ToolsMenuListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		JMenuItem src = (JMenuItem) e.getSource();

		if (cmd.equals("BUILD DAISY CHAIN")) {
			if (EntityControl.selected.size() > 1) {
				AppUtils.warningMessage("Please select only one object");
			} else if (EntityControl.selected.size() == 0) {
				AppUtils.warningMessage("Please select an object");
			} else {
				AppUtils.open(DaisyChainDialog.class, false, ((PlacedEntity) EntityControl.selected.get(0)),
						Application.getEditorControl().getEntityControl());
			}
		}
	}
}
