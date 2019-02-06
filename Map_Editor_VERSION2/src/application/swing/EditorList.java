package application.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import assets.Entity;
import assets.Model;

@SuppressWarnings("serial")
public class EditorList extends JPanel implements ActionListener {

	static protected JSpinner addSpinner(Container c, SpinnerModel model) {
		final JSpinner spinner = new JSpinner(model);
		c.add(spinner);

		return spinner;
	}

	static protected JSpinner addSpinner(Container c, String label, SpinnerModel model) {
		final JLabel l = new JLabel(label);
		c.add(l);

		final JSpinner spinner = new JSpinner(model);
		l.setLabelFor(spinner);
		c.add(spinner);

		return spinner;
	}

	static private JComboBox<String> makeComboBox(ActionListener al, int initialIndex, String... options) {
		final JComboBox<String> comboBox = new JComboBox<String>(options);
		comboBox.setSelectedIndex(initialIndex);
		comboBox.addActionListener(al);
		return comboBox;
	}

	public JList<Model> models;
	public JList<Texture> textures;
	public JList<Entity> entities;
	public JComboBox<String> clips;

	public DefaultListModel<Model> mList = new DefaultListModel<Model>();
	public DefaultListModel<Texture> tList = new DefaultListModel<Texture>();
	public DefaultListModel<Entity> eList = new DefaultListModel<Entity>();
	
	public final JTabbedPane tabbedPane;

	private final JPanel miniPane;

	private final JSpinner scaleBox, scaleBox2, scaleBox3;
	private final JSlider brushWidth;
	private final JSpinner txSpinner, tySpinner;
	private final JPanel defaultPanel;
	private final JPanel fineTunePanel;
	private final JPanel mdlConfigPanel;
	private final JPanel texConfigPanel;
	private final JFormattedTextField texNameField;
	private final JRadioButton isTransparent;
	final JFormattedTextField colMeshLoc;
	private final JButton colMeshPicker;

	private final JComboBox<String> solidityMenu;

	private final JFormattedTextField ModelNameField;

	private final JComboBox<String> materialMenu;
	private final JLabel sp1Text, sp2Text;
	private Vector3f clipColor = new Vector3f();

