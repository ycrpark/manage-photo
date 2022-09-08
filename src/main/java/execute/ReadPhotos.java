package execute;

import comm.CustomLogger;
import model.Photo;
import service.ExifToolService;

import java.util.List;
import java.util.logging.Logger;

public class ReadPhotos {
	private static final Logger log = CustomLogger.getGlobal();
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	/**
	 * print meta infomations of photo or photos
	 */
	public static void main(String[] args) {
		// photo file or directory path
//		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\TEST";
//		String source = "C:\\Users\\pp75362\\Desktop\\새 폴더\\대상\\결혼식스냅RAW";
//		String source = "C:\\Users\\pp75362\\Desktop\\다시\\작업1\\병합";
		String source = "C:\\Users\\pp75362\\Desktop\\다시\\원본\\이유림 신부님 원본 (2) - 복사본2";

		try {
			List<Photo> photos = exifToolService.getPhotos(source, null);
			for(Photo photo : photos) {
				log.info(photo.toString());
			}
		} catch(Exception e) {
			log.severe(e.toString());
		}
	}
}
