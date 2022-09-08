package service;

import comm.CustomLogger;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

public class FileServiceTest {
	private static final Logger log = CustomLogger.getGlobal();
	public static FileService fileService = FileService.getInstance();
	
	@Test
	public void moveFilesFromFolder() {
		String source = "C:\\Users\\ycrpa\\Downloads\\test\\move";
		String movePath = "C:\\Users\\ycrpa\\Downloads\\test\\move2";
		fileService.moveFiles(source, movePath);
	}
	
	@Test
	public void renameFile() {
		
		String source2 = "C:\\Users\\ycrpa\\Downloads\\test\\move\\a.jpg";
		String newName = "b";
		try {
			fileService.renameFile(source2, newName);
		} catch(IOException e) {
			log.severe(e.toString());
			e.printStackTrace();
		}
	}
	
	@Test
	public void moveFilesFromFile() {
		String source3 = "C:\\Users\\ycrpa\\Downloads\\test\\move\\a.jpg";
		String movePath2 = "C:\\Users\\ycrpa\\Downloads\\test\\move2";
		fileService.moveFiles(source3, movePath2);
	}
}
