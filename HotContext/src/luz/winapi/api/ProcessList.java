package luz.winapi.api;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.sun.jna.Pointer;

public class ProcessList implements Iterable<Process> {
	private static final long serialVersionUID = 6688283424432327658L;
	private Map<Integer, Process> map = new HashMap<Integer, Process>();
	private List        <Process> list= new LinkedList      <Process>();

	
	public void add(Process p){
		map.put(p.getPid(), p);
		list.add(p);
	}	

	public void add(int pid, Pointer hWnd) {
		map.get(pid).addHwnd(hWnd);
	}
	
	
	public Process get(Integer index){
		return list.get(index);
	}


	@Override
	public Iterator<Process> iterator() {
		return list.iterator();
	}

	public int size() {
		return list.size();
	}


}
