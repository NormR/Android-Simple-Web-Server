package luz.winapi.constants;

import java.util.BitSet;

/*
 * http://msdn.microsoft.com/en-us/library/ms684880(v=VS.85).aspx
 */
public class DwDesiredAccess{

	private BitSet flags=new BitSet(21);
	
	public void setPROCESS_TERMINATE                (boolean b) { flags.set( 0, b); }	//0x00000001
	public void setPROCESS_CREATE_THREAD            (boolean b) { flags.set( 1, b); }	//0x00000002
	public void setPROCESS_VM_OPERATION             (boolean b) { flags.set( 3, b); }	//0x00000008
	public void setPROCESS_VM_READ                  (boolean b) { flags.set( 4, b); }	//0x00000010
	public void setPROCESS_VM_WRITE                 (boolean b) { flags.set( 5, b); }	//0x00000020
	public void setPROCESS_DUP_HANDLE               (boolean b) { flags.set( 6, b); }	//0x00000040
	public void setPROCESS_CREATE_PROCESS           (boolean b) { flags.set( 7, b); }	//0x00000080
	public void setPROCESS_SET_QUOTA                (boolean b) { flags.set( 8, b); }	//0x00000100
	public void setPROCESS_SET_INFORMATION          (boolean b) { flags.set( 9, b); }	//0x00000200
	public void setPROCESS_QUERY_INFORMATION        (boolean b) { flags.set(10, b); }	//0x00000400
	public void setPROCESS_SUSPEND_RESUME           (boolean b) { flags.set(11, b); }	//0x00000800
	public void setPROCESS_QUERY_LIMITED_INFORMATION(boolean b) { flags.set(12, b); }	//0x00001000
	
	public void setDELETE                           (boolean b) { flags.set(16, b); }	//0x00010000
	public void setREAD_CONTROL                     (boolean b) { flags.set(17, b); }	//0x00020000
	public void setWRITE_DAC                        (boolean b) { flags.set(18, b); }	//0x00040000
	public void setWRITE_OWNER                      (boolean b) { flags.set(19, b); }	//0x00080000
	
	public void setSYNCHRONIZE                      (boolean b) { flags.set(20, b); }	//0x00100000
	
	public void setPROCESS_ALL_ACCESS(){	//0x001F0FFF
		setPROCESS_TERMINATE                (true);
		setPROCESS_CREATE_THREAD            (true);
		setPROCESS_VM_OPERATION             (true);
		setPROCESS_VM_READ                  (true);
		setPROCESS_VM_WRITE                 (true);
		setPROCESS_DUP_HANDLE               (true);
		setPROCESS_CREATE_PROCESS           (true);
		setPROCESS_SET_QUOTA                (true);
		setPROCESS_SET_INFORMATION          (true);
		setPROCESS_QUERY_INFORMATION        (true);
		setPROCESS_SUSPEND_RESUME           (true);
		setPROCESS_QUERY_LIMITED_INFORMATION(false);
		setDELETE                           (true);
		setREAD_CONTROL                     (true);
		setWRITE_DAC                        (true);
		setWRITE_OWNER                      (true);
		setSYNCHRONIZE                      (true);
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