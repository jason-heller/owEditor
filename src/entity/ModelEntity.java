package entity;

import org.joml.Vector3f;

import assets.Model;
import assets.TextureAsset;

public class ModelEntity extends PlacedEntity {
	
	public ModelEntity(Model model, TextureAsset texture) {
		super();
		this.model = model;
		this.texture = texture;
		obb = new OBB(this, Vector3f.sub(model.getMax(), model.getMin()).div(2f));
		obb.setRotation(rotation);
	}
	
	public void setTexture(TextureAsset texture) {
		this.texture = texture;
	}
}
