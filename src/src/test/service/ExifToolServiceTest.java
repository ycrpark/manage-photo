package src.test.service;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import src.main.model.Photo;
import src.main.service.ExifToolService;
import src.main.util.CustomLogger;

public class ExifToolServiceTest {
	private static final Logger log = CustomLogger.getGlobal();
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	public static void main(String[] args) {
		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\TEST";

		File target = new File(source);
		for (File file : target.listFiles()) {
			try {
				Photo photo = exifToolService.getPhoto(file.getPath(), null);
				//log.info(photo.toString());
			} catch(IOException | InterruptedException e) {
				log.severe(e.toString());
			}
		}
	}
}
