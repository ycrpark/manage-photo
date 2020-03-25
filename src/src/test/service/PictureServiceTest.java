package src.test.service;
import java.util.logging.Logger;

import src.main.service.PictureService;
import src.main.util.CustomLogger;

public class PictureServiceTest {
	private static final Logger log = CustomLogger.getGlobal();
	public static PictureService pictureService = PictureService.getInstance();
	
	public static void main(String[] args) {
		String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\TEST";
		
		pictureService.changePictureName(source);
	}
}
