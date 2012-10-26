package luz.winapi.constants;

public enum DIFlags{
	DI_MASK	 (1), 
	DI_IMAGE (2),
	DI_NORMAL(3);
	//DI_COMPAT
	//DI_DEFAULTSIZE
	//DI_NOMIRROR
	
	private int value;
	DIFlags(int value)     { this.value=value; }
	public int getValue() { return value;     }
}