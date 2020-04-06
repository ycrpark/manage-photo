package execute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.RenamePhotoCriteria;
import service.PhotoService;

public class RenamePhotos {
	public static PhotoService photoService = PhotoService.getInstance();
	
	/**
	 * rename the photo's file name to specified format
	 * target is photo or all photos in a folder, inside folder...
	 * 
	 * yyyyMMdd-HHmmss.SSS-sequence-number.extension
	 * ex) 20200403-174504.357-00001-00001.jpg
	 * 
	 * sequence: if same datetime, sequencing start at 1
	 * number: numbering or unique number of file name
	 */
	public static void main(String[] args) {
		// photo file or directory path
		String source = "C:\\Users\\ycrpa\\Downloads\\test";
//		String source = "C:\\Users\\ycrpa\\GoogleDrive\\사진";
//		String source = "C:\\Users\\ycrpa\\Downloads\\test\\20191016_155250.010_00001_00000.CR2";
		
		RenamePhotoCriteria criteria = new RenamePhotoCriteria();
		// auto sequencing
		// if false, append original file name to the rename file name
		criteria.setAutoSequence(true);
		
		// auto numbering by folder. if false, number is 00000
		criteria.setNumbering(true);
		
		// override new numbering folder names
//		criteria.setRenumberingDirectories(Arrays.asList("곰"));
		
		// append original file name at rename file name
//		criteria.setAppendOriginal(true);
		
		// If a folder's suffix contains list, integrated numbering with the folder excluding the suffix.
		// ex) if Arrays.asList(" etc"), each proceeds like a single folder {["apple", "apple etc"], ["picture", "picture etc"]}
		criteria.setMergeDirectorySuffixes(Arrays.asList("기타"));
		
		// if folder name contains value list and file name's prefix is key, using numbering
		// if value list is null, target is all files
		// ex) if key is "IMG_", "IMG_0032.jpg" -> 20200403-174504.357-00001-00032.jpg
		// ex) if key is "IMG_", "IMG_0032-abc.jpg" -> 20200403-174504.357-00001-00032-abc.jpg
		Map<String, List<String>> maintainsNumberingDirectories = new HashMap<>();
		maintainsNumberingDirectories.put("KAWA", null);
		maintainsNumberingDirectories.put("IMG_", Arrays.asList("동기스튜디오", "동기스튜디오 보정"));
		criteria.setMaintainsNumberingDirectories(maintainsNumberingDirectories);
		
		// if true, file name is not changed
		criteria.setTest(true);
		
		photoService.renamePhotos(source, criteria);
	}
}
