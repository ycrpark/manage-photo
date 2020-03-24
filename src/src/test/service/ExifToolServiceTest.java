package src.test.service;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import src.main.model.Picture;
import src.main.service.ExifToolService;
import src.main.util.CustomLogger;

public class ExifToolServiceTest {
	private static final Logger log = CustomLogger.getGlobal();
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	public static void main(String[] args) {
		String path = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\새 폴더";

		File target = new File(path);
		for (File file : target.listFiles()) {
			try {
				Picture picture = exifToolService.getPicture(file.getPath());
				log.info(picture.toString());
			} catch(IOException | InterruptedException e) {
				log.severe(e.toString());
			}
		}
	}
}
