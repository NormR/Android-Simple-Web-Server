package luz.winapi.tools;

import luz.winapi.jna.Shell32;

import com.sun.jna.Pointer;

public class Shell32Tools {
	private static Shell32Tools INSTANCE=new Shell32Tools();	//Eager Creation
	private static Shell32 s32 = Shell32.INSTANCE;
	
	private Shell32Tools(){}
	
	public static Shell32Tools getInstance(){
		return INSTANCE;
	}
	
	////////////////////////////////////////////////////////////////////////
	
	public Pointer ExtractSmallIcon(String lpszFile, int nIconIndex){
		Pointer[] hIcons=new Pointer[1];
        s32.ExtractIconExA(lpszFile, 0, null, hIcons, nIconIndex);
        return hIcons[0];
	}
	
	

	boolean DestroyIcon(Pointer hIcon){
		return s32.DestroyIcon(hIcon);
	}
    
}
