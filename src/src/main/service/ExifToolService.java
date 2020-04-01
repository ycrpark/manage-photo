package src.main.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import src.main.comm.Constants;
import src.main.comm.CustomLogger;
import src.main.model.Photo;
import src.main.model.RenamePhotoInfo;
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
		
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
		
//		process.waitFor();
		
		while((line = reader.readLine()) != null) {
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
		
		if(file.isFile() && info != null) {
			info.addReadPhotoCount();
		}
		
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while((line = errorReader.readLine()) != null) {
			int divi = line.indexOf(":");
			if(divi != -1 && !line.substring(0, divi).trim().equals("Warning")) {
				log.severe(line);
			}
		}
		
		reader.close();
		errorReader.close();
		process.destroy();
		return photos;
	}
	
	/**
	 * override exif meta info
	 * 
	 * @return
	 * success or not
	 */
	public boolean updateDate(String source, ZonedDateTime zonedDateTime, LocalDateTime localDateTime) throws IOException, InterruptedException {
		List<String> commands = new ArrayList<>();
		commands.add(appSource);
		// do not create copies
		commands.add("-overwrite_original");
		
		String dateTime = null;
		if(zonedDateTime != null) {
			dateTime = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSSXXX"));
		} else {
			dateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSS"));
		}
		
		// TODO exist not supported datetimeoriginal
		List<String> dateCommands = Arrays.asList("datetimeoriginal", "CreateDate", "ModifyDate", "FileCreateDate", "FileModifyDate");
		for(String dateCommand : dateCommands) {
			commands.add("\"-" + dateCommand + "=" + dateTime + "\"");
//			commands.add("\"-" + dateCommand + "=");
//			commands.add(dateTime);
//			commands.add("\"");
		}
		
		commands.add(source);
		
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		Process process = processBuilder.start();
		
		boolean success = true;
		String line;
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), charset));
		while ((line = errorReader.readLine()) != null) {
			int pos = line.indexOf(":");
			if (pos != -1 && !line.substring(0, pos).trim().equals("Warning")) {
				log.severe(line);
				success = false;
			}
		}
		
		errorReader.close();
		process.destroy();
		return success;
	}
}