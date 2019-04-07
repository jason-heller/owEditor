package application.swing.menu;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class EditorMenuBar extends JMenuBar {
	
	public EditorMenuBar() {
		addMenu("File", 'f', new FileMenuListener(), "New", "Open", "Save");
		addMenu("Edit", 'e', new EditMenuListener(), "Load Game Profile [todo]");
		addMenu("View", 'v', new ViewMenuListener(), "*NOverhead View", "|", "*NShow Utility Assets");
		addMenu("Map", 'm', new MapMenuListener(), "Edit Map Flags");
		addMenu("Tools", 't', new ToolsMenuListener(), "*SDcBuild Daisy Chain");

	}
	
	private JMenu addMenu(String name, char mnemonic, ActionListener al, String ... options) {
		JMenu menu = new JMenu(name);
		menu.setMnemonic(mnemonic);
		
		for(String option : options) {
			KeyStroke stroke = null;
			
			if (option.equals("|")) {
				menu.addSeparator();
			} else {
				if (option.charAt(0) == '*') {
					int chopOff = 2;
					JMenuItem item = null;
					
					if (option.charAt(1) == 'N') {
						item = new JCheckBoxMenuItem();
						item.setSelected(false);
					}
					else if (option.charAt(1) == 'Y') {
						item = new JCheckBoxMenuItem();
						item.setSelected(true);
					}
					else if (option.charAt(1) == 'S') {
						item = new JMenuItem();
						chopOff = 4;
						
						// Shortcut
						int modifiers = -1;System.out.println(option.charAt(3));
						switch(option.charAt(3)) {
						case 'c': modifiers = InputEvent.CTRL_MASK; break;
						case 's': modifiers = InputEvent.SHIFT_MASK; break;
						case 'a': modifiers = InputEvent.ALT_MASK; break;
						case 'S': modifiers = InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK; break;
						case 'A': modifiers = InputEvent.CTRL_MASK | InputEvent.ALT_MASK; break;
						}
						
						if (modifiers == -1) { 
							stroke = KeyStroke.getKeyStroke(option.charAt(2));
						} else {
							stroke = KeyStroke.getKeyStroke(option.charAt(2), modifiers);
						}
						
						
						if (stroke != null) item.setAccelerator(stroke);
					}
					
					option = option.substring(chopOff);
					item.setText(option);
					
					
					item.setActionCommand(option.toUpperCase());
					if (al != null)
						item.addActionListener(al);
					menu.add(item);		
				} else {
					JMenuItem item = new JMenuItem(option);
					item.setActionCommand(option.toUpperCase());
					if (al != null)
						item.addActionListener(al);
					menu.add(item);
				}
			}
		}
		
		add(menu);
		
		return menu;
	}
}
