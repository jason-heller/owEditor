package editor;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import application.Globals;
import application.Profile;
import application.Tool;
import application.swing.windows.DaisyChainDialog;
import assets.Entity;
import assets.Model;
import assets.TextureAsset;
import console.History;
import console.cmd.DeleteSelectedCommand;
import console.cmd.FastCloneCommand;
import console.cmd.PasteSelectedCommand;
import console.cmd.PlaceEntityCommand;
import console.cmd.RetextureEntityCommand;
import console.cmd.RetextureSelectedCommand;
import entity.EntityControl;
import entity.PlacedEntity;
import heightmap.Heightmap;
import heightmap.HeightmapControl;
import utils.AppUtils;
import utils.Input;
import utils.Keys;

public class EditorControl {
	private HeightmapControl heightmapControl;
	private EntityControl entityControl;
	
	private static Model selectedModel;
	private static TextureAsset selectedTexture;
	private static Entity selectedEntity;
	private static Model circleToolList;
	private static Model squareToolList;
	private static Model pointList;

	private static boolean fastCloning = false;
	
	public EditorControl() {
		heightmapControl = new HeightmapControl();
		entityControl = new EntityControl();
		
		this.createCircleToolList();
		this.createSquareToolList();
		this.createPointList();
		
		selectedModel = Profile.models[0];
		selectedTexture = Profile.textures[0];
	}
	
	public void step() {
		heightmapControl.step();
		entityControl.step();
		
		if (fastCloning && (!Input.isDown(KeyEvent.VK_ALT) || !Input.isMouseDown(0))) {
			fastCloning = false;
			entityControl.endTransforms();
		}
		
		if (Input.isMousePressed(0)) {
			if (Input.isDown(KeyEvent.VK_ALT) && !entityControl.isTransforming()) {
				entityControl.endTransforms();
				fastCloning = true;
				//entityControl.fastClone(HeightmapControl.picker.getCurrentRay());
				new FastCloneCommand(HeightmapControl.picker.getCurrentRay());
				
			} else {
				fastCloning = false;
				entityControl.endTransforms();
				entityControl.selectEntity(HeightmapControl.picker.getCurrentRay());
			}
		}
		
		switch(Globals.tool) {
		case ENTITYTOOL:
			if (Input.isMousePressed(1) && !fastCloning) {
				entityControl.cancelTransforms();
				
				if (selectedEntity == null) {
					new PlaceEntityCommand(selectedModel, selectedTexture);
				} else {
					new PlaceEntityCommand(selectedEntity);
				}
			}
			
			if (Input.isPressed(KeyEvent.VK_DELETE)) {
				//entityControl.deleteSelected();
				new DeleteSelectedCommand();
			}
			
			if (Input.isDown(KeyEvent.VK_CONTROL)) {
				if (Input.isPressed(Keys.C)) {
					entityControl.copy();
				}
				
				if (Input.isPressed(Keys.X)) {
					entityControl.cut();
				}
				
				if (Input.isPressed(Keys.V)) {
					//entityControl.paste();
					new PasteSelectedCommand();
				}
				
				if (Input.isPressed(Keys.Y)) {
					History.redo();
				}
				
				if (Input.isPressed(Keys.Z)) {
					History.undo();
				}
				
				if (Input.isPressed(Keys.D)) {
					if (EntityControl.selected.size() > 1) {
						AppUtils.warningMessage("Please select only one object");
					} else if (EntityControl.selected.size() == 0) {
						AppUtils.warningMessage("Please select an object");
					} else {
						//new DaisyChainDialog(EntityControl.selected.get(0), entityControl);
						AppUtils.open(DaisyChainDialog.class, false, ((PlacedEntity)EntityControl.selected.get(0)), entityControl);
		
					}
				}
				
				
				if (Input.getMouseDWheel() > 0) {
					for(PlacedEntity e : EntityControl.selected) {
						e.scale += 2;
					}
				}
				else if (Input.getMouseDWheel() < 0) {
					for(PlacedEntity e : EntityControl.selected) {
						e.scale -= 2;
					}
				}
			} else {
			
				if (Input.isPressed(KeyEvent.VK_T)) {
					EntityControl.beginTranslating(HeightmapControl.picker.getCurrentTerrainPoint());
				}
				
				if (Input.isPressed(KeyEvent.VK_R)) {
					entityControl.beginRotating();
				}
				
				if (Input.isPressed(KeyEvent.VK_E)) {
					entityControl.beginScaling();
				}
			}
			
			break;
			
		case TEXTURETOOL:
			if (Input.isMousePressed(1)) {
				PlacedEntity entity = EntityControl.getClicked(HeightmapControl.picker.getCurrentRay());
				
				if (entity != null) {
					new RetextureEntityCommand(entity, selectedTexture);
				}
			}
			
			if (Input.isPressed(KeyEvent.VK_ENTER)) {
				new RetextureSelectedCommand(selectedTexture);
			}
			break;
		
			default:
		}
	}
	
