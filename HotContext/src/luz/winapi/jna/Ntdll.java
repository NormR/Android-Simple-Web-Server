package luz.winapi.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Ntdll extends StdCallLibrary{
	Ntdll INSTANCE = (Ntdll) Native.loadLibrary("ntdll", Ntdll.class);
	final Kernel32 k32 = Kernel32.INSTANCE;
	
	public static class PROCESS_BASIC_INFORMATION extends Structure {
		public int ExitStatus;
		public Pointer PebBaseAddress;
		public int AffinityMask;
		public int BasePriority;
		public int UniqueProcessId;
		public int ParentProcessId;
		
		private Pointer process;
		public PROCESS_BASIC_INFORMATION(Pointer process) {
			this.process=process;
		}

		public PEB getPEB() {
			if (PebBaseAddress==null)
				return null;
	        PEB peb = new PEB(process);
	        k32.ReadProcessMemory(process, PebBaseAddress, peb.getPointer(), peb.size(), null);
	        peb.read();
	        return peb;
		}

		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("");
		}
	}
	
	/*
	 * http://msdn.microsoft.com/en-us/library/aa813706(VS.85).aspx
	 * http://undocumented.ntinternals.net/UserMode/Undocumented Functions/NT Objects/Process/PEB.html
	 * http://www.vckbase.com/document/journal/vckbase47/figures/EscapeFromDLLHellPart2Fig.htm
	 */
	public static class PEB extends Structure {
		public byte InheritedAddressSpace;		//0x00
		public byte ReadImageFileExecOptions;	//0x01
		public byte BeingDebugged;				//0x02
		public byte Spare;						//0x03
		public Pointer Mutant;					//0x04
		public Pointer ImageBaseAddress;		//0x08
		public Pointer Ldr;						//0x0C
		public Pointer ProcessParameters; 		//0x10 RTL_USER_PROCESS_PARAMETERS is located at address 0x20000 (for all processes created by call WIN32 API CreateProcess).
		public byte[] Reserved4=new byte[104];
		public int[]  Reserved5=new int[52];
		public Pointer PostProcessInitRoutine;	//0x14C
		public byte[] Reserved6=new byte[128];
		public int[]  Reserved7=new int[1];	//0x1D4
		public NativeLong SessionId;
		
		private Pointer process;
		public PEB(Pointer process){
			this.process=process;
		}
		
		public RTL_USER_PROCESS_PARAMETERS getProcessParameters() {
			if (ProcessParameters==null)
				return new RTL_USER_PROCESS_PARAMETERS(process);
			RTL_USER_PROCESS_PARAMETERS pp = new RTL_USER_PROCESS_PARAMETERS(process);
	        k32.ReadProcessMemory(process, ProcessParameters, pp.getPointer(), pp.size(), null);
	        pp.read();
	        return pp;
		}

		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("");
		}
	}
	
	/*
	 * http://msdn.microsoft.com/en-us/library/aa813741(VS.85).aspx
	 * http://undocumented.ntinternals.net/UserMode/Structures/RTL_USER_PROCESS_PARAMETERS.html
	 * http://www.vckbase.com/document/journal/vckbase47/figures/EscapeFromDLLHellPart2Fig.htm
	 */
	public static class RTL_USER_PROCESS_PARAMETERS extends Structure {
		public NativeLong MaximumLength;			//0x00
		public NativeLong Length;					//0x04
		public NativeLong Flags;					//0x08
		public NativeLong DebugFlags;				//0x0C
		public Pointer ConsoleHandle;				//0x10
		public NativeLong ConsoleFlags;				//0x14
		public Pointer StdInputHandle;				//0x18
		public Pointer StdOutputHandle;				//0x1C
		public Pointer StdErrorHandle;				//0x20
		public CURDIR CurrentDirectoryPath;			//0x24
		public UNICODE_STRING DllPath;				//0x30
		public UNICODE_STRING ImagePathName;		//0x38
		public UNICODE_STRING CommandLine;			//0x40
		public Pointer Environment;					//0x48
		public NativeLong StartingPositionLeft;		//0x4C
		public NativeLong StartingPositionTop;		//0x50
		public NativeLong Width;					//0x54
		public NativeLong Height;					//0x58
		public NativeLong CharWidth;				//0x5C
		public NativeLong CharHeight;				//0x60
		public NativeLong ConsoleTextAttributes	;	//0x64
		public NativeLong WindowFlags;				//0x68
		public NativeLong ShowWindowFlags;			//0x6C
		public UNICODE_STRING WindowTitle;			//0x70
		public UNICODE_STRING DesktopName;			//0x78
		public UNICODE_STRING ShellInfo;			//0x80
		public UNICODE_STRING RuntimeData;			//0x88
		public Pointer DLCurrentDirectory;			//0x90
		
		private Pointer process;
		public RTL_USER_PROCESS_PARAMETERS(Pointer process){
			this.process=process;
		}
		
		private Memory str=null;
		private String readUNICODE_STRING(Pointer pointer) {
			if (pointer==null)
				return null;
			if (str==null)
				str=new Memory(512);
	        k32.ReadProcessMemory(process, pointer, str, (int)str.SIZE, null);
	        return str.getString(0, true);
		}		
		
		public String getCurrentDirectoryPath() throws Exception{
	        return readUNICODE_STRING(CurrentDirectoryPath.DosPath.Buffer);
		}
		
		public String getDllPath() throws Exception{
	        return readUNICODE_STRING(DllPath.Buffer);
		}
		
		public String getImagePathName() throws Exception{
	        return readUNICODE_STRING(ImagePathName.Buffer);
		}
		
		public String getCommandLine() throws Exception{
	        return readUNICODE_STRING(CommandLine.Buffer);
		}
		
		public String getWindowTitle() throws Exception{
	        return readUNICODE_STRING(WindowTitle.Buffer);
		}
		
		public String getDesktopName() throws Exception{
	        return readUNICODE_STRING(DesktopName.Buffer);
		}
		
		public String getShellInfo() throws Exception{
	        return readUNICODE_STRING(ShellInfo.Buffer);
		}
		
		public String getRuntimeData() throws Exception{
	        return readUNICODE_STRING(RuntimeData.Buffer);
		}

		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("");
		}
	}
	
	public static class CURDIR extends Structure {
		public UNICODE_STRING DosPath;
		public Pointer Handle;
		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("");
		}		
	}
	
	/*
	 * http://msdn.microsoft.com/en-us/library/aa380518(VS.85).aspx
	 */
	public static class UNICODE_STRING extends Structure {
		public short Length;
		public short MaximumLength;
		public Pointer Buffer;
		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("");
		}
	}
	

	
	
	
//	public static class NTSTATUS extends Structure {
//		    public Pointer ProcessHandle;
//		    public int ProcessInformationClass;
//		    public Pointer ProcessInformation;
//		    public int ProcessInformationLength;
//		    public IntByReference ReturnLength;
//		   }


	
	

	
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms684280(VS.85).aspx
	 */
	Pointer NtQueryInformationProcess( Pointer ProcessHandle, int ProcessInformationClass,
			PROCESS_BASIC_INFORMATION ProcessInformation,
			int ProcessInformationLength, IntByReference ReturnLength);

    
}