package luz.winapi.tools;

import java.util.LinkedList;
import java.util.List;

import luz.winapi.jna.Psapi;
import luz.winapi.jna.Psapi.LPMODULEINFO;
import luz.winapi.jna.Psapi.PPROCESS_MEMORY_COUNTERS;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public class PsapiTools {
	private static final PsapiTools  INSTANCE=new PsapiTools();	//Eager Creation
	private static Psapi psapi = Psapi.INSTANCE;

	private PsapiTools(){}
	
	public static PsapiTools getInstance(){
		return INSTANCE;
	}	

	
	public List<Integer> enumProcesses() throws Exception{
		List<Integer> list = new LinkedList<Integer>();
		
		int[] pProcessIds = new int[256];
		IntByReference pBytesReturned = new IntByReference(); 
		boolean success = psapi.EnumProcesses(pProcessIds, pProcessIds.length*Integer.SIZE/8, pBytesReturned); 
		if (!success){
			int err=Native.getLastError();
            throw new Exception("EnumProcesses failed. Error: "+err);
    	}
    	
		int size = (pBytesReturned.getValue()/(Integer.SIZE/8)); 
		for (int i=0;i<size;i++)
			list.add(pProcessIds[i]);
		
		return list;
	}
	
	public List<Pointer> EnumProcessModules(Pointer hProcess) throws Exception{
		List<Pointer> list = new LinkedList<Pointer>();
		
		Pointer[] lphModule = new Pointer[100*4];
		IntByReference lpcbNeededs= new IntByReference();
		boolean success = psapi.EnumProcessModules(hProcess, lphModule,lphModule.length, lpcbNeededs);
		if (!success){
			int err=Native.getLastError();
            throw new Exception("EnumProcessModules failed. Error: "+err);
    	}
		for (int i = 0; i < lpcbNeededs.getValue()/4; i++) {
			list.add(lphModule[i]);
		}
		
		return list;
	}
	
	public String GetModuleFileNameExA(Pointer hProcess,Pointer hModule){
		byte[] lpImageFileName= new byte[256];
		psapi.GetModuleFileNameExA(hProcess, hModule, lpImageFileName, 256);
		return Native.toString(lpImageFileName);
	}
	
    public String GetProcessImageFileNameA(Pointer hProcess){
		byte[] lpImageFileName= new byte[256];
		psapi.GetProcessImageFileNameA(hProcess, lpImageFileName, 256);
        return Native.toString(lpImageFileName);
    }
	
	public LPMODULEINFO GetModuleInformation(Pointer hProcess, Pointer hModule) throws Exception{
		LPMODULEINFO lpmodinfo = new LPMODULEINFO();
		
		boolean success = psapi.GetModuleInformation(hProcess, hModule, lpmodinfo, lpmodinfo.size());
		if (!success){
			int err=Native.getLastError();
            throw new Exception("GetModuleInformation failed. Error: "+err);
    	}
		return lpmodinfo;
	}

	public PPROCESS_MEMORY_COUNTERS GetProcessMemoryInfo(Pointer Process) throws Exception{
    	PPROCESS_MEMORY_COUNTERS ppsmemCounters = new PPROCESS_MEMORY_COUNTERS();
    	boolean success = psapi.GetProcessMemoryInfo(Process, ppsmemCounters, ppsmemCounters.size());
		if (!success){
			int err=Native.getLastError();
            throw new Exception("GetProcessMemoryInfo failed. Error: "+err);
    	}
		return ppsmemCounters;
    }
}
