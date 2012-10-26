package luz.winapi.constants;

public enum FType{
	ICON_SMALL  (0),
	ICON_BIG    (1),
	ICON_SMALL2 (2);

	private int value;
	FType(int value)     { this.value=value; }
	public int getValue() { return value;     }
}