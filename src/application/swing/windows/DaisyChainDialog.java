package application.swing.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joml.Matrix4f;
import org.joml.Quaternion;
import org.joml.Vector3f;

import application.swing.SwingControl;
import assets.Entity;
import console.Console;
import entity.EntityControl;
import entity.ModelEntity;
import entity.PlacedEntity;
import utils.AppUtils;

public class DaisyChainDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	protected static final int MAX_THETA = 90;
	
	private List<PlacedEntity> entities = new ArrayList<PlacedEntity>();
	private int axis = 3, sign = 1;
	int radius = 0;
	private float theta = 0;
	
	private JSlider rSlider;
	public DaisyChainDialog(ModelEntity parent, EntityControl eCtrl) {
		this((PlacedEntity)parent, eCtrl);
	}
	
	public DaisyChainDialog(PlacedEntity parent, EntityControl eCtrl) {
		super(SwingUtilities.windowForComponent(SwingControl.jFrame));
		this.setAlwaysOnTop(true);
		//this.eCtrl = eCtrl;
		
		entities.add(parent);
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setTitle("Daisy Chain Creator");
		
		/////////////////////////////
		// DIRECTIONAL BUTTONS
		/////////////////////////////
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel direction = new JPanel();
		direction.setLayout(new BoxLayout(direction, BoxLayout.X_AXIS));
		JButton signBtn = new JButton("pos");
		signBtn.addActionListener(new ActionListener() {
			@Override
			public  void actionPerformed(ActionEvent arg0) {
				if (signBtn.getText().equals("pos")) {
					signBtn.setText("neg");
					sign = -1;
				} else {
					signBtn.setText("pos");
					sign = 1;
				}
			}
		});
		direction.add(signBtn);
		
		ButtonGroup bg = new ButtonGroup();
		JToggleButton xBtn = new JToggleButton("X");
		direction.add(xBtn);
		bg.add(xBtn);
		JToggleButton yBtn = new JToggleButton("Y");
		direction.add(yBtn);
		bg.add(yBtn);
		JToggleButton zBtn = new JToggleButton("Z");
		direction.add(zBtn);
		zBtn.setSelected(true);
		bg.add(zBtn);
		panel.add(direction);
		
		xBtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				axis = 1;
			}
		});
		
		yBtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				axis = 2;
			}
		});
		
		zBtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				axis = 3;
			}
		});
		
		/////////////////////////////
		// TOOLS
		/////////////////////////////
		
		JPanel editTools = new JPanel();
		//editTools.setLayout(new BoxLayout(editTools, BoxLayout.Y_AXIS));
		
		editTools.add(new JLabel("radius: "));
		rSlider = new JSlider(SwingConstants.HORIZONTAL,-32,32,0);
		rSlider.setPreferredSize(new Dimension(120,32));
		editTools.add(rSlider);
		
		rSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				radius = rSlider.getValue();
			}
		});
		
		editTools.add(new JLabel("theta: "));
		JSlider th = new JSlider(SwingConstants.HORIZONTAL,-MAX_THETA,MAX_THETA,0);
		th.setPreferredSize(new Dimension(120,32));
		editTools.add(th);
		
		th.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				theta = th.getValue();
			}
		});

		panel.add(editTools);
		
		JDialog dialog = this;
		JPanel buttons = new JPanel();
		
		add(panel);
		
		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PlacedEntity top = entities.get(entities.size()-1);
				Vector3f dir, newDir;
				float len;
				Vector3f rotation = new Vector3f(top.rotation);
				
				if (axis == 1) {
					dir = top.getObb().X;
					
					len = sign*(top.getObb().bounds.x*top.scale);
					rotation.y += theta;
					
					newDir = setRotation(dir, 0, theta, 0);
				} else if (axis == 2) {
					// TODO: Proper rotations
					dir = top.getObb().Y;
					
					len = sign*(top.getObb().bounds.y*top.scale);
					rotation.z += theta;
					
					newDir = setRotation(dir, 0, 0, theta);
				} else {
					dir = top.getObb().Z;
					
					len = sign*(top.getObb().bounds.z*top.scale);
					newDir = Vector3f.rotate(dir, Vector3f.Y_AXIS, -theta);
					rotation.y += theta;
					
					Quaternion q = new Quaternion().setEulerAnglesDegXYZ(0, theta, 0);
					newDir = setRotation(dir, 0, theta, 0);
					
				}
				
				len -= radius;
				
				//float offset = len*((-sign*theta)/180f);
				Vector3f position = Vector3f.add(top.position, Vector3f.mul(dir, len));
				position.add(Vector3f.mul(newDir, len));
				Console.log(""+dir);
				Console.log(""+newDir);
				
				PlacedEntity newPlacedEntity;
				Entity entity = top.getEntity();
				if (entity != null) {
					newPlacedEntity = new PlacedEntity(entity);
				} else {
					newPlacedEntity = new ModelEntity(top.getModel(), top.getTexture());
				}
				
				if (newPlacedEntity != null) {
					newPlacedEntity.rotation.set(rotation);
					newPlacedEntity.getObb().setRotation(newPlacedEntity.rotation);
					newPlacedEntity.scale = top.scale;
					newPlacedEntity.position.set(position);
					entities.add(newPlacedEntity);
					
					eCtrl.addEntity(newPlacedEntity);
				}
				
			}
		});
		buttons.add(add);
		
		JButton remove = new JButton("Remove");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (entities.size() > 1) {
					
					PlacedEntity ent = entities.get(entities.size()-1);
					eCtrl.removeEntity(ent);
					entities.remove(ent);
				}
			}
		});
		buttons.add(remove);
		
		JButton finsh = new JButton("Finsh");
		finsh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				entities.clear();
				AppUtils.close(dialog);
			}
		});
		buttons.add(finsh);
		
		
		JButton close = new JButton("Cancel");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 1; i < entities.size(); i++) {
					PlacedEntity ent = entities.get(i);
					eCtrl.removeEntity(ent);
				}

				entities.clear();
				AppUtils.close(dialog);
			}
		});
		buttons.add(close);
		
		add(buttons, BorderLayout.SOUTH);
		
		setLocationRelativeTo(null);
		pack();
	}

	public Vector3f setRotation(Vector3f vec, float rx, float ry, float rz) {
		Matrix4f matrix = new Matrix4f();

		matrix.rotateZ(rz);
		matrix.rotateY(ry);
		matrix.rotateX(rx);

		Vector3f v = new Vector3f(vec);

		matrix.transform(v);
		
		return v;
	}
}
