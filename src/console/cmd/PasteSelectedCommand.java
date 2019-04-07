package console.cmd;

import assets.Model;
import assets.TextureAsset;
import console.Command;
import console.History;
import console.Memento;
import entity.EntityControl;

public class PasteSelectedCommand extends Command {
	
	public PasteSelectedCommand() {
		this.object = EntityControl.entities;
		
		History.insert(this);
	}
	
	public PasteSelectedCommand(Model model, TextureAsset texture) {
		this.object = EntityControl.entities;
	}
	
	public void execute() {
		memento = new Memento(object);
		EntityControl.paste();
	}
	
	public void unExecute() {
		object = memento.getState();
	}
}
