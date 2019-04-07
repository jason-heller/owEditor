package application.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.UIManager;

public class HintTextField extends JTextField {
	private String hint;
	
	public HintTextField(String hint) {
		super(1);
		this.hint = hint;
		
		this.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				SwingControl.isTyping = true;
			}

			@Override
			public void focusLost(FocusEvent e) {
				SwingControl.isTyping = false;
			}
			
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (this.getText().length() == 0) {
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			FontMetrics metrics = g.getFontMetrics();
			Insets insets = getInsets();
			g.setColor(UIManager.getColor("nimbusSelectedText"));
			g.drawString(hint, insets.left + 2, (getHeight()/2) + (metrics.getAscent()/2) - 2);
		}
	}
}
