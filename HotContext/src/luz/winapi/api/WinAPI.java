package luz.winapi.api;

import java.awt.image.BufferedImage;
import java.util.List;

import luz.winapi.api.exception.Kernel32Exception;
import luz.winapi.constants.DwDesiredAccess;
import luz.winapi.constants.GAFlags;
import luz.winapi.constants.ProcessInformationClass;
import luz.winapi.jna.Kernel32.MEMORY_BASIC_INFORMATION;
import luz.winapi.jna.Ntdll.PROCESS_BASIC_INFORMATION;
import luz.winapi.jna.Psapi.LPMODULEINFO;
import luz.winapi.jna.Psapi.PPROCESS_MEMORY_COUNTERS;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public interface WinAPI {
	
	public ProcessList getProcessList();
	public Pointer OpenProcess(DwDesiredAccess dwDesiredAccess, boolean bInheritHandle, int dwProcessId) throws Kernel32Exception;
	public MEMORY_BASIC_INFORMATION VirtualQueryEx(Pointer hProcess,Pointer lpAddress) throws Kernel32Exception;
	public void ReadProcessMemory(Pointer hProcess, Pointer pointer, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead) throws Kernel32Exception;
	public void WriteProcessMemory(Pointer hProcess, Pointer pAddress, Pointer inputBuffer, int nSize, IntByReference outNumberOfBytesWritten) throws Kernel32Exception;
	public String GetModuleFileNameExA(Pointer hProcess,Pointer hModule);
	public LPMODULEINFO GetModuleInformation(Pointer hProcess, Pointer hModule) throws Exception;
	public String GetProcessImageFileNameA(Pointer hProcess);
	public List<Pointer> EnumProcessModules(Pointer hProcess) throws Exception;
	public PPROCESS_MEMORY_COUNTERS GetProcessMemoryInfo(Pointer Process) throws Exception;
	public Pointer ExtractSmallIcon(String lpszFile, int nIconIndex);
	public PROCESS_BASIC_INFORMATION NtQueryInformationProcess(Pointer ProcessHandle, ProcessInformationClass pic);
	public Pointer getHIcon(Pointer hWnd);
	public BufferedImage getIcon(Pointer hIcon);
    public Pointer GetAncestor(Pointer hwnd, GAFlags gaFlags);


}
