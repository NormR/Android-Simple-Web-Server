package wrm.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import luz.winapi.api.ProcessList;
import luz.winapi.api.WinAPI;
import luz.winapi.api.WinAPIImpl;
import luz.winapi.jna.DwDesiredAccess;
import luz.winapi.jna.Ntdll;
import luz.winapi.jna.User32;
import luz.winapi.jna.Ntdll.PROCESS_BASIC_INFORMATION;
import luz.winapi.jna.NtdllTools;
import luz.winapi.jna.ProcessInformationClass;
import luz.winapi.jna.User32.WNDENUMPROC;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
//import com.sun.jna.platform.win32.Kernel32;
//import com.sun.jna.platform.win32.Kernel32Util;
//import com.sun.jna.platform.win32.NtDll;
//import com.sun.jna.platform.win32.NtDllUtil;
//import com.sun.jna.platform.win32.User32;
//import com.sun.jna.platform.win32.WinDef.HWND;
//import com.sun.jna.platform.win32.WinDef.RECT;
//import com.sun.jna.platform.win32.WinNT.HANDLE;
//import com.sun.jna.platform.win32.WinUser.WINDOWINFO;
//import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.ptr.IntByReference;

public class Interop
{
	public static class WindowInformation {
		String title;

		
		int pid;
		
		@JsonIgnore
		Pointer hwnd;
		
		String executable;
		
		public WindowInformation() {
		}
		
		public WindowInformation(String title, int pid, Pointer hwnd,
				String executable) {
			super();
			this.title = title;
			this.pid = pid;
			this.hwnd = hwnd;
			this.executable = executable;
		}
		public String getTitle() {
			return title;
		}
		public int getPid() {
			return pid;
		}
		public Pointer getHwnd() {
			return hwnd;
		}
		public String getExecutable() {
			return executable;
		}
		
		
		
	}
	
	public static List<WindowInformation> getVisibleWindows() {

	    final List<WindowInformation> inflList = new LinkedList<>();

	    
	    WinAPI win = WinAPIImpl.getInstance();
		final ProcessList processList = win.getProcessList();
	    
	    
		
	    User32.INSTANCE.EnumWindows(new WNDENUMPROC() {
			
			
			@Override
			public boolean callback(Pointer hWnd, Pointer arg1) {
				 if (User32.INSTANCE.IsWindowVisible(hWnd)) {
			                byte[] buffer = new byte[1024];
			                User32.INSTANCE.GetWindowTextA(hWnd, buffer, buffer.length);
			                String title = Native.toString(buffer); 
			                
			                if (title.isEmpty())
			                	return true;
			                
			                IntByReference lpdwProcessId=new IntByReference();

			                User32.INSTANCE.GetWindowThreadProcessId(hWnd, lpdwProcessId);
			                int pid = lpdwProcessId.getValue();
			                
			                
			                String exec = findProcessExec(processList, pid);
			                
			                inflList.add(new WindowInformation(title, pid, hWnd, exec));

							System.out.println( pid + " " + exec + " " + "\t\t\""+ title +"\"");
			           
			        }
			        return true;
			}

			
		}, null);
	
		return inflList;
	 
	}

	
	
	
	public static void showWindow(final WindowInformation window) {

	    WinAPI win = WinAPIImpl.getInstance();
	    
	    
		
	    User32.INSTANCE.EnumWindows(new WNDENUMPROC() {
			
			
			@Override
			public boolean callback(Pointer hWnd, Pointer arg1) {
				 if (User32.INSTANCE.IsWindowVisible(hWnd)) {
			                byte[] buffer = new byte[1024];
			                User32.INSTANCE.GetWindowTextA(hWnd, buffer, buffer.length);
			                String title = Native.toString(buffer); 
			                
			                if (!title.equals(window.title))
			                	return true;
			                
			                IntByReference lpdwProcessId=new IntByReference();

			                User32.INSTANCE.GetWindowThreadProcessId(hWnd, lpdwProcessId);
			                int pid = lpdwProcessId.getValue();
			                
			                if (window.pid == pid)
			                {
			                	User32.INSTANCE.ShowWindow(hWnd, 11); //forceMinimize
			                	try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
			                	User32.INSTANCE.ShowWindow(hWnd, 1);//show normal
			                	User32.INSTANCE.SetForegroundWindow(hWnd);
			                	User32.INSTANCE.BringWindowToTop(hWnd);
			                	
			                	return false;
			                }
			           
			        }
			        return true;
			}

			
		}, null);
	
	 
	}
	
	
	

	private static String findProcessExec(ProcessList processList, int pid) {
		for(luz.winapi.api.Process p : processList)
			if( p.getPid() == pid)
				return p.getSzExeFile();
		
		return null;
	}
	    
	 
}
