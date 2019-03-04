package entity;

import org.joml.Vector3f;

import assets.Model;
import assets.TextureAsset;

public class ModelEntity extends PlacedEntity {
	
	public ModelEntity(Model model, TextureAsset texture) {
		super();
		this.model = model;
		this.texture = texture;
	}
	
	public void setTexture(TextureAsset texture) {
		this.texture = texture;
	}
}
