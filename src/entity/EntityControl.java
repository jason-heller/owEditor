package entity;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import assets.Entity;
import assets.Model;
import assets.TextureAsset;
import heightmap.HeightmapControl;
import io.SaveData;
import io.data.DataChunk;
import io.data.DataElement;
import io.data.DataFormat;
import opengl.GLWindow;
import opengl.tex.Texture;
import utils.Input;
import utils.MathUtils;

public class EntityControl extends SaveData {
	private static List<PlacedEntity> entities = new ArrayList<PlacedEntity>();
	public static List<PlacedEntity> selected = new ArrayList<PlacedEntity>();
	private static List<PlacedEntity> copied = new ArrayList<PlacedEntity>();
	
	private static Vector3f translation = new Vector3f();
	private static Vector3f rotation = new Vector3f();
	private static Vector3f rotationAxis = new Vector3f(0,1,0);
	private static float scale = 0f;
	private static boolean translating, rotating, scaling;
	private static Vector3f translationAnchor = new Vector3f();
	private static Vector2f rotationAnchor = new Vector2f();
	private static Vector2f scalingAnchor = new Vector2f();
	
	public void placeEntity(Model model, TextureAsset texture) {
		Vector3f position = HeightmapControl.picker.getCurrentTerrainPoint();
		
		if (position == null) return;
		
		ModelEntity mEnt = new ModelEntity(model, texture);
		entities.add(mEnt);
		mEnt.setPosition(position);
	}
	
	public void placeEntity(Entity entity) {
		Vector3f position = HeightmapControl.picker.getCurrentTerrainPoint();
		
		if (position == null) return;
		
		PlacedEntity ent = new PlacedEntity(entity);
		entities.add(ent);
		ent.setPosition(position);
	}
	
