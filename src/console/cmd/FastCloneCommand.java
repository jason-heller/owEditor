package console.cmd;

import org.joml.Vector3f;

import assets.Model;
import assets.TextureAsset;
import console.Command;
import console.History;
import console.Memento;
import entity.EntityControl;

public class FastCloneCommand extends Command {
	
	private Vector3f ray;
	
	public FastCloneCommand(Vector3f ray) {
		this.object = EntityControl.entities;
		this.ray = ray;
		
		History.insert(this);
	}
	
	public FastCloneCommand(Model model, TextureAsset texture) {
		this.object = EntityControl.entities;
	}
	
	public void execute() {
		memento = new Memento(object);
		EntityControl.fastClone(ray);
	}
	
	public void unExecute() {
		object = memento.getState();
	}
}