	public EditorList() {

		// Create the combo box, select the item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		scaleBox = EditorList.addSpinner(this, new SpinnerNumberModel(16, 1, 128, 1));
		scaleBox2 = EditorList.addSpinner(this, new SpinnerNumberModel(12, 1, 45, 1));
		scaleBox3 = EditorList.addSpinner(this, new SpinnerNumberModel(.25f, .1f, 2f, .1f));
		brushWidth = new JSlider(2,10,5);

		//specialProperty2.setVisible(false);
		sp1Text = new JLabel("A");
		//sp1Text.setVisible(false);
		sp2Text = new JLabel("A");
		//sp2Text.setVisible(false);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		final JPanel btns = new JPanel();
		btns.setLayout(new BoxLayout(btns, BoxLayout.X_AXIS));
		final JButton addBtn = new JButton("Add");
		addBtn.setActionCommand("ADD");
		addBtn.addActionListener(this);

		btns.add(addBtn);

		final JButton subBtn = new JButton("Del");
		subBtn.setActionCommand("DEL");
		subBtn.addActionListener(this);
		btns.add(subBtn);
		btns.setPreferredSize(new Dimension(240, 32));

		add(btns);

		models = new JList<Model>(mList);
		textures = new JList<Texture>(tList);
		entities = new JList<Entity>(eList);

		models.setPreferredSize(new Dimension(200, 480));
		textures.setPreferredSize(new Dimension(200, 480));
		entities.setPreferredSize(new Dimension(200, 480));

		models.setVisibleRowCount(16);
		textures.setVisibleRowCount(16);
		entities.setVisibleRowCount(16);

		models.setSelectedIndex(0);
		textures.setSelectedIndex(0);
		entities.setSelectedIndex(0);
		tabbedPane = new JTabbedPane();
		miniPane = new JPanel();

		final JPanel modelPanel = new JPanel();
		final JPanel tabPanel = new JPanel();
		tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));

		modelPanel.add(models, BorderLayout.NORTH);

		final JPanel texturePanel = new JPanel();
		texturePanel.add(textures, BorderLayout.NORTH);

		final JPanel entPanel = new JPanel();
		entPanel.add(entities, BorderLayout.NORTH);

		tabbedPane.addTab("Models", modelPanel);
		tabbedPane.addTab("Textures", texturePanel);
		tabbedPane.addTab("Entities", entPanel);
		changedTabListener();

		tabPanel.setPreferredSize(tabbedPane.getPreferredSize());

		tabPanel.add(tabbedPane);

		mdlConfigPanel = new JPanel();
		mdlConfigPanel.setLayout(new GridLayout(0, 1));
		mdlConfigPanel.add(new JLabel("Model properties"));
		mdlConfigPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
		ModelNameField = new JFormattedTextField("");
		ModelNameField.addActionListener(this);
		ModelNameField.setActionCommand("ModelNAME");
		mdlConfigPanel.add(ModelNameField);
		colMeshLoc = new JFormattedTextField("No collision mesh file");
		colMeshLoc.addActionListener(this);
		colMeshLoc.setActionCommand("COLMESHLOC");
		colMeshLoc.setEditable(false);
		colMeshLoc.setPreferredSize(new Dimension(128,24));
		
		colMeshPicker = new JButton("Browse");
		colMeshPicker.addActionListener(this);
		colMeshPicker.setActionCommand("COLMESHBUTTON");
		
		solidityMenu = makeComboBox(this, 0, "nonsolid", "bounding box", "collision mesh", "use model");
		solidityMenu.setActionCommand("SOLIDITY");
		JPanel colPanel = new JPanel();
		colPanel.add(colMeshLoc, BorderLayout.WEST);
		colPanel.add(colMeshPicker);
		mdlConfigPanel.add(colPanel);
		mdlConfigPanel.add(solidityMenu);
		mdlConfigPanel.add(add(new JSeparator(SwingConstants.HORIZONTAL)));
		tabPanel.add(tabbedPane);
		tabPanel.add(mdlConfigPanel);

		texConfigPanel = new JPanel();
		texConfigPanel.setLayout(new GridLayout(0, 1));
		texConfigPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
		// mdlConfigPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		isTransparent = new JRadioButton();
		isTransparent.addActionListener(this);
		isTransparent.setActionCommand("TRANSPARENT");
		isTransparent.setText("transparent");
		texNameField = new JFormattedTextField("");
		texNameField.addActionListener(this);
		texNameField.setActionCommand("TEXNAME");
		materialMenu = makeComboBox(this, 0, "TODO");
		materialMenu.setActionCommand("MATERIAL");
		texConfigPanel.add(new JLabel("Texture properties"));
		texConfigPanel.add(texNameField);
		texConfigPanel.add(isTransparent);
		texConfigPanel.add(materialMenu);
		texConfigPanel.add(add(new JSeparator(SwingConstants.HORIZONTAL)));
		texConfigPanel.setVisible(false);
		tabPanel.add(texConfigPanel);

		defaultPanel = new JPanel();
		defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.Y_AXIS));
		defaultPanel.add(new JLabel("Grid size:"));
		defaultPanel.add(scaleBox);
		defaultPanel.add(new JLabel("Rotation factor:"));
		defaultPanel.add(scaleBox2);
		defaultPanel.add(new JLabel("Scaling factor:"));
		defaultPanel.add(scaleBox3);
		defaultPanel.add(new JLabel("Brush width:"));
		defaultPanel.add(brushWidth);

		miniPane.add(defaultPanel);


		fineTunePanel = new JPanel();
		fineTunePanel.setLayout(new BoxLayout(fineTunePanel, BoxLayout.Y_AXIS));
		txSpinner = addSpinner(fineTunePanel, "tx offset", new SpinnerNumberModel(0.0, -100000.0, 100000.0, .001));
		tySpinner = addSpinner(fineTunePanel, "ty offset", new SpinnerNumberModel(0.0, -100000.0, 100000.0, .001));
		fineTunePanel.setVisible(false);

		miniPane.add(fineTunePanel);

		add(tabPanel, BorderLayout.NORTH);
		add(miniPane, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//final String cmd = e.getActionCommand();
	}

	public void add(Model Model) {
		mList.addElement(Model);
	}

	public void add(Texture texture) {
		tList.addElement(texture);
	}

	private void changedTabListener() {
		final ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				final JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				final int index = sourceTabbedPane.getSelectedIndex();
				// System.out.println("Tab changed to: " + sourceTabbedPane.getTitleAt(index));
				if (index == 0) {
					mdlConfigPanel.setVisible(true);
					texConfigPanel.setVisible(false);
				} else if (index == 1) {
					mdlConfigPanel.setVisible(false);
					texConfigPanel.setVisible(true);
				} else {
					mdlConfigPanel.setVisible(false);
					texConfigPanel.setVisible(false);
				}
			}
		};
		tabbedPane.addChangeListener(changeListener);
	}

	public int getGridSize() {
		return (int) this.scaleBox.getValue();
	}
	
	public int getBrushSize() {
		return (int) this.brushWidth.getValue();
	}

	public Model getModel() {
		return models.getSelectedValue();
	}

	public int getRotationSize() {
		return (int) this.scaleBox2.getValue();
	}

	public double getScaleSize() {
		return (double) this.scaleBox3.getValue();
	}

	public Texture getTexture() {
		return textures.getSelectedValue();
	}

	public double[] getTextureOffset() {
		return new double[] { (double) this.txSpinner.getValue(), (double) this.tySpinner.getValue() };
	}

	public void setSelectedTexture(Texture texture) {
		textures.setSelectedValue(texture, true);
	}

	public void togglePanes(int i) {
		switch (i) {
		case 1:
			fineTunePanel.setVisible(true);
			defaultPanel.setVisible(false);
			//brushPanel.setVisible(false);
			break;
		case 2:
			fineTunePanel.setVisible(false);
			defaultPanel.setVisible(false);
			//brushPanel.setVisible(true);
			break;
		default:
			fineTunePanel.setVisible(false);
			defaultPanel.setVisible(true);
			//brushPanel.setVisible(false);
		}
	}

}
