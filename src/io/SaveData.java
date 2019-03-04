package io;

import java.util.ArrayList;
import java.util.List;

import io.data.DataChunk;
import io.format.MapFile;

public abstract class SaveData {
	protected List<DataChunk> saveData = new ArrayList<DataChunk>();
	
	public SaveData() {
		MapFile.data.add(this);
	}
	
	public void addDataChunk(DataChunk dataChunk) {
		saveData.add(dataChunk);
	}
	
	/** createDataChunks()
	 * Tells the class to store it's save data into data chunks
	 * 
	 * @return true if the function created save data (Not necessarily failed to do so)
	 */
	public boolean createDataChunks() {
		return false;
	}
	
	public List<DataChunk> getSaveData() {
		return saveData;
	}
	
	public abstract void clear();
	
	public void flush() {
		saveData.clear();
	}
}
