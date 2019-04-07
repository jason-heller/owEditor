package application.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
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
	private ButtonGroup buttonGroup;

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
			Globals.tool = Tool.ENTITYTOOL;
		} else if (TERRAIN.equals(cmd)) { // first button clicked
			Globals.tool = Tool.HEIGHTTOOL;
		} else if (TERRAIN_SMOOTH.equals(cmd)) { // first button clicked
			Globals.tool = Tool.SMOOTHTOOL;
		} else if (TEX.equals(cmd)) { // third button clicked
			Globals.tool = Tool.TEXTURETOOL;
		} else if (BRUSH.equals(cmd)) { // third button clicked
	
		} else if (BRUSHFLOOR.equals(cmd)) { // third button clicked

		} else {
			displayResult("Todo: Give this button purpose in life.");
		}
	}

	protected void addButtons(JToolBar toolBar) {
		JToggleButton button = null;
		buttonGroup = new ButtonGroup();

		button = makeNavigationButton("obj", OBJTOOL, "object editor", "object editor");
		button.setSelected(true);
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

	protected JToggleButton makeNavigationButton(String imageName, String actionCommand, String toolTipText, String altText) {
		// Look for the image.
		final String imgLocation = "" + imageName + ".gif";
		final URL imageURL = EditorToolbar.class.getResource(imgLocation);

		// Create and initialize the button.
		final JToggleButton button = new JToggleButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		buttonGroup.add(button);
		return button;
	}
}