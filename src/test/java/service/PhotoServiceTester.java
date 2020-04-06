package service;

import java.util.logging.Logger;

import org.junit.Test;

import comm.CustomLogger;
import model.RenamePhotoCriteria;

public class PhotoServiceTester {
	private static final Logger log = CustomLogger.getGlobal();
	public static PhotoService photoService = PhotoService.getInstance();
	
	@Test
	public void renamePhotos() {
		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\test";
		long start = System.currentTimeMillis();
		
		RenamePhotoCriteria criteria = new RenamePhotoCriteria();
		criteria.setAutoSequence(true);
//		criteria.setNumbering(true);
//		criteria.setAppendOriginal(true);
		
		photoService.renamePhotos(source, criteria);
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
