package util;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import comm.Constants;
import model.Photo;

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
	
	public static boolean containsAny(String text, CharSequence... sequences) {
		if(text == null || sequences == null) {
			return false;
		}
		
		for(CharSequence sequence : sequences) {
			if(text.indexOf(sequence.toString()) > -1) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean equalsAny(String text, String... anotherTexts) {
		if(text == null || anotherTexts == null) {
			return false;
		}
		
		for(String anotherText : anotherTexts) {
			if(text.equals(anotherText)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean validDateText(String text) {
		return text != null && !text.equals("0000:00:00 00:00:00");
	}
	
	public static Integer getNumbering(String source) {
		try {
			source = Paths.get(source).getFileName().toString();
			
			if(source.length() >= 37 && source.indexOf(Constants.NAME_SEPARATOR) == 8 && source.indexOf(Constants.MILLISEC_SEPARATOR) == 15
					&& (source.indexOf("+") == 20 || source.indexOf("-") == 20)) {
				// photoSync contains offset format
				ZonedDateTime.parse(source.substring(0, 25), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_CONTAINS_OFFSET));
				return Integer.parseInt(source.substring(32, 37));
			} else if(source.length() >= 31 && source.indexOf(Constants.NAME_SEPARATOR) == 8 && source.indexOf(Constants.MILLISEC_SEPARATOR) == 15) {
				// photoSync format
				LocalDateTime.parse(source.substring(0, 19), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT));
				return Integer.parseInt(source.substring(26, 31));
			}
		} catch(Exception e) {
		}
		
		return null;
	}
}
