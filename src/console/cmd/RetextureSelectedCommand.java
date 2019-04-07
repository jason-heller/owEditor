package console.cmd;

import assets.TextureAsset;
import console.Command;
import console.History;
import console.Memento;
import entity.EntityControl;

public class RetextureSelectedCommand extends Command {
	
	private TextureAsset texture;
	
	public RetextureSelectedCommand(TextureAsset texture) {
		this.object = EntityControl.entities;
		this.texture = texture;
		
		History.insert(this);
	}
	
	public void execute() {
		memento = new Memento(object);
		EntityControl.retextureSelected(texture);
	}
	
	public void unExecute() {
		object = memento.getState();
	}
}
