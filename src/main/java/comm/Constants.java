package comm;

public class Constants {
	public static final String ROOT_DIRECTORY = "ROOT";
	
	public static final String NAME_SEPARATOR = "_";
	
	public static final String MILLISEC_SEPARATOR = ".";
	
	public static final String DATETIME_FORMAT = "yyyyMMdd" + NAME_SEPARATOR + "HHmmss" + MILLISEC_SEPARATOR + "SSS";
	
	public static final String DATETIME_FORMAT_CONTAINS_OFFSET = "yyyyMMdd" + NAME_SEPARATOR + "HHmmss" + MILLISEC_SEPARATOR + "SSS" + NAME_SEPARATOR + "xxxx";
}
