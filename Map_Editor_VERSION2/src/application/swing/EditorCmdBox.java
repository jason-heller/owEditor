package application.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import console.Console;

@SuppressWarnings("serial")
public class EditorCmdBox extends JPanel implements ActionListener {
	protected JTextArea textArea;
	protected JTextField input;
	protected String newline = "\n";

	private final JPanel btnPanel;

	public EditorCmdBox() {
		super();
		setLayout(new GridBagLayout());

		// BUTTON PANEL /////////////////////////////////////////
		btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(0, 1280 / 12));

		//Application.TYPFix = addButton(true, "yplane", "YPLANE", "Toggle Y-Plane");
		addButton(false, "yplanereset", "YPLANERESET", "Reset Y-Plane to origin");
		final JToggleButton btn = (JToggleButton) addButton(true, "hmapvis", "HMAPVIS", "Toggle heightmap visibility");
		btn.setSelected(true);
		////////////////////////////////////////////////////////////

		SwingControl.jFrame.add(btnPanel, BorderLayout.NORTH);
		textArea = new JTextArea(5, 90);
		Console.setLog(textArea);
		input = new JTextField(1);
		textArea.setEditable(false);
		input.setEditable(true);
		final JScrollPane scrollPane = new JScrollPane(textArea);

		input.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Console.send(input.getText());
				input.setText("");

			}
		});

		// Lay out the main panel.
		setPreferredSize(new Dimension(450, 110));
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5f;
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridy = 0;
	    c.ipady = 90;
		add(scrollPane, c);
		c.weightx = 0.5f;
		c.fill = GridBagConstraints.HORIZONTAL;
	    c.gridx = 0;
	    c.gridy = 1;
	    c.ipady = 10;
		add(input, c);
		
		//PrintStream printStream = new PrintStream(new TextAreaOutputStream(textArea));
		//System.setOut(printStream);
		//System.setErr(printStream);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();

		/*if ("YPLANE".equals(cmd)) {
			final JToggleButton button = (JToggleButton) e.getSource();
			((PlayingScene) Application.scene).yPlane = button.isSelected();
		}

		else if ("YPLANERESET".equals(cmd)) {
			((PlayingScene) Application.scene).yPlaneHeight = 0;
		}

		else if ("HMAPVIS".equals(cmd)) {
			final JToggleButton button = (JToggleButton) e.getSource();
			((PlayingScene) Application.scene).hmapVis = button.isSelected();
		}*/
	}

	private AbstractButton addButton(boolean toggle, String img, String cmd, String about) {
		AbstractButton button;
		if (toggle) {
			button = new JToggleButton();
		} else {
			button = new JButton();
		}
		final String imgLocation = img + ".gif";
		final URL imageURL = EditorToolbar.class.getResource(imgLocation);
		button.setIcon(new ImageIcon(imageURL, about));
		button.setToolTipText(about);
		// imageURL = EditorToolbar.class.getResource(img + "pressed.gif");
		// button.setPressedIcon(new ImageIcon(imageURL, about));
		button.setActionCommand(cmd);
		button.addActionListener(this);

		btnPanel.add(button);
		return button;
	}

	protected void displayResult(String actionDescription) {
		textArea.append(actionDescription + newline);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}