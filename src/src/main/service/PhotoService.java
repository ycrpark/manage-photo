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
import src.main.util.Utils;

public class PhotoService {
	private static final Logger log = Logger.getGlobal();
	
	private static final String ROOT_DIRECTORY = "ROOT";
	
	private PhotoService() {
	}
	
	private static class Loader {
		public static final PhotoService INSTANCE = new PhotoService();
	}
	
	public static PhotoService getInstance() {
		return Loader.INSTANCE;
	}
	
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	/**
	 * rename the photo's file name to specified format
	 * target is photo or all photos in a folder, inside folder...
	 * ex) yyyyMMdd-HHmmss-0001-originName.jpg (taken date)
	 */
	public void renamePhotos(String source) {
		long start = System.currentTimeMillis();
		Result result = new Result();
		
		File file = new File(source);
		result.setRootDirectory(file.isFile() ? file.getParent() : file.getPath());
		Map<String, List<Photo>> photosMap = new HashMap<>();
		
		try {
			collectPhotos(source, result, photosMap);
			log.info(result.getLog("#####read and collet completed.#####", "#####read and collet completed.#####\n"));
			if(result.getErrorMessage() == null) {
				renamePhotos(result, photosMap);
				log.info(result.getLog("#####rename completed.#####", "#####rename completed.#####\n"));
			}
			
			if(result.getErrorMessage() != null) {
				log.severe(result.getErrorMessage());
			}
		} catch(Exception e) {
			log.severe(e.toString());
		}
		
		log.info("renamePhotos run-time: " + (System.currentTimeMillis() - start) + "ms");
	}
	
	/**
	 * collect photo or photos of sub directories
	 * 
	 * @param source photo or directory path
	 * @param result contains collect info
	 * @param photosMap collect photo at photoMap
	 */
	private void collectPhotos(String source, Result result, Map<String, List<Photo>> photosMap) {
		File target = new File(source);
		
		// photo
		if(target.isFile()) {
			Photo photo = null;
			try {
				photo = exifToolService.getPhoto(target.getPath(), result);
			} catch(IOException | InterruptedException e) {
				log.severe("failed read metadata: " + target.getPath() + "\n" + e.toString());
				result.setErrorMessage("failed read metadata. src: " + target.getPath());
				return;
			}
			
			String photosKey = target.getParent();
			// if root directory
			if(photosKey == null) {
				photosKey = ROOT_DIRECTORY;
			}
			
			List<Photo> collectedPhotos = photosMap.computeIfAbsent(photosKey, key -> new LinkedList<>());
			collectedPhotos.add(photo);
			result.addCollectPhotoCount();
			log.info(result.getLog("collected. -", "- " + Utils.skipDir(target.getPath(), result.getRootDirectory(), ROOT_DIRECTORY)));
			
			return;
		}
		
		
		// folder
		result.addReadDirectoryCount();
		log.info(result.getLog("reading... -", "- " + Utils.skipDir(target.getPath(), result.getRootDirectory(), ROOT_DIRECTORY)));
		
		List<Photo> photos = null;
		try {
			photos = exifToolService.getPhotos(target.getPath(), result);
		} catch(IOException | InterruptedException e) {
			log.severe("failed read metadata: " + target.getPath() + "\n" + e.toString());
			result.setErrorMessage("failed read metadata. src: " + target.getPath());
			return;
		}
		
		List<Photo> collectedPhotos = photosMap.computeIfAbsent(target.getPath(), key -> new LinkedList<>());
		collectedPhotos.addAll(photos);
		
		// for logging
		for(Photo photo : photos) {
			result.addCollectPhotoCount();
			log.info(result.getLog("collected. -", "- " + Utils.skipDir(photo.getSource(), result.getRootDirectory(), ROOT_DIRECTORY)));
		}
		
		// check sub folder
		for(File subFile : target.listFiles()) {
			if(!subFile.isFile()) {
				collectPhotos(subFile.getPath(), result, photosMap);
			}
		}
	}
	
	/**
	 * rename photos
	 * before call, collectPhotos() call required for numbering
	 * 
	 * @param result contains collect info
	 * @param photosMap targets rename
	 */
	private void renamePhotos(Result result, Map<String, List<Photo>> photosMap) {
		for(List<Photo> photos : photosMap.values()) {
			photos.sort(Comparator.comparing(Photo::getTakenDate, Comparator.nullsLast(Comparator.naturalOrder()))
					.thenComparing(photo -> photo.getSource().length())
					.thenComparing(photo -> photo.getSource())
					);
			
			int number = 1;
			for(Photo photo : photos) {
				try {
					File originFile = new File(photo.getSource());
					
					renamePhoto(photo, number++);
					result.addCompletedPhotoCount();
					
					File newFile = new File(photo.getSource());
					
					log.info(result.getLog("renamed. -", "- " + Utils.skipDir(originFile.getParent(), result.getRootDirectory(), ROOT_DIRECTORY) + " - " + originFile.getName() +  " -> " + newFile.getName()));
				} catch(IOException e) {
					log.severe("failed rename: " + photo.getSource() + "\n" + e.toString());
					result.setErrorMessage("failed rename: " + photo.getSource());
					return;
				}
			}
		}
	}
	
	/**
	 * rename of the photo
	 * yyyyMMdd-HHmmss-0001-image.jpg
	 * yyyyMMdd-HHmmss-image.jpg if number is null
	 * 
	 * @param photo
	 * @param number nullable
	 * @throws IOException
	 */
	public void renamePhoto(Photo photo, Integer number) throws IOException {
		if(photo == null) {
			return;
		}
		
		LocalDateTime dateTime = LocalDateTime.parse(photo.getTakenDate(), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
		String date = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String time = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		
		File file = new File(photo.getSource());
		int pos = file.getName().lastIndexOf(".");
		String name = file.getName().substring(0, pos);
		String extension = file.getName().substring(pos + 1);
		
		String numbering = null;
		if(number != null) {
			numbering = String.valueOf(number);
			for(int i = numbering.length(); i < 4; i++) {
				numbering = "0" + numbering;
			}
		}
		
		String newName = null;
		
		// yyyyMMdd-HHmmss-0001-image.jpg
		newName = date + "-" + time + (numbering == null ? "" : "-" + numbering) + "-" + file.getName();
		// yyyyMMdd-HHmmss-0001.jpg
//		newName = date + "-" + time + (numbering == null ? "" : "-" + numbering + "." + extension);
		
		Path path = Paths.get(photo.getSource());
		Files.move(path, path.resolveSibling(newName));
		
		file = new File(file.getParent() + "\\" + newName);
		photo.setSource(file.getPath());
	}
}