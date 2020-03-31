package src.main.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import src.main.comm.Constants;
import src.main.comm.CustomLogger;
import src.main.model.RenamePhotoInfo;
import src.main.model.UpdatePhotoInfo;
import src.main.model.Photo;
import src.main.util.Utils;

public class PhotoService {
	private static final Logger log = CustomLogger.getGlobal();
	
	private PhotoService() {
	}
	
	private static class Loader {
		public static final PhotoService INSTANCE = new PhotoService();
	}
	
	public static PhotoService getInstance() {
		return Loader.INSTANCE;
	}
	
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	public static FileService fileService = FileService.getInstance();
	
	/**
	 * rename the photo's file name to specified format
	 * target is photo or all photos in a folder, inside folder...
	 * ex) yyyyMMdd-HHmmss-0001-originName.jpg (taken date)
	 * 
	 * @param source
	 * photo or directory path
	 * @param numbering
	 * numbering or not
	 */
	public void renamePhotos(String source, boolean numbering) {
		long start = System.currentTimeMillis();
		
		RenamePhotoInfo info = new RenamePhotoInfo();
		info.setNumbering(numbering);
		info.setPhotosMap(new HashMap<String, List<Photo>>());
		info.setDuplicatedSources(new ArrayList<String>());
		
		File file = new File(source);
		info.setRootDirectory(file.isFile() ? file.getParent() : file.getPath());
		
		try {
			collectPhotos(source, info);
			log.info(info.getLog("#####read and collet completed.#####", "#####read and collet completed.#####\n"));
			if(info.getErrorMessage() == null) {
				renamePhotos(info);
				log.info(info.getLog("#####rename completed.#####", "#####rename completed.#####\n"));
			}
			
			if(info.getErrorMessage() != null) {
				log.severe(info.getErrorMessage());
			}
		} catch(Exception e) {
			log.severe(e.toString());
			e.printStackTrace();
		}
		
		log.info("renamePhotos run-time: " + (System.currentTimeMillis() - start) + "ms");
		
		for(String src : info.getDuplicatedSources()) {
			log.warning("duplicated. " + Utils.skipDir(src, info.getRootDirectory(), Constants.ROOT_DIRECTORY));
		}
	}
	
