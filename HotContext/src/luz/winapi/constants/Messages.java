package luz.winapi.constants;

public enum Messages{
	WM_GETICON	(0x7f);
	
	private int value;
	Messages(int value)     { this.value=value; }
	public int getValue() { return value;     }
}