	public void drawEntities() {
		for(PlacedEntity e : entities) {
			Model model = e.getModel();
			Texture texture = e.getTexture().texture;

			texture.bind();	
			GL11.glPushMatrix();
			
			if (e.isSelected()) {
				GL11.glTranslatef(e.position.x+EntityControl.translation.x,
						e.position.y+EntityControl.translation.y,
						e.position.z+EntityControl.translation.z);
				GL11.glRotatef(e.rotation.x+EntityControl.rotation.x, 1,0,0);
				GL11.glRotatef(e.rotation.y+EntityControl.rotation.y, 0,1,0);
				GL11.glRotatef(e.rotation.z+EntityControl.rotation.z, 0,0,1);
				GL11.glScalef(e.scale+EntityControl.scale, e.scale+EntityControl.scale, e.scale+EntityControl.scale);
			} else {
				GL11.glTranslatef(e.position.x, e.position.y, e.position.z);
				GL11.glRotatef(e.rotation.x, 1,0,0);
				GL11.glRotatef(e.rotation.y, 0,1,0);
				GL11.glRotatef(e.rotation.z, 0,0,1);
				GL11.glScalef(e.scale, e.scale, e.scale);
			}

			model.draw();
			
			if (e.isSelected()) {
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
				model.draw();
				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
			
			GL11.glPopMatrix();
		}
	}
	
	public void step() {
		if (translating) {
			Vector3f tPoint = HeightmapControl.picker.getCurrentTerrainPoint();
			if (tPoint != null) {
			
				Vector3f diff = Vector3f.sub(tPoint, translationAnchor);
				
				if (Input.isDown(Keyboard.KEY_LSHIFT)) {
					translation.y = -diff.x;
					translation.z = diff.z;
				} else {
					
					translation.x = diff.x;
					translation.z = diff.z;
				}
			}
		}
		
		else if (rotating) {
			if (rotationAxis.x != 0)
				rotation.x = Vector2f.distance(rotationAnchor, new Vector2f(Mouse.getX(), Mouse.getY()))/2f;
			
			if (rotationAxis.y != 0)
				rotation.y = Vector2f.distance(rotationAnchor, new Vector2f(Mouse.getX(), Mouse.getY()))/2f;
			
			if (rotationAxis.z != 0)
				rotation.z = Vector2f.distance(rotationAnchor, new Vector2f(Mouse.getX(), Mouse.getY()))/2f;
			
			
			if (!Input.isDown(Keyboard.KEY_LCONTROL)) {
				if (Input.isPressed(Keyboard.KEY_X)) {
					rotationAxis.set(1,0,0);
					beginRotating();
				}
				
				if (Input.isPressed(Keyboard.KEY_Y)) {
					rotationAxis.set(0,1,0);
					beginRotating();
				}
				
				if (Input.isPressed(Keyboard.KEY_Z)) {
					rotationAxis.set(0,0,1);
					beginRotating();
				}
			}
		}
		
		else if (scaling) {
			scale = Vector2f.distance(scalingAnchor, new Vector2f(Mouse.getX(), Mouse.getY()))/6f;
		}
	}

	public void selectEntity(Vector3f currentRay, Vector3f currentTerrainPoint) {
		boolean ctrl = Input.isDown(Keyboard.KEY_LCONTROL);
		
		for(PlacedEntity e : entities) {
			if (!ctrl) {
				e.deselect();
				selected.remove(e);
			}
			
			if (e.visibleInEditor
			&&  MathUtils.entityBroadphaseIntersection(GLWindow.camera.getPosition(), currentRay, e)
			&&  MathUtils.rayIntersectsMesh(GLWindow.camera.getPosition(), currentRay, e.position, e.rotation, e.scale, e.getModel().getVertices())) {
				e.toggleSelect();
				if (e.isSelected()) {
					selected.add(e);
				} else {
					selected.remove(e);
				}
			}
		}
	}

	public void deleteSelected() {
		//for(PlacedEntity e : selected) {
			
		//}
		
		entities.removeAll(selected);
		selected.clear();
	}
	
	public void copy() {
		copied.clear();
		for(PlacedEntity e : selected) {
			copied.add(new PlacedEntity(e.position, e));
		}
		
		if (copied.size() > 1) {
			Vector3f smallest = copied.get(0).position;
			for(int i = 1; i < copied.size(); i++) {
				Vector3f target = copied.get(i).position;
				if (target.x < smallest.x && target.y < smallest.y && target.z < smallest.z) {
					smallest = target;
					PlacedEntity e = copied.get(i);
					copied.remove(i);
					copied.add(0, e);
				}
			}
		}
	}
	
	public void cut() {
		copied.clear();
		for(PlacedEntity e : selected) {
			copied.add(new PlacedEntity(e.position, e));
		}
		deleteSelected();
	}
	
	public void paste() {
		if (copied.size() == 0) return; 
		
		Vector3f position = HeightmapControl.picker.getCurrentTerrainPoint();
		selected.clear();
		
		Vector3f origin = copied.get(0).position;
		for(PlacedEntity e : copied) {
			PlacedEntity ne = new PlacedEntity(Vector3f.add(position, Vector3f.sub(e.position, origin)), e);
			ne.select();
			entities.add(ne);
			selected.add(ne);
		}
	}

	public void endTransforms() {
		for(PlacedEntity e : selected) {
			e.position.add(translation);
			e.rotation.add(rotation);
			e.scale += scale;
		}
		
		translation.zero();
		rotation.zero();
		scale = 0f;
		
		translating = false;
		rotating = false;
		scaling = false;
	}
	
	public void cancelTransforms() {
		translation.zero();
		rotation.zero();
		scale = 0f;
		
		translating = false;
		rotating = false;
		scaling = false;
	}

	public void beginTranslating(Vector3f anchor) {
		if (selected.size() == 0) return;
		rotating = false;
		scaling = false;
		translating = true;
		
		if (anchor == null) {
			translationAnchor.set(selected.get(0).position);
		} else {
			translationAnchor.set(anchor);
		}
	}
	
	public void beginRotating() {
		if (selected.size() == 0) return;
		rotating = true;
		scaling = false;
		translating = false;
		
		rotationAnchor.set(Mouse.getX(), Mouse.getY());
	}
	
	public void beginScaling() {
		if (selected.size() == 0) return;
		rotating = false;
		scaling = true;
		translating = false;
		
		scalingAnchor.set(Mouse.getX(), Mouse.getY());
	}
	
	@Override
	public boolean createDataChunks() {
		for(PlacedEntity entity : entities) {
			boolean isModelEntity = (entity instanceof ModelEntity);
			DataChunk chunk = new DataChunk(DataFormat.FORMAT_ENTITY);
			
			if (isModelEntity) {
				chunk.put("name", "Static Prop");
				chunk.put("isVisible", Boolean.toString(entity.isVisible));
				chunk.put("properties", "");
			} else {
				chunk.put("name", entity.getEntity().name);
				chunk.put("isVisible", Boolean.toString(entity.getEntity().isVisible?entity.isVisible:false));
				chunk.put("properties", entity.getProperties());
			}
			
			chunk.put("id", entity.id);
			chunk.put("pos", entity.position.x + "," + entity.position.y + "," + entity.position.z);
			chunk.put("rot", entity.rotation.x + "," + entity.rotation.y + "," + entity.rotation.z);
			chunk.put("scale", Float.toString(entity.scale));
			chunk.put("model", entity.getModel().toString());
			chunk.put("texture", entity.getTexture().toString());
			
			addDataChunk(chunk);
		}
		
		return true;
	}

	public void addEntity(PlacedEntity e) {
		entities.add(e);
	}

	@Override
	public void clear() {
		entities.clear();
		selected.clear();
		copied.clear();
	}
}
