package console;

public class Memento {
	private Object object;
	
	public Memento(Object object) {
		this.object = object;
	}

	public Object getState() {
		return object;
	}
	
	public void setState(Object ... object) {
		this.object = object;
	}
}
