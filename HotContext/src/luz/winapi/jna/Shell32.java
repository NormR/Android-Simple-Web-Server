package luz.winapi.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface Shell32 extends StdCallLibrary{
	Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32", Shell32.class);
 
	/*
	 * http://msdn.microsoft.com/en-us/library/ms648069(VS.85).aspx
	 */
	public int ExtractIconExA(String lpszFile, int nIconIndex, Pointer[] phiconLarge, Pointer[] phiconSmall, int nIcons);
	
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms648063(VS.85).aspx
	 */
	boolean DestroyIcon(Pointer hIcon);
	
}