package src.main.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import src.main.model.Photo;
import src.main.model.Result;

public class PhotoService {
	private static final Logger log = Logger.getGlobal();
	
	private static final String ROOT_DIRECTORY_KEY = "ROOT";
	
	private PhotoService() {
	}
	
	private static class Loader {
		public static final PhotoService INSTANCE = new PhotoService();
	}
	
	public static PhotoService getInstance() {
		return Loader.INSTANCE;
	}
	
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	public void changePhotoName(String source) {
		Result result = new Result();
		Map<String, List<Photo>> photosMap = new HashMap<>();
		
		try {
			collectPhotos(source, result, photosMap);
			log.info("#####collect completed, directoryCount: " + result.getCollectDirectoryCount() + ", photoCount: " + result.getCollectPhotoCount());
			if(result.getErrorMessage() == null) {
				changeCollectedPhotoName(result, photosMap);
				log.info("#####changeName completed, photoCount: " + result.getCompletedPhotoCount() + " / " + result.getCollectPhotoCount());
			}
			
			if(result.getErrorMessage() != null) {
				log.severe(result.getErrorMessage());
			}
		} catch(Exception e) {
			log.severe(e.toString());
		}
	}
	
	/**
	 * @param id
	 * @param source
	 * @return if there is an error, return errorMessage. Otherwise, return null.
	 */
	private void collectPhotos(String source, Result result, Map<String, List<Photo>> photosMap) {
		File target = new File(source);
		
		// folder
		if(!target.isFile()) {
			result.addCollectDirectoryCount();
			log.info("collected. directoryCount: " + result.getCollectDirectoryCount() + ", photoCount: " + result.getCollectPhotoCount() + " - " + target.getPath());
			
			for(File file : target.listFiles()) {
				collectPhotos(file.getPath(), result, photosMap);
				if(result.getErrorMessage() != null) {
					return;
				}
			}
			
			return;
		}
		
		Photo photo = null;
		try {
			photo = exifToolService.getPhoto(target.getPath());
		} catch(IOException | InterruptedException e) {
			log.severe("failed read metadata: " + target.getPath() + "\n" + e.toString());
			result.setErrorMessage("failed read metadata. src: " + target.getPath());
			return;
		}
		
		String photosKey = target.getParent();
		// if root directory
		if(photosKey == null) {
			photosKey = ROOT_DIRECTORY_KEY;
		}
		
		List<Photo> photos = photosMap.computeIfAbsent(photosKey, key -> new LinkedList<>());
		photos.add(photo);
		result.addCollectPhotoCount();
		log.info("collected. directoryCount: " + result.getCollectDirectoryCount() + ", photoCount: " + result.getCollectPhotoCount() + " - " + target.getPath());
	}
	
	private void changeCollectedPhotoName(Result result, Map<String, List<Photo>> photosMap) {
		for(List<Photo> photos : photosMap.values()) {
			photos.sort(Comparator.comparing(Photo::getTakenDate, Comparator.nullsLast(Comparator.naturalOrder())));
			
			int number = 1;
			for(Photo photo : photos) {
				try {
					File originFile = new File(photo.getSource());
					
					changePhotoName(photo, number++);
					result.addCompletedPhotoCount();
					
					File newFile = new File(photo.getSource());
					
					log.info("changed, photoCount: " + result.getCompletedPhotoCount() + " / " + result.getCollectPhotoCount()
							+ " - " + originFile.getParent() + " [" + originFile.getName() +  " -> " + newFile.getName() + "]");
				} catch(IOException e) {
					log.severe("failed rename: " + photo.getSource() + "\n" + e.toString());
					result.setErrorMessage("failed rename: " + photo.getSource());
					return;
				}
			}
		}
	}
	
	public void changePhotoName(Photo photo, Integer number) throws IOException {
		if(photo == null) {
			return;
		}
		
		LocalDateTime dateTime = LocalDateTime.parse(photo.getTakenDate(), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
		String date = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String time = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		
		File file = new File(photo.getSource());
//		int pos = file.getName().lastIndexOf(".");
//		String name = file.getName().substring(0, pos);
//		String extension = file.getName().substring(pos + 1);
		
		String numbering = null;
		if(number != null) {
			numbering = String.valueOf(number);
			for(int i = numbering.length(); i < 4; i++) {
				numbering = "0" + numbering;
			}
		}
		
		
		String newName = date + "-" + time + (numbering == null ? "" : "-" + numbering) + "-" + file.getName();
		
		Path path = Paths.get(photo.getSource());
		Files.move(path, path.resolveSibling(newName));
		
		file = new File(file.getParent() + "\\" + newName);
		photo.setSource(file.getPath());
	}
}