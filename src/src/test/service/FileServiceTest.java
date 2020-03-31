package src.test.service;

import java.io.IOException;
import java.util.logging.Logger;

import src.main.comm.CustomLogger;
import src.main.service.FileService;

public class FileServiceTest {
	private static final Logger log = CustomLogger.getGlobal();
	public static FileService fileService = FileService.getInstance();
	
	public static void main(String[] args) {
		String source = "C:\\Users\\ycrpa\\Downloads\\test\\move";
		String movePath = "C:\\Users\\ycrpa\\Downloads\\test\\move2";
		fileService.moveFiles(source, movePath);
		
		
		String source2 = "C:\\Users\\ycrpa\\Downloads\\test\\move\\a.jpg";
		String newName = "b";
		try {
			fileService.renameFile(source2, newName);
		} catch(IOException e) {
			log.severe(e.toString());
			e.printStackTrace();
		}
		
		String source3 = "C:\\Users\\ycrpa\\Downloads\\test\\move\\a.jpg";
		String movePath2 = "C:\\Users\\ycrpa\\Downloads\\test\\move2";
		fileService.moveFiles(source3, movePath2);
	}
}
