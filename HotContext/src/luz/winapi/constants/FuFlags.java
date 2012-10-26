package luz.winapi.constants;

import java.util.BitSet;

/*
 * http://msdn.microsoft.com/en-us/library/ms644952(VS.85).aspx
 */
public class FuFlags{

	private BitSet flags=new BitSet(6);

	public void setSMTO_BLOCK              (boolean b) { flags.set( 0, b); }	//0x00000001
	public void setSMTO_ABORTIFHUNG        (boolean b) { flags.set( 1, b); }	//0x00000002
	public void setSMTO_NOTIMEOUTIFNOTHUNG (boolean b) { flags.set( 3, b); }	//0x00000008	
	public void setSMTO_ERRORONEXIT        (boolean b) { flags.set( 5, b); }	//0x00000020
	
	public void setSMTO_NORMAL(){	//0x00000000
		setSMTO_BLOCK              (false);
		setSMTO_ABORTIFHUNG        (false);
		setSMTO_NOTIMEOUTIFNOTHUNG (false);
		setSMTO_ERRORONEXIT        (false);
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