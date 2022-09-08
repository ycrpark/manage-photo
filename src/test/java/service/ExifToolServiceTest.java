package service;

import comm.CustomLogger;
import model.Photo;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

public class ExifToolServiceTest {
	private static final Logger log = CustomLogger.getGlobal();
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	@Test
	public void getPhoto() {
		
		String source = "C:\\Users\\ycrpa\\Downloads\\test";
		
		File target = new File(source);
		for(File file : target.listFiles()) {
			try {
				Photo photo = exifToolService.getPhoto(file.getPath(), null);
				log.info(photo.toString());
			} catch(IOException | InterruptedException e) {
				log.severe(e.toString());
				fail();
			}
		}
		
	}
	
	@Test
	public void getPhotos() {
		
		String source = "C:\\Users\\ycrpa\\Downloads\\test\\move2";
		try {
			List<Photo> photos = exifToolService.getPhotos(source, null);
			log.info(photos.toString());
		} catch(IOException | InterruptedException e) {
			log.severe(e.toString());
			fail();
		}
	}
	
	@Test
	public void setDate() {
		String source = "C:\\Users\\ycrpa\\Downloads\\test\\move2\\P20200204_224328541_E1A31B3C-CB5E-49CE-82CA-54F13A0510B5.JPG";
		ZonedDateTime zonedDateTime = ZonedDateTime.parse("20201010131315 333+04:00", DateTimeFormatter.ofPattern("yyyyMMddHHmmss SSSXXX"));
		try {
			boolean success = exifToolService.updateDate(source, zonedDateTime, zonedDateTime.toLocalDateTime());
			if(!success) {
				fail();
			}
		} catch(IOException | InterruptedException e) {
			fail();
		}
	}
}
