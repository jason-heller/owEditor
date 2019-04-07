package assets;

import utils.EntryType;

public class Asset {

	public EntryType type;
	public String name;
	public String[] tags;
	
	public String toString() {
		return name;
	}
}
