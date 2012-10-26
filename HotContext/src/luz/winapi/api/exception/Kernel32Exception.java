package luz.winapi.api.exception;

public class Kernel32Exception extends Exception {
	private static final long serialVersionUID = 458021893060793152L;
	
	public Kernel32Exception(String msg){
		super(msg);
	}
	
	public Kernel32Exception(Exception e){
		super(e);
	}
	
}
