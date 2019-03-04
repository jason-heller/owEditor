package overhead;

import java.awt.Color;

public class OverheadItem {
	private String name;
	private Color color;
	
	public OverheadItem(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String toString() {
		return name;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
