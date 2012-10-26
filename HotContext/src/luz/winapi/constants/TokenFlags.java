package luz.winapi.constants;

import java.util.BitSet;

/*
 * http://msdn.microsoft.com/en-us/library/aa374905(v=VS.85%).aspx
 * http://www.pinvoke.net/default.aspx/advapi32/OpenProcessToken.html
 */
public class TokenFlags{

	private BitSet flags=new BitSet(20);
	
    public void setTOKEN_ASSIGN_PRIMARY         (boolean b) { flags.set( 0, b); }	//0x00000001
	public void setTOKEN_DUPLICATE              (boolean b) { flags.set( 1, b); }	//0x00000002
	public void setTOKEN_IMPERSONATE            (boolean b) { flags.set( 2, b); }	//0x00000004
	public void setTOKEN_QUERY                  (boolean b) { flags.set( 3, b); }	//0x00000008	
	public void setTOKEN_QUERY_SOURCE           (boolean b) { flags.set( 4, b); }	//0x00000010
	public void setTOKEN_ADJUST_PRIVILEGES      (boolean b) { flags.set( 5, b); }	//0x00000020
	public void setTOKEN_ADJUST_GROUPS          (boolean b) { flags.set( 6, b); }	//0x00000040
	public void setTOKEN_ADJUST_DEFAULT         (boolean b) { flags.set( 7, b); }	//0x00000080
	public void setTOKEN_ADJUST_SESSIONID       (boolean b) { flags.set( 8, b); }	//0x00000100
	
	public void setSTANDARD_RIGHTS_DELETE       (boolean b) { flags.set(16, b); }	//0x00010000
	public void setSTANDARD_RIGHTS_READ_CONTROL (boolean b) { flags.set(17, b); }	//0x00020000
	public void setSTANDARD_RIGHTS_WRITE_DAC    (boolean b) { flags.set(18, b); }	//0x00040000
	public void setSTANDARD_RIGHTS_WRITE_OWNER  (boolean b) { flags.set(19, b); }	//0x00080000
	
	public void setTOKEN_READ() {
		setTOKEN_ASSIGN_PRIMARY         (false);
		setTOKEN_DUPLICATE              (false);
		setTOKEN_IMPERSONATE            (false);
		setTOKEN_QUERY                  (true);
		setTOKEN_QUERY_SOURCE           (false);
		setTOKEN_ADJUST_PRIVILEGES      (false);
		setTOKEN_ADJUST_GROUPS          (false);
		setTOKEN_ADJUST_DEFAULT         (false);
		setTOKEN_ADJUST_SESSIONID       (false);
		setSTANDARD_RIGHTS_DELETE       (false);
		setSTANDARD_RIGHTS_READ_CONTROL (true);
		setSTANDARD_RIGHTS_WRITE_DAC    (false);
		setSTANDARD_RIGHTS_WRITE_OWNER  (false);
	}
	
	public void setTOKEN_ALL_ACCESS() {
		setTOKEN_ASSIGN_PRIMARY         (true);
		setTOKEN_DUPLICATE              (true);
		setTOKEN_IMPERSONATE            (true);
		setTOKEN_QUERY                  (true);
		setTOKEN_QUERY_SOURCE           (true);
		setTOKEN_ADJUST_PRIVILEGES      (true);
		setTOKEN_ADJUST_GROUPS          (true);
		setTOKEN_ADJUST_DEFAULT         (true);
		setTOKEN_ADJUST_SESSIONID       (true);
		setSTANDARD_RIGHTS_DELETE       (false);
		setSTANDARD_RIGHTS_READ_CONTROL (true);
		setSTANDARD_RIGHTS_WRITE_DAC    (false);
		setSTANDARD_RIGHTS_WRITE_OWNER  (false);
	}
	
	public void setSTANDARD_RIGHTS_REQUIRED() {	//0x000F0000
		setTOKEN_ASSIGN_PRIMARY         (false);
		setTOKEN_DUPLICATE              (false);
		setTOKEN_IMPERSONATE            (false);
		setTOKEN_QUERY                  (false);
		setTOKEN_QUERY_SOURCE           (false);
		setTOKEN_ADJUST_PRIVILEGES      (false);
		setTOKEN_ADJUST_GROUPS          (false);
		setTOKEN_ADJUST_DEFAULT         (false);
		setTOKEN_ADJUST_SESSIONID       (false);
		setSTANDARD_RIGHTS_DELETE       (true);
		setSTANDARD_RIGHTS_READ_CONTROL (true);
		setSTANDARD_RIGHTS_WRITE_DAC    (true);
		setSTANDARD_RIGHTS_WRITE_OWNER  (true);
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