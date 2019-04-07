package console.cmd;

import assets.TextureAsset;
import console.Command;
import console.History;
import console.Memento;
import entity.EntityControl;
import entity.PlacedEntity;

public class RetextureEntityCommand extends Command {
	
	private PlacedEntity entity;
	private TextureAsset texture;
	
	public RetextureEntityCommand(PlacedEntity entity, TextureAsset texture) {
		this.object = entity;
		this.entity = entity;
		this.texture = texture;
		
		History.insert(this);
	}
	
	public void execute() {
		memento = new Memento(object);
		EntityControl.retextureEntity(entity, texture);
	}
	
	public void unExecute() {
		object = memento.getState();
	}
}
