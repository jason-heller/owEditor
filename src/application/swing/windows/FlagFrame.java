package application.swing.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import application.Flag;
import application.Profile;
import utils.AppUtils;

public class FlagFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean[] values;
	
	public FlagFrame() {
		super();
		this.setAlwaysOnTop(true);
		
		values = new boolean[Profile.flags.length];
		for(int i = 0; i < values.length; i++) {
			values[i] = Profile.flags[i].value;
		}
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setTitle("World Flags");
		
		JPanel flags = new JPanel();
		flags.setLayout(new BoxLayout(flags, BoxLayout.Y_AXIS));
		for(Flag flag : Profile.flags) {
			JCheckBox cb = new JCheckBox();
			cb.setText(flag.name);
			cb.setSelected(flag.value);
			cb.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for(int i = 0; i < flags.getComponents().length; i++) {
						if (flags.getComponents()[i] == cb) {
							values[i] = cb.isSelected();
							break;
						}
					}
				}
				
			});
			flags.add(cb);
		}
		add(flags);
		
		JFrame frame = this;
		JPanel buttons = new JPanel();
		
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < values.length; i++) {
					Profile.flags[i].value = values[i];
				}
			}
		});
		buttons.add(apply);
		
		JButton applyAndClose = new JButton("Apply & Close");
		applyAndClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(int i = 0; i < values.length; i++) {
					Profile.flags[i].value = values[i];
				}
				AppUtils.close(frame);
			}
		});
		buttons.add(applyAndClose);
		
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Profile.resetFlags();
				for(int i = 0; i < flags.getComponents().length; i++) {
					values[i] = Profile.flags[i].value;
					((JCheckBox) flags.getComponents()[i]).setSelected(values[i]);
				}
			}
		});
		buttons.add(reset);
		
		JButton close = new JButton("Cancel");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AppUtils.close(frame);
			}
		});
		buttons.add(close);
		
		add(buttons, BorderLayout.SOUTH);
		
		setLocationRelativeTo(null);
		pack();
	}

}
