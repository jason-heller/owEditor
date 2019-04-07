package entity;

import org.joml.Vector3f;

import application.Profile;
import assets.Entity;
import assets.Model;
import assets.TextureAsset;

public class PlacedEntity {
	public Vector3f position = new Vector3f();
	public Vector3f rotation = new Vector3f(0,0,0);
	public float scale = 1f;
	
	private Entity entity;
	protected Model model;
	protected TextureAsset texture;
	public boolean visibleInEditor = true;
	protected OBB obb;

	private boolean select;
	
	
	public boolean isVisible = true;
	public String id = "";
	private String properties = "";
	
	public PlacedEntity(Entity entity) {
		this.entity = entity;
		this.model = Profile.getModel(entity.model);
		this.texture = Profile.getTexture(entity.texture);
		obb = new OBB(this, Vector3f.sub(model.getMax(), model.getMin()).div(2f));
		obb.setRotation(rotation);
	}
	
	public PlacedEntity() {}

	public PlacedEntity(Vector3f position, PlacedEntity e) {
		setPosition(position);
		this.entity = e.entity;
		this.model = e.model;
		this.texture = e.texture;
		this.rotation.set(e.rotation);
		this.scale = e.scale;
		obb = new OBB(this, Vector3f.sub(model.getMax(), model.getMin()).div(2f));
		obb.setRotation(e.rotation);
		System.out.println(e.rotation);
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
	}
	
	public Model getModel() {
		return model;
	}
	
	public TextureAsset getTexture() {
		return texture;
	}

	public void select() {
		select = true;
	}
	
	public void deselect() {
		select = false;
	}
	
	public void toggleSelect() {
		select = !select;
	}
	
	public boolean isSelected() {
		return select;
	}

	public Entity getEntity() {
		return entity;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}
	
	public OBB getObb() {
		return obb;
	}

	public void setModel(Model model) {
		this.model = model;
		if (obb == null) {
			obb = new OBB(this, Vector3f.sub(model.getMax(), model.getMin()).div(2f));
		}
	}
	
	public void setTexture(TextureAsset texture) {
		this.texture = texture;
	}
}
