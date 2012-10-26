package luz.winapi.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
    static int MAX_PATH=256;
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms683180(VS.85).aspx
     */
    int GetCurrentProcessId();
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms683179(VS.85).aspx
     * NOT tested
     */
    Pointer GetCurrentProcess();

    /*
     * http://msdn.microsoft.com/en-us/library/ms679360(VS.85).aspx
     * NOT tested
     */
    int GetLastError();
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms684320(VS.85).aspx
     */
    Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms680553(VS.85).aspx
     */
    boolean ReadProcessMemory(Pointer hProcess, Pointer pAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);
   
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms681674(v=VS.85).aspx
     */
    boolean WriteProcessMemory(Pointer hProcess, Pointer pAddress, Pointer inputBuffer, int nSize, IntByReference outNumberOfBytesWritten);
    
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms724211(VS.85).aspx
     */
    boolean CloseHandle(Pointer hObject);
    
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms682489(VS.85).aspx
     * http://msdn.microsoft.com/en-us/library/ms686701(VS.85).aspx
     */
	Pointer CreateToolhelp32Snapshot(int dwFlags,int th32ProcessID);
        
    /*
     * http://msdn.microsoft.com/en-us/library/ms684839(VS.85).aspx
     */
    public static class LPPROCESSENTRY32 extends Structure {
		//public static class ByValue     extends LPPROCESSENTRY32 implements Structure.ByValue {}
		//public static class ByReference extends LPPROCESSENTRY32 implements Structure.ByReference {}
		public int				dwSize;							//The size of the structure, in bytes. If you do not initialize dwSize, Process32First fails.
		public int				cntUsage;							//This member is no longer used and is always set to zero.
		public int				th32ProcessID;					//The process identifier.
		public IntByReference	th32DefaultHeapID;					//This member is no longer used and is always set to zero.
		public int				th32ModuleID;						//This member is no longer used and is always set to zero.
		public int				cntThreads;						//The number of execution threads started by the process.
		public int				th32ParentProcessID;			//The identifier of the process that created this process (its parent process).
		public NativeLong      pcPriClassBase;					//The base priority of any threads created by this process.
		public int				dwFlags;							//This member is no longer used and is always set to zero.
		public char[]			szExeFile = new char[MAX_PATH];//The name of the executable file for the process.
		
		public LPPROCESSENTRY32(){
			dwSize=size();
		}
		
		/*
		 * LPPROCESSENTRY32 seems to need the char array. However its not the correct structure.
		 * So this needs to be transformed.
		 */
		public String getSzExeFile(){
			StringBuilder sb = new StringBuilder();
			char a, b;
			for (int i = 0; i < szExeFile.length; i++) {
				a=(char)(szExeFile[i]   &0xFF);
				if (a==0) break;
				sb.append(a);
				
				b=(char)(szExeFile[i]>>8&0xFF);
				if (b==0) break;
				sb.append(b);
			}
			return sb.toString();
		}

		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("cntThreads", "cntUsage", "dwFlags", "dwSize", "pcPriClassBase", "szExeFile", "th32DefaultHeapID", "th32ModuleID", "th32ParentProcessID", "th32ProcessID");
		}
    }
   
    /*
     * http://msdn.microsoft.com/en-us/library/ms684834(VS.85).aspx
     */
    boolean Process32First(Pointer hSnapshot, LPPROCESSENTRY32 lppe);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms684836(VS.85).aspx
     */
    boolean Process32Next(Pointer hSnapshot, LPPROCESSENTRY32 lppe);
 

    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648042(VS.85).aspx
     */    
    Pointer FindResourceA(Pointer hModule, String lpName, String lpType);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648047(VS.85).aspx
     */
    Pointer LockResource(Pointer hResData);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648048(VS.85).aspx
     */    
    int SizeofResource(Pointer hModule,Pointer hResInfo);
   
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648037(VS.85).aspx
     */
    public static interface ENUMRESNAMEPROC extends StdCallCallback {
    	public abstract boolean callback(Pointer hModule, String lpszType, String lpszName, IntByReference lParam);
    }
    boolean EnumResourceNamesA(Pointer hModule,String lpszType, ENUMRESNAMEPROC lpEnumFunc, IntByReference lParam);
    
    
    
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms724958(VS.85).aspx
     * http://www.pinvoke.net/default.aspx/Structures.SYSTEM_INFO
     */
    public static class LPSYSTEM_INFO extends Structure {
		public short	wProcessorArchitecture;
		public short	wReserved;		
		public int		dwPageSize;
		public Pointer	lpMinimumApplicationAddress;
		public Pointer	lpMaximumApplicationAddress;
		public Pointer	dwActiveProcessorMask;
		public int		dwNumberOfProcessors;
		public int		dwProcessorType;
		public int		dwAllocationGranularity;
		public short	wProcessorLevel;
		public short	wProcessorRevision;
		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("");
		}
    }    	
    	
    /*
     * http://msdn.microsoft.com/en-us/library/ms724381(VS.85).aspx
     */
    void GetSystemInfo(LPSYSTEM_INFO lpSystemInfo);
    

    /*
     * http://msdn.microsoft.com/en-us/library/aa366775(VS.85).aspx
     */
    public static class MEMORY_BASIC_INFORMATION extends Structure {
    	public Pointer	BaseAddress;
    	public Pointer	AllocationBase;
    	public int		AllocationProtect;
    	public int		RegionSize;
    	public int		State;
    	public int		Protect;
    	public int		Type;
		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("");
		}
    }
    
    /*
     * http://msdn.microsoft.com/en-us/library/aa366907(VS.85).aspx
     */
    public int VirtualQueryEx(Pointer hProcess,Pointer lpAddress, MEMORY_BASIC_INFORMATION lpBuffer, int dwLength);

    
}
