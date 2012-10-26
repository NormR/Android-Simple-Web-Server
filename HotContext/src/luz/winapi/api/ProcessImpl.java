package luz.winapi.api;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;

import luz.winapi.api.exception.Kernel32Exception;
import luz.winapi.constants.DwDesiredAccess;
import luz.winapi.constants.GAFlags;
import luz.winapi.constants.ProcessInformationClass;
import luz.winapi.jna.Kernel32.LPPROCESSENTRY32;
import luz.winapi.jna.Kernel32.MEMORY_BASIC_INFORMATION;
import luz.winapi.jna.Ntdll.PEB;
import luz.winapi.jna.Ntdll.PROCESS_BASIC_INFORMATION;
import luz.winapi.tools.Kernel32Tools;



import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;


public class ProcessImpl implements Process {
//	private static final Log log = LogFactory.getLog(ProcessImpl.class);
	private WinAPI winAPI;

	private List<Pointer> hWnds = new LinkedList<Pointer>();
	private final int pid;
	private final String szExeFile;
	private final int cntThreads;
	private final int th32ParentProcessID;
	private final int pcPriClassBase;
	
	private MemoryListener listener;
	
	public ProcessImpl(LPPROCESSENTRY32 pe32, WinAPI winapi) {
		this.winAPI=winapi;
		this.pid=pe32.th32ProcessID;
		this.szExeFile=pe32.getSzExeFile();
		this.cntThreads=pe32.cntThreads;
		this.pcPriClassBase=pe32.pcPriClassBase.intValue();
		this.th32ParentProcessID=pe32.th32ParentProcessID;
	}

	private Pointer handleCache =null;
	public Pointer getHandle() throws Kernel32Exception{
		if (handleCache!=null)
			return handleCache;
		DwDesiredAccess dwDesiredAccess=new DwDesiredAccess();
		dwDesiredAccess.setPROCESS_ALL_ACCESS();
		handleCache = winAPI.OpenProcess(dwDesiredAccess, false, this.pid);
		return handleCache;
	}
	
	
	public void clearHwnds() {
		hWnds.clear();
	}
	
	public void addHwnd(Pointer hWnd) {
		hWnds.add(hWnd);		
	}
	
	public List<Pointer> getHwnds(){
		return hWnds;
	}
	
	

	//Getter
	
	public int getPid() {
		return pid;
	}

	public String getSzExeFile() {
		return szExeFile;
	}

	public int getCntThreads() {
		return cntThreads;
	}
	
	public int getTh32ParentProcessID() {
		return th32ParentProcessID;
	}	

	public int getPcPriClassBase() {
		return pcPriClassBase;
	}
	
	public String getProcessImageFileName(){
		 try {
			return winAPI.GetProcessImageFileNameA(getHandle());
		} catch (Exception e) {
			return "";
		}
	}

	public String getModuleFileNameExA(){
		 try {
			return winAPI.GetModuleFileNameExA(getHandle(), null);
		} catch (Exception e) {
			return "";
		}
	}

	public List<Module> getModules(){
		//TODO add modules cache?
		try {
			List<Pointer> pointers = winAPI.EnumProcessModules(getHandle());
			List<Module> modules = new LinkedList<Module>();
			for (Pointer hModule : pointers) 
				modules.add(new Module(getHandle(), hModule, winAPI));
			return modules;
		} catch (Exception e) {
			return null;
		}
	}

	private Module moduleCache=null;
	public Module getModule(){
		if (moduleCache!=null)
			return moduleCache;	

		List<Module> modules = getModules();
		if (modules!=null && modules.size()>0)
			moduleCache=modules.get(0);

		return moduleCache;
	}
	
	@Override
	public String getStatic(Long address) {
		if (address==null)
			return null;
		List<Module> modules = getModules();
		int begin, end;
		for (Module module : modules) {
			begin = module.getLpBaseOfDll();
			end= begin+module.getSizeOfImage();
			//log.trace("module "+begin+" "+end+" "+module.getFileName());
			if (begin<=address && address<=end){
				File f = new File(module.getFileName());
				return  f.getName()+ "+" +String.format("%08X", address-begin);
			}
		}		
		return null;
	}
	
	public int getBase() {
		Module module = getModule();
		if (module!=null)
			return module.getLpBaseOfDll();
		else
			return -1;
	}
	
	public int getSize() {
		Module module = getModule();
		if (module!=null)
			return module.getSizeOfImage();
		else
			return 0;
	}
	
	public int getMemUsage() {
		try {
			return winAPI.GetProcessMemoryInfo(getHandle()).WorkingSetSize;
		} catch (Exception e) {
			return 0;
		}
	}
	
