package entity;

import org.joml.Vector3f;

import assets.Entity;
import assets.Model;
import assets.TextureAsset;

public class PlacedEntity {
	public Vector3f position = new Vector3f();
	public Vector3f rotation = new Vector3f();
	public float scale = 1f;
	
	private Entity entity;
	protected Model model;
	protected TextureAsset texture;
	public boolean visibleInEditor = true;

	private boolean select;
	
	
	public boolean isVisible = true;
	public String id = "";
	private String properties = "";
	
	public PlacedEntity(Entity entity) {
		this.entity = entity;
	}
	
	public PlacedEntity() {}

	public PlacedEntity(Vector3f position, PlacedEntity e) {
		setPosition(position);
		this.entity = e.entity;
		this.model = e.model;
		this.texture = e.texture;
		this.rotation = e.rotation;
		this.scale = e.scale;
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

	public void setModel(Model model) {
		this.model = model;
	}
	
	public void setTexture(TextureAsset texture) {
		this.texture = texture;
	}
}
