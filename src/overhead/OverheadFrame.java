package overhead;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.OverlayLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import application.Globals;
import application.Profile;
import application.Tool;
import application.swing.CanvasFrame;

public class OverheadFrame extends CanvasFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private OverheadCanvas panel;
	private JPanel tools;
	private JPanel[] optionsPanel = new JPanel[3];
	@SuppressWarnings("unchecked")
	private JList<OverheadItem>[] lists = new JList[3];
	private JLayeredPane toolOptions;
	
	private static final int BIOMES = 0, REGIONS = 1, PATHS = 2;
	
	public OverheadFrame(String title) {
		super(title);
	}

	@Override
	protected void init() {}
	
	public void setup() {
		panel = new OverheadCanvas(this);
		panel.setPreferredSize(this.getSize());
		
		tools = new JPanel();
		tools.setLayout(new BoxLayout(tools, BoxLayout.X_AXIS));
		tools.setBounds(0,  0, this.getWidth(), 64);
		JPanel btns = new JPanel();
		ButtonGroup group = new ButtonGroup();
		btns.setPreferredSize(new Dimension(32,96));
		addButton("TOOL_BIOME", "Biome Tool", "biome_tool", btns, group);
		addButton("TOOL_REGION", "Region Tool", "region_tool", btns, group);
		addButton("TOOL_PATH", "Path Tool", "path_tool", btns, group);
		
		tools.add(btns);
		tools.add(Box.createRigidArea(new Dimension(32,128)));
		
		toolOptions = new JLayeredPane();
		toolOptions.setLayout(new OverlayLayout(toolOptions));
		addOptions(BIOMES, Profile.biomes);
		addOptions(REGIONS, Profile.regions);
		addOptions(PATHS, Profile.paths);
		tools.add(toolOptions);
		
		add(tools, BorderLayout.SOUTH);
		add(panel);
		
		pack();
		setVisible(true);
	}

	private void addOptions(int index, OverheadItem[] items) {
		DefaultListModel<OverheadItem> listModel = new DefaultListModel<OverheadItem>();
		for(OverheadItem path : items) {
			listModel.addElement(path);
		}
		
		lists[index] = new JList<OverheadItem>(listModel);
		lists[index].setVisibleRowCount(4);
		lists[index].setSelectedIndex(0);
		lists[index].addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
			}
		});
		
		optionsPanel[index] = new JPanel();
		optionsPanel[index] .setLayout(new BoxLayout(optionsPanel[index] , BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(lists[index]);
		scrollPane.setPreferredSize(new Dimension(80,64));
		optionsPanel[index] .add(scrollPane);
		toolOptions.add(optionsPanel[index] , 0, new Integer(index));
	}

	private JToggleButton addButton(String cmd, String about, String imageName, JPanel panel, ButtonGroup group) {
		JToggleButton button = new JToggleButton();
		button.addActionListener(this);
		button.setActionCommand(cmd);
		
		final String imgLocation = "" + imageName + ".gif";
		final URL imageURL = OverheadFrame.class.getResource(imgLocation);
		
		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, about));
		} else { // no image found
			button.setText(about);
			System.err.println("Resource not found: " + imgLocation);
		}
		
		panel.add(button, BorderLayout.NORTH);
		group.add(button);
		
		return button;
	}

	public OverheadCanvas getPanel() {
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		//JToggleButton src = (JToggleButton) e.getSource();
		
		if (cmd.equals("TOOL_BIOME")) {
			Globals.tool = Tool.BIOMETOOL;
			toolOptions.moveToFront(optionsPanel[BIOMES]);
		}
		else if (cmd.equals("TOOL_REGION")) {
			Globals.tool = Tool.REGIONTOOL;
			toolOptions.moveToFront(optionsPanel[REGIONS]);
		}
		else if (cmd.equals("TOOL_PATH")) {
			Globals.tool = Tool.PATHTOOL;
			toolOptions.moveToFront(optionsPanel[PATHS]);
		}
		
		OverheadControl.endPathBuilding();
	}
	
	public OverheadItem getSelected() {
		switch(Globals.tool) {
		case BIOMETOOL:
			return lists[BIOMES].getSelectedValue();
		case REGIONTOOL:
			return lists[REGIONS].getSelectedValue();
		case PATHTOOL:
			return lists[PATHS].getSelectedValue();
		default:
			return null;
		}
	}

}