	private ImageIcon iconCache=null;
	public ImageIcon getIcon(){
		if (iconCache!=null)
			return iconCache;
		
		Pointer hIcon = null;
		
        hIcon=winAPI.ExtractSmallIcon(this.getModuleFileNameExA(), 1);        
        if (hIcon==null){
        	hIcon=winAPI.ExtractSmallIcon(this.getSzExeFile(), 1);
        }
        
        if (hIcon==null){      	
        	if(hWnds.size()>0){
        		hIcon = winAPI.getHIcon(winAPI.GetAncestor(hWnds.get(0), GAFlags.GA_ROOTOWNER));
        	}
//        	for (Pointer hWnd : hWnds) {
//        		hIcon = u32.getHIcon(hWnd);
//        		if (hIcon!=null)
//        			break;
//			}
        }
        
        if (hIcon!=null)
        	iconCache=new ImageIcon(winAPI.getIcon(hIcon));
        else
        	iconCache=new ImageIcon();
        return iconCache;        
	}
	
	private PROCESS_BASIC_INFORMATION infoCache=null;
	public PROCESS_BASIC_INFORMATION getPROCESS_BASIC_INFORMATION(){
		if (infoCache!=null)
			return infoCache;
		try {
			infoCache = winAPI.NtQueryInformationProcess(getHandle(), ProcessInformationClass.ProcessBasicInformation);
		} catch (Exception e) {}
		
		return infoCache;
	}
	
	private PEB pebCache=null;
	public PEB getPEB() throws Exception{
		if (pebCache!=null)
			return pebCache;
		pebCache=getPROCESS_BASIC_INFORMATION().getPEB();
		return pebCache;
	}
	
	public MEMORY_BASIC_INFORMATION VirtualQueryEx(Pointer lpAddress) throws Kernel32Exception{
		return winAPI.VirtualQueryEx(getHandle(), lpAddress);
	}
	
	public void ReadProcessMemory(Pointer pAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead) throws Kernel32Exception{
		winAPI.ReadProcessMemory(getHandle(), pAddress, outputBuffer, nSize, outNumberOfBytesRead);
	}
	
	public void WriteProcessMemory(Pointer pAddress, Pointer inputBuffer, int nSize, IntByReference outNumberOfBytesWritten) throws Kernel32Exception{
		winAPI.WriteProcessMemory(getHandle(), pAddress, inputBuffer, nSize, outNumberOfBytesWritten);
	}
	
	
	public synchronized void search(long from, long to, final Object value, MemoryListener listener) throws Kernel32Exception {
//		log.debug("search from "+Long.toHexString(from)+" to "+Long.toHexString(to)+" value "+value+" listener "+listener);
		this.listener=listener;
		long timer=System.currentTimeMillis();

		this.listener.init(value);
		search(from, to);
		
//		log.debug("timer "+(System.currentTimeMillis()-timer));
	}
	
	private void search(long from, long to) throws Kernel32Exception{
		int partSize=512*1024;
		int bufferSize=partSize+listener.getOverlapping();
		int readSize;
		long regionEnd;
		MEMORY_BASIC_INFORMATION info;
		Memory outputBuffer = new Memory(bufferSize);
		long maxRegionSize=0;
		
		for (long regionBegin = from; regionBegin < to; ) {
			info=VirtualQueryEx(Pointer.createConstant(regionBegin));
			maxRegionSize=Math.max(maxRegionSize, info.RegionSize);
			regionEnd=regionBegin+info.RegionSize;
		
			if (info.State==Kernel32Tools.MEM_COMMIT 
				&& (info.Protect&Kernel32Tools.PAGE_NOACCESS    )==0
				&& (info.Protect&Kernel32Tools.PAGE_GUARD       )==0
				&& (info.Protect&Kernel32Tools.PAGE_EXECUTE_READ)==0
				&& (info.Protect&Kernel32Tools.PAGE_READONLY    )==0
			){
//				log.trace("Region:\t"+Long.toHexString(regionBegin)+" - "+Long.toHexString(regionBegin+info.RegionSize));
				
				for (long regionPart = regionBegin; regionPart < regionEnd; regionPart=regionPart+partSize) {
					if ((regionPart+bufferSize)<regionEnd)
						readSize=bufferSize;
					else
						readSize=(int)(regionEnd-regionPart);
					
//					log.trace("Read:\t\t"+Long.toHexString(regionPart)+" - "+Long.toHexString(regionPart+readSize)+"\t"+Integer.toHexString(info.Type));
					try{
						ReadProcessMemory(Pointer.createConstant(regionPart), outputBuffer, readSize, null);
						listener.mem(outputBuffer, regionPart, readSize);
					}catch(Exception e){	//FIXME 
//						log.warn("Cannot search mem\t"+Long.toHexString(regionPart)+"\t"+Integer.toHexString(info.Type), e);
					}
				}
			}
			regionBegin+=info.RegionSize;
		}
//		log.debug("maxRegionSize "+(maxRegionSize/1024)+" kB");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null)
			return false;
		
		if (!(obj instanceof ProcessImpl))
			return false;
		
		if (this.getPid()!=((ProcessImpl)obj).getPid())
			return false;
		
		return true;
	}

	
	//TODO stop search function
	//TODO search function progess

	
}
