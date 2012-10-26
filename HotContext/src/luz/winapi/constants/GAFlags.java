package luz.winapi.constants;

public enum GAFlags{
	GA_PARENT	(1), 
	GA_ROOT		(2),
	GA_ROOTOWNER(3);
	
	private int value;
	GAFlags(int value)     { this.value=value; }
	public int getValue() { return value;     }
}