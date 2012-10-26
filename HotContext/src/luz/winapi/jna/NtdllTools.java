package luz.winapi.jna;

import luz.winapi.jna.ProcessInformationClass;
import luz.winapi.jna.Ntdll;
import luz.winapi.jna.Ntdll.PROCESS_BASIC_INFORMATION;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class NtdllTools {
        private static final NtdllTools INSTANCE=new NtdllTools();      //Eager Creation
        private static Ntdll nt = Ntdll.INSTANCE;
       
        private NtdllTools(){}
       
        public static NtdllTools getInstance(){ 
                return INSTANCE;
        }
       
        ////////////////////////////////////////////////////////////////////////
       
        public PROCESS_BASIC_INFORMATION NtQueryInformationProcess(Pointer ProcessHandle,
                        ProcessInformationClass pic){
        PROCESS_BASIC_INFORMATION info = new PROCESS_BASIC_INFORMATION(ProcessHandle);
        IntByReference ret = new IntByReference();
        nt.NtQueryInformationProcess(ProcessHandle, pic.getValue(), info, info.size(), ret);
        return info;
        }
   
}
