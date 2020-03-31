package src.main.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import src.main.comm.Constants;
import src.main.comm.CustomLogger;
import src.main.model.RenamePhotoInfo;
import src.main.model.Photo;
import src.main.util.Utils;

public class ExifToolService {
	private static final Logger log = CustomLogger.getGlobal();
	
	// exiftool.exe path
	private final String appSource = "C:\\exiftool.exe";
	private final String charset = "EUC-KR";
	
	private ExifToolService() {
	}
	
	private static class Loader {
		public static final ExifToolService INSTANCE = new ExifToolService();
	}
	
	public static ExifToolService getInstance() {
		return Loader.INSTANCE;
	}
	
	/**
	 * print meta infomations of photo
	 */
	public Photo getPhoto(String source, RenamePhotoInfo info) throws IOException, InterruptedException {
		File file = new File(source);
		if(!file.isFile()) {
			log.severe("is directory, source: " + source);
			return null;
		}
		
		List<Photo> photos = getPhotos(source, info);
		if(photos == null || photos.size() != 1) {
			log.severe("photos is empty or more, source: " + source);
			return null;
		}
		
		return photos.get(0);
	}
	
	/**
	 * print meta infomations of photo or photos
	 */
	public List<Photo> getPhotos(String source, RenamePhotoInfo info) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(appSource, source);
		Process process = processBuilder.start();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
		
		File file = new File(source);
		List<Photo> photos = new LinkedList<>();
		
		String line;
		Photo photo = null;
		Map<String, String> exifInfos = null;
		if(file.isFile()) {
			photo = new Photo();
			photo.setSource(source);
			exifInfos = new HashMap<>();
			photo.setExifInfos(exifInfos);
			photos.add(photo);
		}
		while((line = stdInput.readLine()) != null) {
			int divi = line.indexOf("========");
			if(divi >= 0 && !file.isFile()) {
				photo = new Photo();
				
				String photoSource = line.substring(divi + 8).trim();
				if(info != null) {
					info.addReadPhotoCount();
					log.info(info.getLog("reading... -", "- " + Utils.skipDir(photoSource, info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
				}
				
				photo.setSource(photoSource);
				
				exifInfos = new HashMap<>();
				photo.setExifInfos(exifInfos);
				
				photos.add(photo);
			}
			
			if(divi < 0) {
				divi = line.indexOf(":");
				if(divi >= 0) {
//					exifInfos.putIfAbsent(line.substring(0, divi).trim(), line.substring(divi + 1).trim());
					exifInfos.put(line.substring(0, divi).trim(), line.substring(divi + 1).trim());
				}
			}
		}
		
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while((line = stdError.readLine()) != null) {
			int divi = line.indexOf(":");
			if(divi != -1 && !line.substring(0, divi).trim().equals("Warning")) {
				log.severe(line);
			}
		}
		
//		process.waitFor();
		return photos;
	}
}