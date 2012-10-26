package luz.winapi.constants;

public enum BICompression{
	BI_RGB	         (0), 
	BI_RLE8          (1),
	BI_RLE4          (2),
	BI_BITFIELDS     (3);
	
	//BI_ALPHABITFIELDS();

	private int value;
	BICompression(int value)     { this.value=value; }
	public int getValue() { return value;     }
}