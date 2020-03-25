package src.main.execute;
import java.util.List;
import java.util.logging.Logger;

import src.main.model.Photo;
import src.main.model.Result;
import src.main.service.ExifToolService;
import src.main.util.CustomLogger;

public class ExecuteReadPhotoMetaInfo {
	private static final Logger log = CustomLogger.getGlobal();
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	/**
	 * print meta infomations of photo or photos
	 */
	public static void main(String[] args) {
		// photo file or directory path
		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\TEST";
		
		try {
			List<Photo> photos = exifToolService.getPhotos(source, new Result());
			for(Photo photo : photos) {
				log.info(photo.toString());
			}
		} catch(Exception e) {
			log.severe(e.toString());
		}
	}
}
