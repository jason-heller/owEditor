package console;

import javax.swing.JTextArea;

public class Console {
	private static JTextArea log;

	public static void clear() {
		log.setText("");
	}

	public static void log(String s) {
		log.append(s + "\n");
	}

	public static void send(String string) {
		final String[] strs = string.split(" ");
		if (strs == null || strs.length == 0) {
			return;
		}

		final String command = strs[0];
		final String[] args = new String[strs.length - 1];
		if (strs.length > 1) {
			for (int i = 1; i < strs.length; i++) {
				args[i - 1] = strs[i];
			}
		}

		// Now check if you typed a command
		final Command cmd = Commands.getCommand(command);
		if (cmd != null) {
			cmd.execute(args);
			return;
		}

		log("No such command: " + command);
	}

	public static void setLog(JTextArea textArea) {
		log = textArea;
	}
}
