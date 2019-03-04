package io;

import static io.data.DataFormat.FORMAT_UNKNOWN;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.data.DataChunk;
import io.data.DataFile;
import io.data.DataFormat;

public class DataFileReader {
	public static DataFile read(File file) {
		
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			List<DataChunk> chunks = new ArrayList<DataChunk>();
			
			DataFormat format = null;
			String id = "";
			String[] values = null;
			
			for(int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				
				if (line.contains("{")) {
					id = lines.get(i-1).replaceAll(" ", "").replaceAll("\t", "");
					format = DataFormat.getFormat(id);
					
					if (format == FORMAT_UNKNOWN) {
						// Error
						System.err.println("ERR: Malformed profile file, line="+id);
						throw new IOException();
					}
					
					values = new String[format.getElements().length];
				}
				
				else if (line.contains("}")) {
					DataChunk chunk;
					chunk = new DataChunk(format, values);
					chunks.add(chunk);
					format = null;
				}
				
				else if (format != null) {
					// Read
					String[] data = line.split("=");
					String name  = data[0].replaceAll(" ", "").replaceAll("\t", "");
					String value = data[1].substring(data[1].indexOf("\"")+1, data[1].lastIndexOf("\""));
					
					int ind = format.getElementIndex(name);
					
					if (ind != -1) {
						values[ind] = value;
					} else {
						System.err.println("Unknown data element: "+name);
					}
					
				}
			}
			
			return new DataFile(file, chunks.toArray(new DataChunk[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
}
