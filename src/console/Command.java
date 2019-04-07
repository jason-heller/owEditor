package console;

public abstract class Command {
	public Object object;
	public Memento memento;
	
	public abstract void execute();
	public abstract void unExecute();
}
