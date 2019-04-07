package console.cmd;

import java.util.ArrayList;

import assets.Entity;
import assets.Model;
import assets.TextureAsset;
import console.Command;
import console.History;
import console.Memento;
import entity.EntityControl;
import entity.PlacedEntity;

public class PlaceEntityCommand extends Command {
	
	private Entity entity = null;
	private Model model;
	private TextureAsset texture;
	
	public PlaceEntityCommand(Entity entity) {
		this.object = EntityControl.entities;
		this.entity = entity;
		
		History.insert(this);
	}
	
	public PlaceEntityCommand(Model model, TextureAsset texture) {
		this.object = EntityControl.entities;
		this.model = model;
		this.texture = texture;
		
		History.insert(this);
	}
	
	@SuppressWarnings("unchecked")
	public void execute() {
		memento = new Memento(new ArrayList<PlacedEntity>((ArrayList<PlacedEntity>)object));
		if (entity == null) {
			EntityControl.placeEntity(model, texture);
		} else {
			EntityControl.placeEntity(entity);
		}
	}
	
	public void unExecute() {
		EntityControl.entities = (ArrayList<PlacedEntity>)memento.getState();
	}
}
