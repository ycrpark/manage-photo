package src.main.execute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.main.model.RenamePhotoCriteria;
import src.main.service.PhotoService;

public class RenamePhotos {
	public static PhotoService photoService = PhotoService.getInstance();
	
	/**
	 * rename the photo's file name to specified format
	 * target is photo or all photos in a folder, inside folder...
	 * ex) yyyyMMdd-HHmmss.SSS-00001-00001.jpg
	 */
	public static void main(String[] args) {
		// photo file or directory path
		String source = "C:\\Users\\ycrpa\\Downloads\\test";
//		String source = "C:\\Users\\ycrpa\\GoogleDrive\\사진";
//		String source = "C:\\Users\\ycrpa\\Downloads\\test\\20191016_155250.010_00001_00000.CR2";
		
		RenamePhotoCriteria criteria = new RenamePhotoCriteria();
		criteria.setAutoSequence(true);
		criteria.setNumbering(true);
//		criteria.setAppendOriginal(true);
		criteria.setMergeDirectirySuffixes(Arrays.asList("기타"));
		
		Map<String, List<String>> maintainsNumberingDirectories = new HashMap<>();
		maintainsNumberingDirectories.put("KAWA", null);
		maintainsNumberingDirectories.put("IMG_", Arrays.asList("동기스튜디오", "동기스튜디오 보정"));
		criteria.setMaintainsNumberingDirectories(maintainsNumberingDirectories);
		
//		criteria.setTest(true);
		photoService.renamePhotos(source, criteria);
	}
}
