package io.data;

public class DataElement {
	private Object type;
	private String name;
	
	public DataElement(String name, Object type) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getType() {
		return type;
	}
}