	public static void draw() {
		Vector3f p = HeightmapControl.picker.getCurrentTerrainPoint();
		if (p != null && Mouse.isInsideWindow()) {
			//GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glPushMatrix();
			GL11.glTranslatef(p.x, p.y, p.z);
			final float size = Globals.brushWidth*Heightmap.POLYGON_WIDTH;
			if (Globals.tool == Tool.HEIGHTTOOL || Globals.tool == Tool.SMOOTHTOOL) {
				switch(Globals.toolShape) {
				case CIRCLE:
					GL11.glScalef(size, size, size);
					circleToolList.draw();
					break;
				case SQUARE:
					GL11.glScalef(size, size, size);
					squareToolList.draw();
					break;
					default:
					pointList.draw();
				}
			} else {
				pointList.draw();
			}
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		
	}
	
	public static void setModel(Model model) {
		selectedModel = model;
	}
	
	public static void setTexture(TextureAsset texture) {
		selectedTexture = texture;
	}
	
	public static void setEntity(Entity e) {
		selectedEntity = e;
	}
	
	public void clean() {
		circleToolList.clean();
		squareToolList.clean();
		pointList.clean();
		heightmapControl.clean();
	}
	
	final float TOOL_SHAPE_HEIGHT = .25f / Heightmap.POLYGON_WIDTH;
	
	private void createCircleToolList() {
		circleToolList = Model.create();
		GL11.glNewList(circleToolList.id, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		
		final float steps = (float) ((Math.PI*2.0)/10);
		GL11.glColor3f(1, 1, 1);
		for (float theta = 0; theta < (Math.PI*2.0); theta += steps) {
			GL11.glVertex3f((float)Math.cos(theta), 0, (float)Math.sin(theta));
			GL11.glVertex3f((float)Math.cos(theta), TOOL_SHAPE_HEIGHT, (float)Math.sin(theta));
		}
		
		GL11.glVertex3f(1, 0, 0);
		GL11.glVertex3f(1, TOOL_SHAPE_HEIGHT, 0);
		
		GL11.glEnd();
		GL11.glEndList();
	}
	
	private void createSquareToolList() {
		squareToolList = Model.create();
		GL11.glNewList(squareToolList.id, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_QUAD_STRIP);
		
		GL11.glColor3f(1, 1, 1);
		GL11.glVertex3f(-1,0,1);
		GL11.glVertex3f(-1,TOOL_SHAPE_HEIGHT,1);
		GL11.glVertex3f(1,0,1);
		GL11.glVertex3f(1,TOOL_SHAPE_HEIGHT,1);
		GL11.glVertex3f(1,0,-1);
		GL11.glVertex3f(1,TOOL_SHAPE_HEIGHT,-1);
		GL11.glVertex3f(-1,0,-1);
		GL11.glVertex3f(-1,TOOL_SHAPE_HEIGHT,-1);
		GL11.glVertex3f(-1,0,1);
		GL11.glVertex3f(-1,TOOL_SHAPE_HEIGHT,1);
		
		GL11.glEnd();
		GL11.glEndList();
	}
	
	private void createPointList() {
		pointList = Model.create();
		GL11.glNewList(pointList.id, GL11.GL_COMPILE);
		GL11.glBegin(GL11.GL_LINES);
		
		GL11.glColor3f(1, 1, 1);
		GL11.glVertex3f(1, 0, 0);
		GL11.glVertex3f(-1, 0, 0);
		GL11.glVertex3f(0, 0, 1);
		GL11.glVertex3f(0, 0, -1);
		GL11.glVertex3f(0, 1, 0);
		GL11.glVertex3f(0, -1, 0);
		
		GL11.glEnd();
		GL11.glEndList();
	}
	
	public HeightmapControl getHeightmapControl() {
		return heightmapControl;
	}

	public EntityControl getEntityControl() {
		return entityControl;
	}

	public void setEntityControl(EntityControl state) {
		this.entityControl = state;
	}
}
