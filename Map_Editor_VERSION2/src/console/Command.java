package console;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import application.Application;

class Command {
	public static String getVariable(Class<?> A, String name) {
		try {
			try {
				return A.getField(name).get(null).toString();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final NoSuchFieldException e) {
			Console.log("Console error: No such var " + A.toString() + "." + name);
		} catch (final SecurityException e) {
			e.printStackTrace();
		}

		return "ERR";
	}

	public static String invokeMethod(Class<?> A, String methodName, Object... args) {
		try {
			Method m = null;
			if (args.length > 0) {
				m = A.getMethod(methodName, String.class);
			} else {
				m = A.getMethod(methodName);
			}
			final Object s = m.invoke(null, args);
			if (s == null) {
				return null;
			} else {
				return s.toString();
			}
		} catch (NoSuchMethodException | SecurityException e) {
			Console.log("Console error: No such method " + A.toString() + "." + methodName);
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String invokeMethod(Object o, String methodName, Object... args) {
		try {
			final Method m = o.getClass().getMethod(methodName);
			final Object s = m.invoke(o, args);
			if (s == null) {
				return null;
			} else {
				return s.toString();
			}
		} catch (NoSuchMethodException | SecurityException e) {
			Console.log("Console error: No such method " + o.getClass().toString() + "." + methodName);
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String setVariable(Class<?> A, String name, String value) {
		try {
			try {
				final Field field = A.getField(name);

				if (field.getType().isAssignableFrom(Float.TYPE)) {
					field.set(null, Float.parseFloat(value));
				}
				if (field.getType().isAssignableFrom(Boolean.TYPE)) {
					field.set(null, Boolean.parseBoolean(value));
				}
				if (field.getType().isAssignableFrom(String.class)) {
					field.set(null, value);
				}
				if (field.getType().isAssignableFrom(Integer.TYPE)) {
					field.set(null, Integer.parseInt(value));
				}

				return A.getField(name).get(null).toString();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final NoSuchFieldException e) {
			Console.log("Console error: No such var " + A.toString() + "." + name);
		} catch (final SecurityException e) {
			e.printStackTrace();
		}

		return "ERR";
	}

	private final String var_name;
	private final String var_value;

	private Class<?> var_class = null;

	private Object var_object = null;

	public boolean requiresCheats = false;

	private CommandType cmdType = CommandType.ACTION;

	public Command(String name, CommandType action) {
		this(name, "", null, action, false);
	}

	public Command(String name, String value) {
		this(name, value, null, CommandType.ACTION, false);
	}

	public Command(String name, String value, Class<?> object, CommandType cmdType, boolean requiresCheats) {
		Commands.vars.add(this);

		this.var_name = name;
		this.requiresCheats = requiresCheats;
		this.var_class = object;
		this.cmdType = cmdType;
		this.var_value = value;
	}

	public Command(String name, String value, Object object, CommandType cmdType, boolean requiresCheats) {
		Commands.vars.add(this);

		this.var_name = name;
		this.requiresCheats = requiresCheats;
		this.var_object = object;
		this.cmdType = cmdType;
		this.var_value = value;
	}

	@SuppressWarnings("all")
	public void execute(String[] args) {
		if (var_class == Console.class) {
			var_object = null;
		}

		if (cmdType == CommandType.GETTER) {
			Console.log(getVariable(var_class, getValue()));
		}

		if (cmdType == CommandType.SETTER) {
			setVariable(var_class, getValue(), args[0]);
		}

		if (cmdType == CommandType.METHOD) {
			String output = null;

			if (var_class != null && var_class != Console.class) {
				output = invokeMethod(var_class, getValue(), args);
			} else {
				output = invokeMethod(var_object, getValue(), args);
			}

			if (output != null) {
				Console.log(output);
			}
		}

		if (var_name.equals("quit") || var_name.equals("exit")) {
			Application.close();
		}

	}

	public String getName() {
		return this.var_name;
	}

	public String getValue() {
		return this.var_value;
	}
}
