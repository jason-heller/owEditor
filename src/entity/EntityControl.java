package entity;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import application.Globals;
import application.Profile;
import assets.Entity;
import assets.Model;
import assets.TextureAsset;
import console.cmd.DeleteSelectedCommand;
import heightmap.HeightmapControl;
import io.SaveData;
import io.data.DataChunk;
import io.data.DataFormat;
import opengl.GLWindow;
import opengl.tex.Texture;
import utils.Input;
import utils.MathUtils;

public class EntityControl extends SaveData {
	public static List<PlacedEntity> entities = new ArrayList<PlacedEntity>();
	public static List<PlacedEntity> selected = new ArrayList<PlacedEntity>();
	private static List<PlacedEntity> copied = new ArrayList<PlacedEntity>();
	private static List<PlacedEntity> markedForDeletion = new ArrayList<PlacedEntity>();
	
	private static Vector3f translation = new Vector3f();
	private static Vector3f rotation = new Vector3f(), suggestedRotation = new Vector3f();
	private static Vector3f rotationAxis = new Vector3f(0,1,0);
	private static float scale = 0f;
	private static boolean translating, rotating, scaling;
	private static Vector3f translationAnchor = new Vector3f();
	private static Vector2f rotationAnchor = new Vector2f();
	private static Vector2f scalingAnchor = new Vector2f();
	
	public static ModelEntity placeEntity(Model model, TextureAsset texture) {
		Vector3f position = HeightmapControl.picker.getCurrentTerrainPoint();
		
		if (position == null) return null;
		
		if (model.associatedTexture != null && !model.associatedTexture.equals("")) {
			texture = Profile.getTexture(model.associatedTexture);
		}
		
		ModelEntity mEnt = new ModelEntity(model, texture);
		entities.add(mEnt);
		deselectAll();
		select(mEnt);
		mEnt.setPosition(position);
		return mEnt;
	}
	
	private static void deselectAll() {
		for(PlacedEntity e : selected) {
			e.deselect();
		}
		selected.clear();
	}
	
	private static void select(PlacedEntity e) {
		selected.add(e);
		e.select();
	}
	
	private static void deselect(PlacedEntity e) {
		selected.remove(e);
		e.deselect();
	}

	public static PlacedEntity placeEntity(Entity entity) {
		Vector3f position = HeightmapControl.picker.getCurrentTerrainPoint();
		
		if (position == null) return null;
		
		PlacedEntity ent = new PlacedEntity(entity);
		entities.add(ent);
		deselectAll();
		select(ent);
		ent.setPosition(position);
		
		return ent;
	}
	
