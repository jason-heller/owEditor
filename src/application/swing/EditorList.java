package application.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import application.Globals;
import assets.Entity;
import assets.Model;
import assets.TextureAsset;
import editor.EditorControl;
import opengl.tex.Texture;

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

	public JList<Model> models;
	public JList<TextureAsset> textures;
	public JList<Entity> entities;
	public JComboBox<String> clips;

	public DefaultListModel<Model> mList = new DefaultListModel<Model>();
	public DefaultListModel<TextureAsset> tList = new DefaultListModel<TextureAsset>();
	public DefaultListModel<Entity> eList = new DefaultListModel<Entity>();
	
	public final JTabbedPane tabbedPane;

	private final JPanel miniPane;

	private final JSpinner scaleBox;
	private final JSlider brushWidth;
	private final JSpinner txSpinner, tySpinner;
	private final JPanel defaultPanel;
	private final JPanel fineTunePanel;

	public EditorList() {

		// Create the combo box, select the item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		scaleBox = EditorList.addSpinner(this, new SpinnerNumberModel(Globals.gridSize, 1, 128, 1));
		brushWidth = new JSlider(0,10,Globals.brushWidth);
		//sp2Text.setVisible(false);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		models = new JList<Model>(mList);
		textures = new JList<TextureAsset>(tList);
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
		
		models.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				EditorControl.setModel(models.getSelectedValue());
			}
		});
		
		textures.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				EditorControl.setTexture(textures.getSelectedValue());
			}
		});

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

		defaultPanel = new JPanel();
		defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.Y_AXIS));
		defaultPanel.add(new JLabel("Grid size:"));
		defaultPanel.add(scaleBox);
		defaultPanel.add(new JLabel("Brush width:"));
		defaultPanel.add(brushWidth);

		miniPane.add(defaultPanel);

		brushWidth.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Globals.brushWidth = brushWidth.getValue();
			}
			
		});

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

	public void add(TextureAsset texture) {
		tList.addElement(texture);
	}
	
	public void add(Entity entity) {
		eList.addElement(entity);
	}

	private void changedTabListener() {
		final ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				//final JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				//final int index = sourceTabbedPane.getSelectedIndex();
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

	public TextureAsset getTexture() {
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
