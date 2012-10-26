package luz.winapi.api;

import java.awt.image.BufferedImage;
import java.util.List;

import luz.winapi.api.exception.Kernel32Exception;
import luz.winapi.constants.DwDesiredAccess;
import luz.winapi.constants.GAFlags;
import luz.winapi.constants.ProcessInformationClass;
import luz.winapi.jna.Kernel32.LPPROCESSENTRY32;
import luz.winapi.jna.Kernel32.MEMORY_BASIC_INFORMATION;
import luz.winapi.jna.Ntdll.PROCESS_BASIC_INFORMATION;
import luz.winapi.jna.Psapi.LPMODULEINFO;
import luz.winapi.jna.Psapi.PPROCESS_MEMORY_COUNTERS;
import luz.winapi.tools.Kernel32Tools;
import luz.winapi.tools.NtdllTools;
import luz.winapi.tools.PsapiTools;
import luz.winapi.tools.Shell32Tools;
import luz.winapi.tools.User32Tools;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class WinAPIImpl implements WinAPI{
	private static WinAPIImpl INSTANCE=null;
	
	private Kernel32Tools k32   = Kernel32Tools.getInstance();
	private PsapiTools    psapi = PsapiTools   .getInstance();
	private Shell32Tools  s32   = Shell32Tools .getInstance();
	private NtdllTools    nt    = NtdllTools   .getInstance();
	private User32Tools   u32   = User32Tools  .getInstance();
	
	private WinAPIImpl(){}
	
	public static WinAPI getInstance(){
		if (INSTANCE==null)
			INSTANCE=new WinAPIImpl();
		return INSTANCE;
	}
	
	/**
	 * Return an new process list, and links the windows (hWnds) to the processes.
	 * @return
	 */
	public ProcessList getProcessList() {
		ProcessList plist = new ProcessList();	
		
		try {
			List<LPPROCESSENTRY32>  processes = k32.getProcessList();
			for (LPPROCESSENTRY32 pe32 : processes)
				plist.add(new ProcessImpl(pe32, this));
	
			List<Pointer> hWnds = u32.EnumWindows();
			IntByReference lpdwProcessId=new IntByReference();
			int pid=0;
			for (Pointer hWnd : hWnds) {
				u32.GetWindowThreadProcessId(hWnd,lpdwProcessId);
				pid=lpdwProcessId.getValue();
				plist.add(pid, hWnd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return plist;
	}
	
	public Pointer OpenProcess(DwDesiredAccess dwDesiredAccess, boolean bInheritHandle, int dwProcessId) throws Kernel32Exception{
		return k32.OpenProcess(dwDesiredAccess, bInheritHandle, dwProcessId);		
	}
	
	public MEMORY_BASIC_INFORMATION VirtualQueryEx(Pointer hProcess,Pointer lpAddress) throws Kernel32Exception{
		return k32.VirtualQueryEx(hProcess, lpAddress);
	}
	
	public void ReadProcessMemory(Pointer hProcess, Pointer pointer, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead) throws Kernel32Exception{
		k32.ReadProcessMemory(hProcess, pointer, outputBuffer, nSize, outNumberOfBytesRead);
	}
	
	public void WriteProcessMemory(Pointer hProcess, Pointer pAddress, Pointer inputBuffer, int nSize, IntByReference outNumberOfBytesWritten) throws Kernel32Exception{
	    k32.WriteProcessMemory(hProcess, pAddress, inputBuffer, nSize, outNumberOfBytesWritten);
	}
	
	
	public String GetModuleFileNameExA(Pointer hProcess,Pointer hModule){
		return psapi.GetModuleFileNameExA(hProcess, hModule);
	}
	
	public LPMODULEINFO GetModuleInformation(Pointer hProcess, Pointer hModule) throws Exception{
		return psapi.GetModuleInformation(hProcess, hModule);
	}
	
	public String GetProcessImageFileNameA(Pointer hProcess){
		return psapi.GetProcessImageFileNameA(hProcess);
	}
	
	public List<Pointer> EnumProcessModules(Pointer hProcess) throws Exception{
		return psapi.EnumProcessModules(hProcess);
	}
	
	public PPROCESS_MEMORY_COUNTERS GetProcessMemoryInfo(Pointer Process) throws Exception{
		return psapi.GetProcessMemoryInfo(Process);
	}
	
	
	
	
	public Pointer ExtractSmallIcon(String lpszFile, int nIconIndex){
		return s32.ExtractSmallIcon(lpszFile, nIconIndex);
	}
	
	
	
	
	public PROCESS_BASIC_INFORMATION NtQueryInformationProcess(Pointer ProcessHandle, 
			ProcessInformationClass pic){
		return nt.NtQueryInformationProcess(ProcessHandle,  pic);
	}
	
	
	
	public Pointer getHIcon(Pointer hWnd){
		return u32.getHIcon(hWnd);
	}
	
	public BufferedImage getIcon(Pointer hIcon) {
		return u32.getIcon(hIcon);
	}
	
    public Pointer GetAncestor(Pointer hwnd, GAFlags gaFlags){
    	return u32.GetAncestor(hwnd, gaFlags);
    }

}
