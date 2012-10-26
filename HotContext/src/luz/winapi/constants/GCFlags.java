package luz.winapi.constants;

public enum GCFlags{
	GCL_HICON  (-14),
	GCL_HICONSM(-34);
	
//GCW_ATOM
//GCL_CBCLSEXTRA
//GCL_CBWNDEXTRA
//GCL_HBRBACKGROUND
//GCL_HCURSOR
//GCL_HMODULE
//GCL_MENUNAME
//GCL_STYLE
//GCL_WNDPROC
	
	private int value;
	GCFlags(int value)     { this.value=value; }
	public int getValue() { return value;     }
}