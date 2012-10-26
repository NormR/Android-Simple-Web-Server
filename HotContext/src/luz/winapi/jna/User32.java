package luz.winapi.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface User32 extends StdCallLibrary {
	User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
	
	/*
	 * http://msdn.microsoft.com/en-us/library/ms648072(VS.85).aspx
	 * 
	 * http://support.microsoft.com/kb/142815
	 */
	Pointer LoadIconA(Pointer hInstance, String lpIconName);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms633522(VS.85).aspx
     */
    int GetWindowThreadProcessId(Pointer hWnd, IntByReference lpdwProcessId);

    /*
     * http://msdn.microsoft.com/en-us/library/ms633514(VS.85).aspx
     */
    Pointer GetTopWindow(Pointer hWnd);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms633509(VS.85).aspx
     */
    Pointer GetNextWindow(Pointer hWnd, int wCmd);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms633497(VS.85).aspx
     */
    public static interface WNDENUMPROC extends StdCallCallback {
    	public abstract boolean callback(Pointer hwnd, Pointer lParam);
    }
    public abstract boolean EnumWindows(WNDENUMPROC wndenumproc, Pointer lParam);
    
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648070%28VS.85%29.aspx
     */
    public static class ICONINFO extends Structure{
        public boolean fIcon;
        public int xHotspot;
        public int yHotspot;
        public Pointer hbmMask;
        public Pointer hbmColor;
		@Override
		protected List getFieldOrder() {
			// TODO Auto-generated method stub
			return Arrays.asList("");
		}
    }
    
    boolean GetIconInfo(Pointer hIcon, ICONINFO piconinfo);
    
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms633499(VS.85).aspx
     */
    Pointer FindWindowA(String lpClassName,String lpWindowName);
    
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms633510(VS.85).aspx
     */
    Pointer GetParent(Pointer hWnd);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms633502(VS.85).aspx
     */
    Pointer GetAncestor(Pointer hwnd, int gaFlags);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms644950(VS.85).aspx
     */
    Pointer SendMessageA(Pointer hWnd,int Msg,int wParam,int lParam);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms644952(VS.85).aspx
     */
    int SendMessageTimeoutA(Pointer hWnd,int Msg,int wParam,int lParam, int fuFlags, int uTimeout, PointerByReference lpdwResult);
        
	/*
	 * http://msdn.microsoft.com/en-us/library/ms633580(VS.85).aspx
     */
    int GetClassLongA(Pointer hWnd,int nIndex);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648058(S.85).aspx
     */
    Pointer CopyIcon(Pointer hIcon);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648063(VS.85).aspx
     */
    boolean DestroyIcon(Pointer hIcon);
    
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648064(VS.85).aspx
     */    
    boolean DrawIcon( Pointer hDC, int X, int Y,Pointer hIcon);
       
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648065(VS.85).aspx
     */ 
    public boolean DrawIconEx(Pointer hdc, int xLeft, int yTop, 
    	Pointer hIcon, int cxWidth, int cyWidth, int istepIfAniCur,
    	Pointer hbrFlickerFreeDraw, int diFlags);

    
    /*
     * http://msdn.microsoft.com/en-us/library/dd144871(VS.85).aspx
     */
    Pointer GetDC(Pointer hWnd);
    
    /*
     * http://msdn.microsoft.com/en-us/library/dd162920(VS.85).aspx
     */
	int ReleaseDC(Pointer hWnd, Pointer hDC);

	

    void GetWindowTextA(Pointer hWnd, byte[] buffer, int buflen);
    boolean IsWindowVisible(Pointer hWnd);

    
    public static final int SW_SHOW = 5;
    boolean ShowWindow(Pointer hWnd, int nCmdShow);
    boolean SetForegroundWindow(Pointer hWnd);  
    boolean BringWindowToTop(Pointer hWnd);
    boolean SetFocus(Pointer hWnd);

}
