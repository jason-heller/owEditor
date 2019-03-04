package io.data;

import java.util.HashMap;
import java.util.Map;

public class DataChunk {
	public DataFormat format;
	public Map<String, String> data = new HashMap<String, String>();
	
	public DataChunk(DataFormat format) {
		for(DataElement element : format.getElements()) {
			data.put(element.getName(), "");
		}
		
		this.format = format;
	}
	
	public DataChunk(DataFormat format, String ... values) {
		this.format = format;
		if (format.getElements().length > values.length) {
			System.err.println("Too few values passed to "+this);
		}
		else if (format.getElements().length < values.length) {
			System.err.println("Too many values passed to "+this);
		}
		
		int i = 0;
		for(DataElement element : format.getElements()) {
			data.put(element.getName(), values[i++]);
		}
	}
	
	public DataChunk(String[] values) {
		for(String value : values) {
			String[] d = value.split("=");
			data.put(d[0], d[1]);
		}
	}
	
	public void put(String name, String value) {
		data.put(name, value);
	}
}
