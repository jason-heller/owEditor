package io.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class DataFile {
	private DataChunk[] chunks;
	private String location, name;
	
	public DataFile(File file, DataChunk[] chunks) {
		this.chunks = chunks;
		this.location = file.getParent();
		this.name = file.getName();
	}
	
	public DataChunk[] getChunks() {
		return chunks;
	}
	
	public String getLocation() {
		return location;
	}

	public void save() throws IOException {
		int dotInd = name.lastIndexOf('.');
		String out = location + "/" + name.substring(0, dotInd==-1 ? name.length() : dotInd) + ".txt";
		
		PrintWriter writer = new PrintWriter(out, "UTF-8");
		for(DataChunk chunk : chunks) {
			writer.println(chunk.format.getName());
			writer.println("{");
			Map<String, String> data = chunk.data;
			for(String key : data.keySet()) {
				writer.println("\t" + key + " = \"" + data.get(key) + "\"");
			}
			writer.println("}");
		}
		writer.close();
	}
}
