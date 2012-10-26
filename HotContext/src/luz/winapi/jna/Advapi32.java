package luz.winapi.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Advapi32 extends StdCallLibrary{
	Advapi32 INSTANCE = (Advapi32) Native.loadLibrary("advapi32", Advapi32.class);

	/*
	 * http://msdn.microsoft.com/en-us/library/aa379295(VS.85).aspx
	 */
	boolean OpenProcessToken(Pointer ProcessHandle,int DesiredAccess, PointerByReference TokenHandle);

	/*
	 * http://msdn.microsoft.com/en-us/library/aa379180(VS.85).aspx
	 */
	boolean LookupPrivilegeValueA(byte[] lpSystemName, String lpName, LUID lpLuid);

	public static class TOKEN_PRIVILEGES extends Structure {
		public int PrivilegeCount;
		public LUID_AND_ATTRIBUTES[] Privileges;
		
		public TOKEN_PRIVILEGES(int c){
			PrivilegeCount=c;
			Privileges=new LUID_AND_ATTRIBUTES[c];
			for (int i = 0; i < Privileges.length; i++)
				Privileges[i]=new LUID_AND_ATTRIBUTES();
		}

		@Override
		protected List getFieldOrder() {
			return Arrays.asList("PrivilegeCount", "Privileges");
		}		
	}
	
	public static class LUID_AND_ATTRIBUTES extends Structure {
		public LUID Luid;
		public int Attributes;
		
		@Override
		protected List getFieldOrder() {
			return Arrays.asList("Luid", "Attributes");
		}	
	}
	
	public static class LUID extends Structure {
		public int LowPart; 
		public NativeLong HighPart; 
		
		@Override
		protected List getFieldOrder() {
			return Arrays.asList("LowPart", "HighPart");
		}
	}
	
	/*
	 * http://msdn.microsoft.com/en-us/library/aa375202(VS.85).aspx
	 */
	boolean AdjustTokenPrivileges(Pointer TokenHandle, boolean DisableAllPrivileges,
		TOKEN_PRIVILEGES NewState, int BufferLength,
		TOKEN_PRIVILEGES PreviousState, IntByReference ReturnLength);

	
}