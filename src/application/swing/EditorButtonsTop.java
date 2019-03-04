package application.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import application.Globals;
import application.ToolShape;
import heightmap.HeightmapControl;
import opengl.GLWindow;

public class EditorButtonsTop extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EditorButtonsTop() {
		super();
		setLayout(new FlowLayout(FlowLayout.LEFT));

		addButton(false, "circle_tool", "CIRCLE_TOOL_SHAPE", "Circle Brush Shape");
		addButton(false, "square_tool", "SQUARE_TOOL_SHAPE", "Square Brush Shape");
		addButton(true, "yplane", "TOGGLE_HEIGHT_DRAG", "Toggle height dragging");
		addDropdown("Smoothing:", "Tool smoothing", "LERP", "Linear", "Smooth");
		addSlider("Brush Width", "Change Brush Width", 1, 10, Globals.brushWidth, 1, new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Globals.brushWidth = (int) ((JSpinner) e.getSource()).getValue();
			}
		});
		addSeparator();
		
		addButton(true, "yplane", "YPLANE", "Toggle XZ-Plane");
		addButton(false, "yplanereset", "YPLANERESET", "Reset XZ-Plane to origin");
		addButton(false, "yplaneplus", "YPLANE_PLUS", "Increase XZ-Plane height");
		addButton(false, "yplaneminus", "YPLANE_MINUS", "Decrease XZ-Plane height");
		
		addSeparator();
		
		addSlider("Grid Size", "Change Grid Size", 1, 64, Globals.gridSize, 1, new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Globals.gridSize = (int) ((JSpinner) e.getSource()).getValue();
			}
		});
		
		addSeparator();
		addButton(true, "yplaneminus", "TOGGLE_FREECAM", "Toggle freecam mode");
		
		//btn.setSelected(true);
		
		//SwingControl.jFrame.add(btnPanel, BorderLayout.NORTH);
	}
	
	private void addSeparator() {
		add(Box.createRigidArea(new Dimension(12,0)));
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
		button.setPreferredSize(new Dimension(24,24));
		button.setActionCommand(cmd);
		button.addActionListener(this);

		add(button);
		return button;
	}
	
	private JSpinner addSlider(String label, String about, int min, int max, int val, int inc, ChangeListener listener) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel(val,min,max,inc));

		spinner.setToolTipText(about);
		spinner.setPreferredSize(new Dimension(48,24));
		spinner.addChangeListener(listener);

		add(new JLabel(label));
		add(spinner);
		return spinner;
	}
	
	private JComboBox<String> addDropdown(String label, String about, String cmd, String ... options) {
		JComboBox<String> menu = new JComboBox<String>();
		
		for(String option : options) {
			menu.addItem(option);
		}
		
		menu.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				String item = (String) e.getItem();
				
				actionPerformed(new ActionEvent(menu, ActionEvent.ACTION_PERFORMED, cmd+":"+item));
			} 

		});

		add(new JLabel(label));
		add(menu);
		return menu;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();

		if ("CIRCLE_TOOL_SHAPE".equals(cmd)) {
			Globals.toolShape = ToolShape.CIRCLE;
		}
		else if ("SQUARE_TOOL_SHAPE".equals(cmd)) {
			Globals.toolShape = ToolShape.SQUARE;
		}
		else if ("TOGGLE_HEIGHT_DRAG".equals(cmd)) {
			Globals.dragTerrain = !Globals.dragTerrain;
		}
		
		else if ("LERP:Linear".equals(cmd)) {
			Globals.smoothBrush = false;
		}
		else if ("LERP:Smooth".equals(cmd)) {
			Globals.smoothBrush = true;
		}
		
		else if ("YPLANE".equals(cmd)) {
			Globals.yPlaneVisible = !Globals.yPlaneVisible;
		}
		else if ("YPLANERESET".equals(cmd)) {
			HeightmapControl.resetYPlane();
		}
		else if ("YPLANE_PLUS".equals(cmd)) {
			HeightmapControl.raiseYPlane(Globals.gridSize);
		}
		else if ("YPLANE_MINUS".equals(cmd)) {
			HeightmapControl.raiseYPlane(-Globals.gridSize);
		}
		
		else if ("TOGGLE_FREECAM".equals(cmd)) {
			GLWindow.camera.toggleFreecam();
		}
	}
}
