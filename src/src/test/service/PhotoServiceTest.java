package src.test.service;
import java.util.logging.Logger;

import src.main.service.PhotoService;
import src.main.util.CustomLogger;

public class PhotoServiceTest {
	private static final Logger log = CustomLogger.getGlobal();
	public static PhotoService photoService = PhotoService.getInstance();
	
	public static void main(String[] args) {
		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\test";
		long start = System.currentTimeMillis();
		photoService.renamePhotos(source);
		log.info("running time: " + (System.currentTimeMillis() - start));
	}
}
