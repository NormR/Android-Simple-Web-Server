package luz.winapi.tools;

import luz.winapi.constants.TokenFlags;
import luz.winapi.jna.Advapi32;
import luz.winapi.jna.Kernel32;
import luz.winapi.jna.Advapi32.LUID;
import luz.winapi.jna.Advapi32.TOKEN_PRIVILEGES;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class Advapi32Tools {
	private static final Advapi32Tools INSTANCE=new Advapi32Tools();	//Eager Creation
	private static Advapi32 a32 = Advapi32.INSTANCE;
	private static Kernel32 k32 = Kernel32.INSTANCE;
	
	private Advapi32Tools(){}
	
	public static Advapi32Tools getInstance(){
		return INSTANCE;
	}
	
	////////////////////////////////////////////////////////////////////////
	
	public static final String SE_DEBUG_NAME = "SeDebugPrivilege";
	
	////////////////////////////////////////////////////////////////////////
	
	public static final int SE_PRIVILEGE_ENABLED = 2;
	
	////////////////////////////////////////////////////////////////////////
	
	public void enableDebugPrivilege(Pointer hProcess) throws Exception{
        PointerByReference hToken = new PointerByReference();
        TokenFlags tokenFlags=new TokenFlags();
        tokenFlags.setTOKEN_QUERY(true);
        tokenFlags.setTOKEN_ADJUST_PRIVILEGES(true);
        boolean success = a32.OpenProcessToken(hProcess, tokenFlags.getFlags(), hToken);
    	if (!success){
    		int err=Native.getLastError();
            throw new Exception("OpenProcessToken failed. Error: "+err);
    	}
        
        LUID luid = new LUID();
        success = a32.LookupPrivilegeValueA(null, SE_DEBUG_NAME, luid);
    	if (!success){
    		int err=Native.getLastError();
            throw new Exception("LookupPrivilegeValueA failed. Error: "+err);
    	
    	}
        
        TOKEN_PRIVILEGES tkp = new TOKEN_PRIVILEGES(1);
        tkp.Privileges[0].Luid=luid;
        tkp.Privileges[0].Attributes=SE_PRIVILEGE_ENABLED;
        success = a32.AdjustTokenPrivileges(hToken.getValue(), false, tkp, 0, null, null);
    	if (!success){
    		int err=Native.getLastError();
            throw new Exception("AdjustTokenPrivileges failed. Error: "+err);
    	}
    	
    	k32.CloseHandle(hToken.getValue());
	}
    
}
