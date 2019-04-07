package console.cmd;

import assets.Model;
import assets.TextureAsset;
import console.Command;
import console.History;
import console.Memento;
import entity.EntityControl;

public class DeleteSelectedCommand extends Command {
	
	public DeleteSelectedCommand() {
		this.object = EntityControl.entities;
		
		History.insert(this);
	}
	
	public DeleteSelectedCommand(Model model, TextureAsset texture) {
		this.object = EntityControl.entities;
	}
	
	public void execute() {
		memento = new Memento(object);
		EntityControl.deleteSelected();
	}
	
	public void unExecute() {
		object = memento.getState();
	}
}
