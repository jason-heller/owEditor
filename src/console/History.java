package console;

import java.util.Stack;

public class History {
	private static Stack<Command> commands = new Stack<>();
	private static int actionPtr = -1;
	
	public static void insert(Command command) {
		deleteAhead();
		commands.push(command);
		command.execute();
		actionPtr++;
	}
	
	public static void undo() {
		if(actionPtr == 0)
	        return;
		
		commands.get(actionPtr).unExecute();
		Console.log("undo action");
		actionPtr--;
	}
	
	public static void redo() {
		if(actionPtr == commands.size() - 1)
	        return;
		
		actionPtr++;
	    commands.get(actionPtr).execute();
	}
	
	public static void clear() {
		commands.clear();
	}
	
	private static void deleteAhead() {
		if (commands.size() < 1)
			return;
		
		for(int i = commands.size() - 1; i > actionPtr; i--)
			commands.remove(i);
	}
}