	public synchronized void drawEntities() {
		for(PlacedEntity e : entities) {
			Model model = e.getModel();
			Texture texture = e.getTexture().texture;

			texture.bind();	
			GL11.glPushMatrix();
			
			if (e.isSelected()) {
				GL11.glTranslatef(e.position.x+EntityControl.translation.x,
						e.position.y+EntityControl.translation.y,
						e.position.z+EntityControl.translation.z);
				if (suggestedRotation.isZero()) {
					GL11.glRotatef(e.rotation.x+EntityControl.rotation.x, 1,0,0);
					GL11.glRotatef(e.rotation.y+EntityControl.rotation.y, 0,1,0);
					GL11.glRotatef(e.rotation.z+EntityControl.rotation.z, 0,0,1);
				} else {
					GL11.glRotatef(suggestedRotation.x, 1,0,0);
					GL11.glRotatef(suggestedRotation.y, 0,1,0);
					GL11.glRotatef(suggestedRotation.z, 0,0,1);
				}
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
		
		for(PlacedEntity e : markedForDeletion) {
			entities.remove(e);
			selected.remove(e);
			copied.remove(e);
		}
		
		markedForDeletion.clear();
	}
	
	public void step() {
		if (translating) {
			Vector3f tPoint = HeightmapControl.picker.getCurrentTerrainPoint();
			if (tPoint != null) {
			
				Vector3f diff = Vector3f.sub(tPoint, translationAnchor);
				
				if (Globals.gridSnap) {
					diff.x = (float) (Math.floor(diff.x / Globals.gridSize)*Globals.gridSize);
					diff.y = (float) (Math.floor(diff.y / Globals.gridSize)*Globals.gridSize);
					diff.z = (float) (Math.floor(diff.z / Globals.gridSize)*Globals.gridSize);	
					
					
				}
				
				if (selected.size() == 1) {
					if (Globals.objectSnap) {
						PlacedEntity current = selected.get(0);
						for(PlacedEntity e : entities) {
							if (e.visibleInEditor && e != current) {
								Vector3f originalPos = new Vector3f(current.position);
								current.position.add(diff.x,0,diff.z);
								Vector3f escapeAxis = current.getObb().checkForSnap(e.getObb());
								current.position.set(originalPos);
								
								if (escapeAxis != null) {
									float length;
									if (escapeAxis.equals(e.getObb().X)) {
										length = (e.getObb().bounds.x*e.scale + current.getObb().bounds.x*current.scale);
									} else if (escapeAxis.equals(e.getObb().Y)) {
										length = (e.getObb().bounds.y*e.scale + current.getObb().bounds.y*current.scale);
									} else {
										length = (e.getObb().bounds.z*e.scale + current.getObb().bounds.z*current.scale);
									}
									
									diff = Vector3f.add(e.position, Vector3f.mul(escapeAxis, length));
									diff.sub(originalPos);
									
									suggestedRotation.x = e.rotation.x;
									suggestedRotation.y = e.rotation.y;
									suggestedRotation.z = e.rotation.z;
								} else {
									suggestedRotation.zero();
								}
							}
						}
					}
					
					else if (Globals.objectPlacementCollision) {
						PlacedEntity current = selected.get(0);
						for(PlacedEntity e : entities) {
							if (e.visibleInEditor && e != current) {
								Vector3f originalPos = new Vector3f(current.position);
								current.position.add(diff.x,0,diff.z);
								Vector3f escape = current.getObb().intersection(e.getObb());
								current.position.set(originalPos);
								
								if (escape != null) {
									diff.x += escape.x;
									diff.y += escape.y;
									diff.z += escape.z;
									
								}
							}
						}
					}
				}
				
				if (Input.isDown(KeyEvent.VK_SHIFT)) {
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
			
			
			if (!Input.isDown(KeyEvent.VK_CONTROL)) {
				if (Input.isPressed(KeyEvent.VK_X)) {
					rotationAxis.set(1,0,0);
					beginRotating();
				}
				
				if (Input.isPressed(KeyEvent.VK_Y)) {
					rotationAxis.set(0,1,0);
					beginRotating();
				}
				
				if (Input.isPressed(KeyEvent.VK_Z)) {
					rotationAxis.set(0,0,1);
					beginRotating();
				}
			}
		}
		
		else if (scaling) {
			scale = Vector2f.distance(scalingAnchor, new Vector2f(Mouse.getX(), Mouse.getY()))/6f;
		}
	}

	public void selectEntity(Vector3f currentRay) {
		boolean ctrl = Input.isDown(KeyEvent.VK_CONTROL);
		
		float shortestDist = Float.POSITIVE_INFINITY;
		PlacedEntity entity = null;
		
		for(PlacedEntity e : entities) {
			if (!ctrl) {
				deselect(e);
			}
			
			if (e.visibleInEditor
			&&  MathUtils.entityBroadphaseIntersection(GLWindow.camera.getPosition(), currentRay, e)) {
				float dist = MathUtils.rayMeshIntersection(GLWindow.camera.getPosition(), currentRay, e.position, e.rotation, e.scale, e.getModel().getVertices());
				
				if (dist < shortestDist) {
					shortestDist = dist;
					entity = e;
				}
			}
		}
		
		if (entity != null) {
			entity.toggleSelect();
			if (entity.isSelected()) {
				select(entity);
			} else {
				deselect(entity);
			}
		}
	}
	
	public static void fastClone(Vector3f currentRay) {
		PlacedEntity placedEntity = getClicked(currentRay);
		
		if (placedEntity != null) {
			deselectAll();
			Entity entity = placedEntity.getEntity();
			
			if (entity != null) {
				placeEntity(entity);
			} else {
				ModelEntity me = placeEntity(placedEntity.model, placedEntity.texture);
				
				if (me != null) {
					me.rotation.set(placedEntity.rotation);
					me.getObb().setRotation(me.rotation);
					me.scale = placedEntity.scale;
					//me.position.set(placedEntity.position);
				}
			}
			
			beginTranslating(placedEntity.position);
		}
	}
	
	public static PlacedEntity getClicked(Vector3f currentRay) {
		float shortestDist = Float.POSITIVE_INFINITY;
		PlacedEntity entity = null;
		
		for(PlacedEntity e : entities) {
			if (e.visibleInEditor
			&&  MathUtils.entityBroadphaseIntersection(GLWindow.camera.getPosition(), currentRay, e)) {
				float dist = MathUtils.rayMeshIntersection(GLWindow.camera.getPosition(), currentRay, e.position, e.rotation, e.scale, e.getModel().getVertices());
				
				if (dist < shortestDist) {
					shortestDist = dist;
					entity = e;
				}
			}
		}
		
		return entity;
	}

	public static void deleteSelected() {
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
	
	public static void retextureSelected(TextureAsset texture) {
		for(PlacedEntity e : selected) {
			e.setTexture(texture);
		}
	}
	
	public static void retextureEntity(PlacedEntity e,TextureAsset texture) {
		e.setTexture(texture);
	}
	
	public void cut() {
		copied.clear();
		for(PlacedEntity e : selected) {
			copied.add(new PlacedEntity(e.position, e));
		}
		
		new DeleteSelectedCommand();
	}
	
	public static void paste() {
		if (copied.size() == 0) return; 
		
		Vector3f position = HeightmapControl.picker.getCurrentTerrainPoint();
		deselectAll();
		
		Vector3f origin = copied.get(0).position;
		for(PlacedEntity e : copied) {
			//position = Vector3f.add(e.position, Vector3f.mul(e.getObb().Z, e.getObb().bounds.z*2));
			PlacedEntity ne = new PlacedEntity(Vector3f.add(position, Vector3f.sub(e.position, origin)), e);
			ne.select();
			entities.add(ne);
			selected.add(ne);
		}
	}

	public void endTransforms() {
		for(PlacedEntity e : selected) {
			e.position.add(translation);
			if (suggestedRotation.isZero()) {
				e.rotation.add(rotation);
			} else {
				e.rotation.set(suggestedRotation);
			}
			e.getObb().setRotation(e.rotation);
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

	public static void beginTranslating(Vector3f anchor) {
		if (selected.size() == 0) return;
		rotating = false;
		scaling = false;
		translating = true;
		
		if (anchor == null) {
			translationAnchor.set(selected.get(0).position);
		} else {
			translationAnchor.set(anchor);
		}
		
		if (Globals.gridSnap) {
			translationAnchor.x = (float) (Math.floor(translationAnchor.x / Globals.gridSize)*Globals.gridSize);
			translationAnchor.y = (float) (Math.floor(translationAnchor.y / Globals.gridSize)*Globals.gridSize);
			translationAnchor.z = (float) (Math.floor(translationAnchor.z / Globals.gridSize)*Globals.gridSize);
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

	public synchronized void addEntity(PlacedEntity e) {
		entities.add(e);
	}

	@Override
	public void clear() {
		entities.clear();
		selected.clear();
		copied.clear();
	}

	public boolean isTransforming() {
		return (translating || rotating || scaling);
	}

	public void removeEntity(PlacedEntity e) {
		markedForDeletion.add(e);
	}
}
