package luz.winapi.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public interface Gdi32 extends StdCallLibrary{
	Gdi32 INSTANCE = (Gdi32) Native.loadLibrary("gdi32", Gdi32.class);
 
	/*
	 * http://msdn.microsoft.com/en-us/library/dd183489(VS.85).aspx
	 */
	Pointer CreateCompatibleDC(Pointer hdc);

	/*
	 * http://msdn.microsoft.com/en-us/library/dd183488(VS.85).aspx
	 */
	Pointer CreateCompatibleBitmap(Pointer hdc, int nWidth, int nHeight);

	/*
	 * http://msdn.microsoft.com/en-us/library/dd162957(VS.85).aspx
	 */
	Pointer SelectObject(Pointer hdc,Pointer hgdiobj);
	
    /*
     * http://msdn.microsoft.com/en-us/library/dd183533(VS.85).aspx
     */
    boolean DeleteDC(Pointer hdc);
    

    /*
     *  http://msdn.microsoft.com/en-us/library/dd144909(VS.85).aspx
     */
    public int GetPixel(Pointer hdc, int nXPos, int nYPos);

    /*
     *  http://msdn.microsoft.com/en-us/library/dd183539(VS.85).aspx
     */  
    public boolean DeleteObject(Pointer hObject);

    
    public static class BITMAPINFO extends Structure {
    	public BITMAPINFOHEADER bmiHeader;
    	public int[] bmiColors = new int[1];
//    	public RGBQUAD[] bmiColors=new RGBQUAD[1];
		@Override
		protected List getFieldOrder() {
			return Arrays.asList("bmiHeader", "bmiColors");
		}
    }
    public static class BITMAPINFOHEADER extends Structure {
    	public int biSize=size();
    	public int  biWidth;
    	public int  biHeight;
    	public short  biPlanes=1;
    	public short  biBitCount;
    	public int biCompression;
    	public int biSizeImage=0;
    	public long  biXPelsPerMeter;
    	public long  biYPelsPerMeter;
    	public int biClrUsed=0;
    	public int biClrImportant=0;
    	
    	@Override
		protected List getFieldOrder() {
			return Arrays.asList("");
		}
    };
    public static class RGBQUAD extends Structure {
    	public byte rgbBlue;
    	public byte rgbGreen;
    	public byte rgbRed;
    	public byte rgbReserved=0;
    	@Override
		protected List getFieldOrder() {
			return Arrays.asList("");
		}
    }

    /*
     * http://msdn.microsoft.com/en-us/library/dd144879(VS.85).aspx
     */
    int GetDIBits(Pointer hdc, Pointer hbmp, int uStartScan, int cScanLines,
    	byte[] lpvBits, BITMAPINFO lpbi, int uUsage);

    


}