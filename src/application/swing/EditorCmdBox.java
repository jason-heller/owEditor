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

import application.Globals;
import application.ToolShape;
import console.Console;

@SuppressWarnings("serial")
public class EditorCmdBox extends JPanel {
	protected JTextArea textArea;
	protected HintTextField input;
	protected String newline = "\n";

	public EditorCmdBox() {
		super();
		setLayout(new GridBagLayout());

		textArea = new JTextArea(5, 90);
		Console.setLog(textArea);
		input = new HintTextField("cmd");
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

	protected void displayResult(String actionDescription) {
		textArea.append(actionDescription + newline);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}