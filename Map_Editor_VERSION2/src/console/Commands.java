package console;

import java.util.ArrayList;

@SuppressWarnings("unused")
class Commands {
	protected static ArrayList<Command> vars = new ArrayList<Command>();

	private static final Command quit = new Command("quit", CommandType.ACTION);
	private static final Command exit = new Command("exit", CommandType.ACTION);

	public static Command getCommand(String name) {
		for (int i = 0; i < vars.size(); i++) {
			final Command c = vars.get(i);
			if (c.getName().equals(name)) {
				return c;
			}
		}

		return null;
	}
}
