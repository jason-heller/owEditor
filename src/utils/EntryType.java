package utils;

import java.awt.Color;

import application.Globals;

public enum EntryType {
	DEFAULT("", Color.BLACK), UNLINKED_ASSET("unlinked", Color.DARK_GRAY), INVISIBLE_OBJECT("invisible", Color.ORANGE),
	CONTROL("control", Color.MAGENTA), DEVELOPER("dev", Globals.COLOR_DEV), UTILITY("utility", Globals.COLOR_DONT_USE);

	private Color color;
	private String name;

	EntryType(String name, Color color) {
		this.color = color;
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	private String getName() {
		return name;
	}

	public static EntryType get(String name) {
		for (EntryType e : EntryType.values()) {
			if (e.getName().equals(name)) {
				return e;
			}
		}

		return DEFAULT;
	}
}
