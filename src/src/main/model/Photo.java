package src.main.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public class Photo {
	public static final String TAKEN_DATE = "Create Date";
	public static final String FILE_MODIFICATION_DATE = "File Modification Date/Time";
	
	/**
	 * absolute path + file name + extension
	 * ex) C:\\Users\\user\\Downloads\\image.jpg
	 */
	private String source;
	
	/**
	 * image meta info (exif)
	 * ex) {"Create Date": "2020:02:08 07:24:32", "Date/Time Original": "2020:02:08 07:24:32"}
	 */
	private Map<String, String> exifInfos;
	
	private LocalDateTime localDateTime;
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public Map<String, String> getExifInfos() {
		return exifInfos;
	}
	
	public void setExifInfos(Map<String, String> exifInfos) {
		this.exifInfos = exifInfos;
	}
	
	public String getTakenDate() {
		return exifInfos != null ? exifInfos.get(TAKEN_DATE) : null;
	}
	
	public String getFileModificationDate() {
		return exifInfos != null ? exifInfos.get(FILE_MODIFICATION_DATE) : null;
	}
	
	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}
	
	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}
	
	@Override
	public String toString() {
		return source + "\n" + exifInfos == null ? null : exifInfos.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining("\n"));
	}
}
