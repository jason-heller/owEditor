package utils;

import java.awt.Color;

public enum EntryType {
	DEFAULT(Color.BLACK), LINKED_TO_ANOTHER_ASSET(Color.PINK), NONVISIBLE_OBJECT(Color.ORANGE), DO_NOT_USE(Color.RED), NATURE(Color.GREEN), INDUSTRIAL(Color.BLUE);
	
	private Color color;
	
	EntryType(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
