package src.test.service;

import java.util.logging.Logger;

import org.junit.Test;

import src.main.comm.CustomLogger;
import src.main.service.PhotoService;

public class PhotoServiceTester {
	private static final Logger log = CustomLogger.getGlobal();
	public static PhotoService photoService = PhotoService.getInstance();
	
	@Test
	public void renamePhotos() {
		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\test";
		long start = System.currentTimeMillis();
		photoService.renamePhotos(source, true);
		log.info("running time: " + (System.currentTimeMillis() - start));
	}
	
	@Test
	public void updatePhotos() {
		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\test";
		long start = System.currentTimeMillis();
		photoService.updatePhotos(source);
		log.info("running time: " + (System.currentTimeMillis() - start));
	}
}
