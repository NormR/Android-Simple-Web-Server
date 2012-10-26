package luz.winapi.api;

import luz.winapi.jna.Psapi.LPMODULEINFO;

import com.sun.jna.Pointer;

public class Module {
	private Pointer hProcess;
	private Pointer hModule;
	private int lpBaseOfDll;
	private int  SizeOfImage=0;
	private Pointer EntryPoint=null;
	
	private WinAPI winAPI;
	
	protected Module(){}		
			
	public Module(Pointer hProcess, Pointer hModule, WinAPI winAPI){
		this.winAPI=winAPI;
		this.hProcess=hProcess;
		this.hModule=hModule;
	}

	public Pointer getPointer(){
		return hModule;
	}
	
	public String getFileName(){
		return winAPI.GetModuleFileNameExA(hProcess,hModule);
	}
	
	
	private void GetModuleInformation(){
		if(EntryPoint==null){
			try {
				LPMODULEINFO x = winAPI.GetModuleInformation(hProcess, hModule);
				lpBaseOfDll=x.lpBaseOfDll;
				SizeOfImage=x.SizeOfImage;
				EntryPoint=x.EntryPoint;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getLpBaseOfDll(){
		GetModuleInformation();
		return lpBaseOfDll;
	}
	
	public int getSizeOfImage(){
		GetModuleInformation();
		return SizeOfImage;
	}
	
	public Pointer getEntryPoint(){
		GetModuleInformation();
		return EntryPoint;
	}
	

	
}
