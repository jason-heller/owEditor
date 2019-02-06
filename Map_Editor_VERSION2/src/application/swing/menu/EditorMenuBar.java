package application.swing.menu;

import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class EditorMenuBar extends JMenuBar {
	
	public EditorMenuBar() {
		addMenu("File", 'f', null, "new", "open", "save");
		addMenu("Edit", 'e', null, "import ENV data [wip]", "|", "test");
		addMenu("View", 'v', new ViewMenuListener(), "*Yworld view", "*Nbiome view", "|", "World view in window");

	}
	
	private JMenu addMenu(String name, char mnemonic, ActionListener al, String ... options) {
		JMenu menu = new JMenu(name);
		menu.setMnemonic(mnemonic);
	
		for(String option : options) {
			if (option.equals("|")) {
				menu.addSeparator();
			} else {
				if (option.charAt(0) == '*') {
					boolean checked = true;
					if (option.charAt(1) == 'N') {
						checked = false;
					}
					
					option = option.substring(2);
					
					JCheckBoxMenuItem item = new JCheckBoxMenuItem(option);
					item.setActionCommand(option.toUpperCase());
					item.setSelected(checked);
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
