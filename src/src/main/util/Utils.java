package src.main.util;

public class Utils {
	public static String lPad(String value, int padSize, String pad) {
		if(value == null) {
			return value;
		}
		
		if(pad == null) {
			pad = " ";
		}
		
		for(int i = value.length(); i < padSize; i++) {
			value = pad + value;
		}
		
		return value;
	}
	
	public static String skipDir(String dir, String skipDir, String alias) {
		if(dir == null || skipDir == null) {
			return dir;
		}
		
		return dir.replace(skipDir.replace("/", "\\"), alias != null ? alias : "")
				.replace(skipDir.replace("\\", "/"), alias != null ? alias : "");
	}
}
