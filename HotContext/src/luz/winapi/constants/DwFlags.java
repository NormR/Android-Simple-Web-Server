package luz.winapi.constants;

import java.util.BitSet;

/*
 * http://msdn.microsoft.com/en-us/library/ms682489(VS.85).aspx
 */
public class DwFlags{

	private BitSet flags=new BitSet(32);
	
	public void setTH32CS_SNAPHEAPLIST (boolean b) { flags.set( 0, b); }	//0x00000001
	public void setTH32CS_SNAPPROCESS  (boolean b) { flags.set( 1, b); }	//0x00000002
	public void setTH32CS_SNAPTHREAD   (boolean b) { flags.set( 2, b); }	//0x00000004
	public void setTH32CS_SNAPMODULE   (boolean b) { flags.set( 3, b); }	//0x00000008	
	public void setTH32CS_SNAPMODULE32 (boolean b) { flags.set( 4, b); }	//0x00000010
	public void setTH32CS_INHERIT      (boolean b) { flags.set(31, b); }	//0x80000000
	
	public void setTH32CS_SNAPALL() {	//0x0000000F
		setTH32CS_SNAPHEAPLIST (true);
		setTH32CS_SNAPPROCESS  (true);
		setTH32CS_SNAPTHREAD   (true);
		setTH32CS_SNAPMODULE   (true);
		setTH32CS_SNAPMODULE32 (false);
		setTH32CS_INHERIT      (false);
	}
	
	public int getFlags(){
		int value=0;
		int max=flags.length();
		for (int i = 0; i < max; i++) {
			if(flags.get(i))
				value|=(1<<i);
		}
		return value;
	}
}