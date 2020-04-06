package execute;
import java.util.List;
import java.util.logging.Logger;

import comm.CustomLogger;
import model.Photo;
import service.ExifToolService;

public class ReadPhotos {
	private static final Logger log = CustomLogger.getGlobal();
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	/**
	 * print meta infomations of photo or photos
	 */
	public static void main(String[] args) {
		// photo file or directory path
		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\TEST";
		
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
