package application.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import application.Globals;
import application.Tool;

public class EditorToolbar extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	static final private String OBJTOOL = "object";
	static final private String TERRAIN = "terrain";
	static final private String TERRAIN_SMOOTH = "terrain_smooth";
	static final private String TEX = "tex";
	static final private String BRUSH = "brush";
	static final private String BRUSHFLOOR = "bfloor";
	protected JTextArea textArea;
	protected EditorCmdBox cmd;

	public EditorToolbar(EditorCmdBox cmdBox) {
		super(new BorderLayout());

		this.cmd = cmdBox;
		// Create the toolbar.
		final JToolBar toolBar = new JToolBar("", SwingConstants.VERTICAL);
		toolBar.setFloatable(false);
		addButtons(toolBar);

		// Lay out the main panel.
		setPreferredSize(new Dimension(48, 400));
		add(toolBar, BorderLayout.PAGE_START);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();

		if (OBJTOOL.equals(cmd)) {

		} else if (TERRAIN.equals(cmd)) { // first button clicked
			Globals.tool = Tool.HEIGHTTOOL;
		} else if (TERRAIN_SMOOTH.equals(cmd)) { // first button clicked

		} else if (TEX.equals(cmd)) { // third button clicked

		} else if (BRUSH.equals(cmd)) { // third button clicked
	
		} else if (BRUSHFLOOR.equals(cmd)) { // third button clicked

		} else {
			displayResult("Todo: Give this button purpose in life.");
		}
	}

	protected void addButtons(JToolBar toolBar) {
		JButton button = null;

		button = makeNavigationButton("obj", OBJTOOL, "object editor", "object editor");
		toolBar.add(button);

		button = makeNavigationButton("terrain_edit", TERRAIN, "terrain editor", "terrain editor");
		toolBar.add(button);
		
		button = makeNavigationButton("terrain_smooth", TERRAIN_SMOOTH, "terrain smoother", "terrain smoother");
		toolBar.add(button);

		button = makeNavigationButton("tex", TEX, "texture editor", "texure editor");
		toolBar.add(button);

		button = makeNavigationButton("brush", BRUSH, "wall tool", "configure info");
		toolBar.add(button);

		button = makeNavigationButton("bfloor", BRUSHFLOOR, "floor tool", "configure info");
		toolBar.add(button);
	}

	protected void displayResult(String actionDescription) {
		cmd.displayResult(actionDescription);
	}

	protected JButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText) {
		// Look for the image.
		final String imgLocation = "" + imageName + ".gif";
		final URL imageURL = EditorToolbar.class.getResource(imgLocation);

		// Create and initialize the button.
		final JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		return button;
	}
}