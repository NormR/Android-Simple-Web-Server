package luz.winapi.tools;

import java.util.LinkedList;
import java.util.List;

import luz.winapi.api.exception.Kernel32Exception;
import luz.winapi.constants.DwDesiredAccess;
import luz.winapi.constants.DwFlags;
import luz.winapi.jna.Kernel32;
import luz.winapi.jna.Kernel32.ENUMRESNAMEPROC;
import luz.winapi.jna.Kernel32.LPPROCESSENTRY32;
import luz.winapi.jna.Kernel32.LPSYSTEM_INFO;
import luz.winapi.jna.Kernel32.MEMORY_BASIC_INFORMATION;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class Kernel32Tools {
	private static final Kernel32Tools INSTANCE=new Kernel32Tools();	//Eager Creation
	private static Kernel32 k32 = Kernel32.INSTANCE;
	
	private Kernel32Tools(){}
	
	public static Kernel32Tools getInstance(){
		return INSTANCE;
	}
	
	public int GetCurrentProcessId(){
		return k32.GetCurrentProcessId();
	}
	
	public Pointer GetCurrentProcess(){
		return k32.GetCurrentProcess();
	}
	
	
	public int GetLastError(){
		return k32.GetLastError();
	}
	
	public Pointer OpenProcess(DwDesiredAccess dwDesiredAccess, boolean bInheritHandle, int dwProcessId) throws Kernel32Exception{
		Pointer process = k32.OpenProcess(dwDesiredAccess.getFlags(), false, dwProcessId);
    	if (process == null){
    		int err=k32.GetLastError();
            throw new Kernel32Exception("openProcess failed. Error: "+err);
    	}
        return process;
    }
	
	public void ReadProcessMemory(Pointer hProcess, Pointer pAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead) throws Kernel32Exception{
        boolean success = k32.ReadProcessMemory(hProcess, pAddress, outputBuffer, nSize, outNumberOfBytesRead);
    	if (!success){
    		int err=k32.GetLastError();
    		throw new Kernel32Exception("readProcessMemory failed. Error: "+err);
    	}
    }
	
	public void WriteProcessMemory(Pointer hProcess, Pointer pAddress, Pointer inputBuffer, int nSize, IntByReference outNumberOfBytesWritten) throws Kernel32Exception{
        boolean success = k32.WriteProcessMemory(hProcess, pAddress, inputBuffer, nSize, outNumberOfBytesWritten);
    	if (!success){
    		int err=k32.GetLastError();
    		throw new Kernel32Exception("writeProcessMemory failed. Error: "+err);
    	}
    }
	
	public List<LPPROCESSENTRY32> getProcessList() throws Exception{
		List<LPPROCESSENTRY32> list = new LinkedList<LPPROCESSENTRY32>();
		
		DwFlags dwFlags=new DwFlags();
		dwFlags.setTH32CS_SNAPPROCESS(true);
        Pointer hProcessSnap = k32.CreateToolhelp32Snapshot(dwFlags.getFlags(), 0);
        
        LPPROCESSENTRY32 pe32 = new LPPROCESSENTRY32();
        boolean success = k32.Process32First(hProcessSnap, pe32);
    	if (!success){
    		int err=k32.GetLastError();
    		throw new Exception("Process32First failed. Error: "+err);
    	}
       
        do{
        	if (pe32.th32ProcessID!=0){
	        	list.add(pe32);
        	}
        	pe32 = new LPPROCESSENTRY32();
        }while(k32.Process32Next(hProcessSnap, pe32));
		return list;		
	}
    
    
    
    public Pointer FindResource(Pointer hModule, String lpName, String lpType){
    	return k32.FindResourceA(hModule, lpName, lpType);
    }
    
    public Pointer LockResource(Pointer hResData){
        return k32.LockResource(hResData);
    }

    public int SizeofResource(Pointer hModule,Pointer hResInfo) throws Exception{
        int size = k32.SizeofResource(hModule,hResInfo);
    	if (size==0){
    		int err=k32.GetLastError();
    		throw new Exception("SizeofResource failed. Error: "+err);
    	}
    	return size;
    }
    
    public boolean EnumResourceNamesA(Pointer hModule,String lpszType, ENUMRESNAMEPROC lpEnumFunc, IntByReference lParam){
        return k32.EnumResourceNamesA(hModule, lpszType, lpEnumFunc, lParam);
    }
    
    private LPSYSTEM_INFO lpSystemInfoCache=null;
    public LPSYSTEM_INFO GetSystemInfo(){
    	if (lpSystemInfoCache!=null)
    		return lpSystemInfoCache;
    	lpSystemInfoCache=new LPSYSTEM_INFO();
    	k32.GetSystemInfo(lpSystemInfoCache);
    	return lpSystemInfoCache;
    }
    
    
    public static int MEM_COMMIT =0x01000;
    public static int MEM_RESERVE=0x02000;
    public static int MEM_FREE   =0x10000;
    
    
    
    public static int PAGE_NOACCESS         =0x0001;	//000 0000 0001
    public static int PAGE_READONLY         =0x0002;	//000 0000 0010
    public static int PAGE_READWRITE        =0x0004;	//000 0000 0100
    public static int PAGE_WRITECOPY        =0x0008;	//000 0000 1000   
    public static int PAGE_EXECUTE          =0x0010;	//000 0001 0000
    public static int PAGE_EXECUTE_READ     =0x0020;	//000 0010 0000
    public static int PAGE_EXECUTE_READWRITE=0x0040;	//000 0100 0000
    public static int PAGE_EXECUTE_WRITECOPY=0x0080;	//000 1000 0000
    public static int PAGE_GUARD            =0x0100;	//001 0000 0000
    public static int PAGE_NOCACHE          =0x0200;	//010 0000 0000
    public static int PAGE_WRITECOMBINE     =0x0400;	//100 0000 0000

    
    public MEMORY_BASIC_INFORMATION VirtualQueryEx(Pointer hProcess,Pointer lpAddress) throws Kernel32Exception{
    	MEMORY_BASIC_INFORMATION lpBuffer = new MEMORY_BASIC_INFORMATION();
        int ret = k32.VirtualQueryEx(hProcess, lpAddress, lpBuffer, lpBuffer.size());
        if (ret==0){
    		int err=k32.GetLastError();
    		throw new Kernel32Exception("VirtualQueryEx failed. Error: "+err);
    	}
        return lpBuffer;
    }
}
