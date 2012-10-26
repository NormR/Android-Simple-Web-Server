package luz.winapi.constants;

public enum DIBwUsage{
	DIB_RGB_COLORS	(0);
	//DIB_PAL_COLORS 	();
	
	private int value;
	DIBwUsage(int value)     { this.value=value; }
	public int getValue() { return value;     }
}