package src.main.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import src.main.model.Picture;

public class ExifToolService {
	private static final Logger log = Logger.getGlobal();
	
	// exiftool.exe path
	private final String appPath = "C:\\exiftool.exe";
	
	private ExifToolService() {
	}
	
	private static class Loader {
		public static final ExifToolService INSTANCE = new ExifToolService();
	}
	
	public static ExifToolService getInstance() {
		return Loader.INSTANCE;
	}

	public Picture getPicture(String path) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(appPath, path);
		Process process = processBuilder.start();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		Picture picture = new Picture();
		
		Map<String, String> exifInfos = new HashMap<>();
		String line;
		while((line = stdInput.readLine()) != null) {
			int divi = line.indexOf(":");
			if(divi != -1) {
				exifInfos.put(line.substring(0, divi).trim(), line.substring(divi + 1).trim());
			}
			log.info(line);
		}
		picture.setExifInfos(exifInfos);
		
		while((line = stdError.readLine()) != null) {
			int divi = line.indexOf(":");
			if(divi != -1 && !line.substring(0, divi).trim().equals("Warning")) {
				log.warning(line);
			}
		}
		
		process.waitFor();
		return picture;
	}
}