package luz.winapi.api;

import com.sun.jna.Memory;

public interface MemoryListener {
	
	/**
	 * To optimize memory reading, the reading algorithm divides large memory areas into
	 * smaller ones. This value forces the reading algorithm to overlap these memory areas by
	 * the given value. This is usefull if the seach pattern is longer than one Byte. In this case, 
	 * the MemoryListener has only to worry about one area at each time. Values at the end of an 
	 * area will repeat at the beginning of the next area. In general this value should be 
	 * 'size of the search pattern -1'.
	 * @param overlapping
	 */
	
	public void init(Object value);
	public int getOverlapping();	
	public void mem(Memory outputBuffer, long address, long size);
	public Object getResults();


}