	/**
	 * collect photo or photos of sub directories
	 * 
	 * @param info
	 */
	private void collectPhotos(String source, RenamePhotoInfo info) {
		File target = new File(source);
		
		// photo
		if(target.isFile()) {
			Photo photo = null;
			try {
				photo = exifToolService.getPhoto(target.getPath(), info);
			} catch(IOException | InterruptedException e) {
				log.severe("failed read metadata: " + target.getPath() + "\n" + e.toString());
				info.setErrorMessage("failed read metadata. src: " + target.getPath());
				return;
			}
			
			String photosKey = target.getParent();
			// if root directory
			if(photosKey == null) {
				photosKey = Constants.ROOT_DIRECTORY;
			}
			
			List<Photo> collectedPhotos = info.getPhotosMap().computeIfAbsent(photosKey, key -> new LinkedList<>());
			parseInfo(photo);
			collectedPhotos.add(photo);
			
			info.addCollectPhotoCount();
			log.info(info.getLog("collected. -", "- " + Utils.skipDir(target.getPath(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
			
			return;
		}
		
		
		// folder
		info.addReadDirectoryCount();
		log.info(info.getLog("reading... -", "- " + Utils.skipDir(target.getPath(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
		
		List<Photo> photos = null;
		try {
			photos = exifToolService.getPhotos(target.getPath(), info);
		} catch(IOException | InterruptedException e) {
			log.severe("failed read metadata: " + target.getPath() + "\n" + e.toString());
			info.setErrorMessage("failed read metadata. src: " + target.getPath());
			return;
		}
		
		List<Photo> collectedPhotos = info.getPhotosMap().computeIfAbsent(target.getPath(), key -> new LinkedList<>());
		
		// parse data
		for(Photo photo : photos) {
			parseInfo(photo);
			collectedPhotos.add(photo);
			
			info.addCollectPhotoCount();
			log.info(info.getLog("collected. -", "- " + Utils.skipDir(photo.getSource(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
		}
		
		// check sub folder
		for(File subFile : target.listFiles()) {
			if(!subFile.isFile()) {
				collectPhotos(subFile.getPath(), info);
			}
		}
	}
	
	/**
	 * parse photo meta data
	 * 
	 * @param photo
	 */
	private void parseInfo(Photo photo) {
		File file = new File(photo.getSource());
		int pos = file.getName().lastIndexOf(".");
		String name = file.getName().substring(0, pos);
		
		LocalDateTime localDateTime = null;
		ZonedDateTime zonedDateTime = null;
		if(photo.getExifInfo(Photo.DATETIME_ORIGINAL) != null) {
			zonedDateTime = ZonedDateTime.parse(photo.getExifInfo(Photo.DATETIME_ORIGINAL), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSSXXX"));
			localDateTime = zonedDateTime.toLocalDateTime();
		} else if(photo.getExifInfo(Photo.CREATE_DATE) != null) {
			zonedDateTime = ZonedDateTime.parse(photo.getExifInfo(Photo.CREATE_DATE), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSSXXX"));
			localDateTime = zonedDateTime.toLocalDateTime();
		} else if(name.startsWith("P") || name.startsWith("V")) {
			// naver cloud format
			String dateStr = name.substring(1, 19);
			localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS"));
			// zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Seoul"));
		} else if(name.length() >= 19 && name.indexOf("-") == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15) {
			// origin photoSync format
			localDateTime = LocalDateTime.parse(name.substring(0, 19), DateTimeFormatter.ofPattern("yyyyMMdd" + Constants.NAME_SEPARATOR + "HHmmss" + Constants.MILLISEC_SEPARATOR + "SSS"));
		} else if(name.length() >= 24 && name.indexOf(Constants.NAME_SEPARATOR) == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15
				&& (name.indexOf("+") == 19 || name.indexOf("-") == 19)) {
			// photoSync format
			localDateTime = LocalDateTime.parse(name.substring(0, 19), DateTimeFormatter.ofPattern("yyyyMMdd" + Constants.NAME_SEPARATOR + "HHmmss" + Constants.MILLISEC_SEPARATOR + "SSS"));
		} else if(name.length() >= 19 && name.indexOf(Constants.NAME_SEPARATOR) == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15) {
			// photoSync format
			localDateTime = LocalDateTime.parse(name.substring(0, 19), DateTimeFormatter.ofPattern("yyyyMMdd" + Constants.NAME_SEPARATOR + "HHmmss" + Constants.MILLISEC_SEPARATOR + "SSS"));
		} else if(photo.getExifInfo(Photo.FILE_MODIFICATION_DATE) != null) {
			zonedDateTime = ZonedDateTime.parse(photo.getExifInfo(Photo.FILE_MODIFICATION_DATE), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssXXX"));
			localDateTime = zonedDateTime.toLocalDateTime();
		}
		
		if(localDateTime == null) {
			log.severe(photo.getSource());
			throw new NullPointerException();
		}
		
		if(zonedDateTime == null) {
			if(photo.getExifInfo(Photo.FILE_MODIFICATION_DATE) != null) {
				ZoneId zoneId = ZonedDateTime.parse(photo.getExifInfo(Photo.FILE_MODIFICATION_DATE), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssXXX")).getZone();
				zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
			}
		}
		
		photo.setLocalDateTime(localDateTime);
		photo.setZonedDateTime(zonedDateTime);
	}
	
	/**
	 * rename photos
	 * before call, collectPhotos() call required
	 * 
	 * @param info
	 */
	private void renamePhotos(RenamePhotoInfo info) {
		// check duplication, counting grouping
		Map<String, Long> newNameCounts = info.getPhotosMap().values().stream()
				.flatMap(photos -> photos.stream())
				.collect(Collectors.groupingBy(photo -> getNewName(photo, null, false), Collectors.counting()));
		
		for(List<Photo> photos : info.getPhotosMap().values()) {
			// sort for numbering
			photos.sort(Comparator.comparing(Photo::getLocalDateTime, Comparator.nullsLast(Comparator.naturalOrder()))
					.thenComparing(photo -> photo.getSource().length())
					.thenComparing(photo -> photo.getSource())
					);
			
			int number = 1;
			for(Photo photo : photos) {
				try {
					File originFile = new File(photo.getSource());
					
					String uniqueNewName = getNewName(photo, null, false);
					
					boolean duplication = newNameCounts.get(uniqueNewName) > 1;
					String newName = getNewName(photo, info.isNumbering() ? number++ : null, duplication);
					
					// rename
					String newSource = fileService.renameFile(photo.getSource(), newName);
					photo.setSource(newSource);
					
					info.addCompletedPhotoCount();
					
					File newFile = new File(photo.getSource());
					log.info(info.getLog("renamed. -", "- " + Utils.skipDir(originFile.getParent(), info.getRootDirectory(), Constants.ROOT_DIRECTORY) + " - " + originFile.getName() +  " -> " + newFile.getName()));
					
					if(duplication) {
						info.getDuplicatedSources().add(photo.getSource());
					}
					
				} catch(IOException e) {
					log.severe("failed rename: " + photo.getSource() + "\n" + e.toString());
					info.setErrorMessage("failed rename: " + photo.getSource());
					return;
				}
			}
		}
	}
	
	/**
	 * get new file name
	 * 
	 * @param photo
	 * @param number
	 * nullable 00000
	 * @param duplication
	 * if duplication append orignal file name
	 * @return
	 */
	private String getNewName(Photo photo, Integer number, boolean duplication) {
		File file = new File(photo.getSource());
		int pos = file.getName().lastIndexOf(".");
		String name = file.getName().substring(0, pos);
		String extension = file.getName().substring(pos + 1);
		
		ZonedDateTime dateTime = photo.getZonedDateTime();
		// set +00:00
		if(dateTime == null) {
			dateTime = ZonedDateTime.of(photo.getLocalDateTime(), ZoneOffset.UTC);
		}
		String date = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String time = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		String ms = dateTime.format(DateTimeFormatter.ofPattern("SSS"));
		String offset = dateTime.format(DateTimeFormatter.ofPattern("xxxx"));
		
		
		StringBuilder newName = new StringBuilder();
		
		// yyyyMMdd_HHmmss.SSS_+0900_00001_00001.jpg
		newName.append(date);
		newName.append(Constants.NAME_SEPARATOR).append(time).append(Constants.MILLISEC_SEPARATOR).append(ms);
		newName.append(Constants.NAME_SEPARATOR).append(offset);
		newName.append(Constants.NAME_SEPARATOR).append("00001");
		newName.append(Constants.NAME_SEPARATOR).append(number == null ? "00000" : Utils.lPad(String.valueOf(number), 5, "0"));
		if(duplication) {
			newName.append(Constants.NAME_SEPARATOR).append(name);
		}
		newName.append(".").append(extension);
		
		// yyyyMMdd-HHmmss-0001-image.jpg
//		newName = date + "-" + time + (numbering == null ? "" : "-" + numbering) + "-" + file.getName();
		// yyyyMMdd-HHmmss-0001.jpg
//		newName = date + "-" + time + (numbering == null ? "" : "-" + numbering + "." + extension);
		
		return newName.toString();
	}
	
	public void updatePhotos(String source) {
		long start = System.currentTimeMillis();
		
		UpdatePhotoInfo info = new UpdatePhotoInfo();
		
		File file = new File(source);
		info.setRootDirectory(file.isFile() ? file.getParent() : file.getPath());
		info.setFailedPhotoSources(new ArrayList<>());
		
		try {
//			updatePhotos(source, info);
			log.info(info.getLog("#####updatePhotos completed.#####", "#####updatePhotos completed.#####\n"));
		} catch(Exception e) {
			log.severe(e.toString());
			e.printStackTrace();
		}
		
		log.info("updatePhotos run-time: " + (System.currentTimeMillis() - start) + "ms");
		
		for(String src : info.getFailedPhotoSources()) {
			log.severe("failed. " + Utils.skipDir(src, info.getRootDirectory(), Constants.ROOT_DIRECTORY));
		}
	}
	
//	private void updatePhotos(String source, UpdatePhotoInfo info) {
//		File target = new File(source);
//		
//		// photo
//		if(target.isFile()) {
//			Photo photo = null;
//			try {
//				photo = exifToolService.getPhoto(target.getPath(), null);
//				parseInfo(photo);
//				
//				
//				LocalDateTime localDateTime = LocalDateTime.parse(target.getName(), DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss.SSS"));
//				ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneOffset.of);
//				
//				photo = exifToolService.getPhoto(target.getPath(), info);
//			} catch(IOException | InterruptedException e) {
//				log.severe("failed read metadata: " + target.getPath() + "\n" + e.toString());
//				return;
//			}
//			
//			String photosKey = target.getParent();
//			// if root directory
//			if(photosKey == null) {
//				photosKey = Constants.ROOT_DIRECTORY;
//			}
//			
//			List<Photo> collectedPhotos = info.getPhotosMap().computeIfAbsent(photosKey, key -> new LinkedList<>());
//			parseInfo(photo);
//			collectedPhotos.add(photo);
//			
//			info.addCollectPhotoCount();
//			log.info(info.getLog("collected. -", "- " + Utils.skipDir(target.getPath(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
//			
//			return;
//		}
//		
//		
//		// folder
//		info.addReadDirectoryCount();
//		log.info(info.getLog("reading... -", "- " + Utils.skipDir(target.getPath(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
//		
//		List<Photo> photos = null;
//		try {
//			photos = exifToolService.getPhotos(target.getPath(), info);
//		} catch(IOException | InterruptedException e) {
//			log.severe("failed read metadata: " + target.getPath() + "\n" + e.toString());
//			info.setErrorMessage("failed read metadata. src: " + target.getPath());
//			return;
//		}
//		
//		List<Photo> collectedPhotos = info.getPhotosMap().computeIfAbsent(target.getPath(), key -> new LinkedList<>());
//		
//		// parse data
//		for(Photo photo : photos) {
//			parseInfo(photo);
//			collectedPhotos.add(photo);
//			
//			info.addCollectPhotoCount();
//			log.info(info.getLog("collected. -", "- " + Utils.skipDir(photo.getSource(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
//		}
//		
//		// check sub folder
//		for(File subFile : target.listFiles()) {
//			if(!subFile.isFile()) {
//				collectPhotos(subFile.getPath(), info);
//			}
//		}
//	}
}