package application.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import application.Globals;
import application.Profile;
import assets.Asset;
import assets.Entity;
import assets.Model;
import assets.TextureAsset;
import editor.EditorControl;
import opengl.tex.Texture;
import utils.EntryType;

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

	private final JSpinner txSpinner, tySpinner;
	private final JPanel fineTunePanel;
	
	private final HintTextField searchBox;

	public EditorList() {

		// Create the combo box, select the item at index 4.
		// Indices start at 0, so 4 specifies the pig.
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		models = new JList<Model>(mList);
		textures = new JList<TextureAsset>(tList);
		entities = new JList<Entity>(eList);

		models.setPreferredSize(new Dimension(220, 720));
		textures.setPreferredSize(new Dimension(220, 720));
		entities.setPreferredSize(new Dimension(220, 720));

		models.setVisibleRowCount(32);
		textures.setVisibleRowCount(32);
		entities.setVisibleRowCount(32);

		models.setSelectedIndex(0);
		textures.setSelectedIndex(0);
		entities.setSelectedIndex(0);
		
		searchBox = new HintTextField("Search Filter...");
		searchBox.setMaximumSize(new Dimension(400, 64));
		
		tabbedPane = new JTabbedPane();
		miniPane = new JPanel();
		
		models.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (models.getSelectedValue() != null) {
					EditorControl.setEntity(null);
					EditorControl.setModel(models.getSelectedValue());
				}
			}
		});
		
		textures.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (textures.getSelectedValue() != null) {
					EditorControl.setEntity(null);
					EditorControl.setTexture(textures.getSelectedValue());
				}
			}
		});
		
		entities.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				EditorControl.setEntity(entities.getSelectedValue());
			}
		});
		
		searchBox.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent  e) {
				search();
			}

			@Override
			public void removeUpdate(DocumentEvent  e) {
				search();
			}

			@Override
			public void insertUpdate(DocumentEvent  e) {
				search();
			}
			
			private void search() {
				searchFilter(searchBox.getText());
			}
		});

		final JPanel modelPanel = new JPanel();
		final JPanel tabPanel = new JPanel();
		tabPanel.setLayout(new BoxLayout(tabPanel, BoxLayout.Y_AXIS));
		tabPanel.setPreferredSize(new Dimension(220, 720));

		modelPanel.add(models);

		final JPanel texturePanel = new JPanel();
		texturePanel.add(textures);

		final JPanel entPanel = new JPanel();
		entPanel.add(entities);

		tabbedPane.addTab("Models", modelPanel);
		tabbedPane.addTab("Textures", texturePanel);
		tabbedPane.addTab("Entities", entPanel);
		
		changedTabListener();

		tabPanel.add(searchBox);
		tabPanel.add(tabbedPane);

		fineTunePanel = new JPanel();
		fineTunePanel.setLayout(new BoxLayout(fineTunePanel, BoxLayout.Y_AXIS));
		txSpinner = addSpinner(fineTunePanel, "tx offset", new SpinnerNumberModel(0.0, -100000.0, 100000.0, .001));
		tySpinner = addSpinner(fineTunePanel, "ty offset", new SpinnerNumberModel(0.0, -100000.0, 100000.0, .001));
		fineTunePanel.setVisible(false);

		miniPane.add(fineTunePanel);

		add(tabPanel, BorderLayout.NORTH);
		add(miniPane, BorderLayout.SOUTH);
		
		DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
			
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				
				EntryType type = ((Asset) value).type;
				setForeground(type.getColor());
				
				this.setFont(new Font("Arial", Font.BOLD, 16));
				
				if (isSelected) setBackground(Color.CYAN);
				
				return c;
			}
		};
		
		models.setCellRenderer(renderer);
		textures.setCellRenderer(renderer);
		entities.setCellRenderer(renderer);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//final String cmd = e.getActionCommand();
	}

	public void add(Model model) {
		if (model.type == EntryType.UTILITY && !Globals.showUtilities) {
			return;
		}
		mList.addElement(model);
	}

	public void add(TextureAsset texture) {
		if (texture.type == EntryType.UTILITY && !Globals.showUtilities) {
			return;
		}
		tList.addElement(texture);
	}
	
	public void add(Entity entity) {
		if (entity.type == EntryType.UTILITY && !Globals.showUtilities) {
			return;
		}
		eList.addElement(entity);
	}

	private void changedTabListener() {
		final ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent changeEvent) {
				//final JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
				//final int index = sourceTabbedPane.getSelectedIndex();
				searchFilter(searchBox.getText());
			}
		};
		tabbedPane.addChangeListener(changeListener);
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
			//defaultPanel.setVisible(false);
			//brushPanel.setVisible(false);
			break;
		case 2:
			fineTunePanel.setVisible(false);
			//defaultPanel.setVisible(false);
			//brushPanel.setVisible(true);
			break;
		default:
			fineTunePanel.setVisible(false);
			//defaultPanel.setVisible(true);
			//brushPanel.setVisible(false);
		}
	}

	public void searchFilter(String filter) {
		filter = filter.toLowerCase();
		DefaultListModel listModel;
		//JList list;
		Asset[] assets;
		
		switch(tabbedPane.getSelectedIndex()) {
		case 1:
			listModel = tList;
			assets = Profile.textures;
			//list = textures;
			break;
		case 2:
			listModel = eList;
			assets = Profile.entities;
			//list = entities;
			break;
		default:
			listModel = mList;
			assets = Profile.models;
			//list = models;
		}

		listModel.clear();
		
		if (filter.equals("")) {
			for(Asset asset : assets) {
				listModel.addElement(asset);
			}
		} else {
			EntryType type = EntryType.get(filter);
			
			for(Asset asset : assets) {
				
				if (asset.type == EntryType.UTILITY && !Globals.showUtilities) {
					continue;
				}
				
				if (type != EntryType.DEFAULT && asset.type == type) {
					listModel.addElement(asset);
				} else {
					boolean tagged = false;
					for(String tag : asset.tags) {
						if (tag.equals(filter)) {
							listModel.addElement(asset);
							tagged = true;
							break;
						}
					}
					
					if (!tagged) {
						if (asset.name.toLowerCase().contains(filter)) {
							listModel.addElement(asset);
						}
					}
				}
			}
		}
		
		//list.setModel(listModel);
	}

	public void clear() {
		mList.clear();
		tList.clear();
		eList.clear();
	}
}
