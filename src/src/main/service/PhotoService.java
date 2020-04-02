package src.main.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import src.main.comm.Constants;
import src.main.comm.CustomLogger;
import src.main.comm.WindowsExplorerComparator;
import src.main.model.NameCriteria;
import src.main.model.Photo;
import src.main.model.RenamePhotoCriteria;
import src.main.model.RenamePhotoInfo;
import src.main.model.UpdatePhotoInfo;
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
	public void renamePhotos(String source, RenamePhotoCriteria criteria) {
		long start = System.currentTimeMillis();
		
		RenamePhotoInfo info = new RenamePhotoInfo();
		info.setPhotosMap(new LinkedHashMap<String, List<Photo>>());
		info.setDuplicatedSources(new LinkedHashMap<String, List<String>>());
		
		File file = new File(source);
		info.setRootDirectory(file.isFile() ? file.getParent() : file.getPath());
		
		try {
			collectPhotos(source, info);
			log.info(info.getLog("#####read and collet completed.#####", "#####read and collet completed.#####\n"));
			if(info.getErrorMessage() == null) {
				renamePhotos(criteria, info);
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
		
		for(List<String> srcs : info.getDuplicatedSources().values()) {
			srcs.sort(Comparator.comparing(src -> Paths.get(src).getFileName().toString(), new WindowsExplorerComparator()));
			for(String src : srcs) {
				log.warning("duplicated. " + Utils.skipDir(src, info.getRootDirectory(), Constants.ROOT_DIRECTORY));
			}
			log.warning("");
		}
		
		if(!info.getDuplicatedSources().isEmpty()) {
			long count = info.getDuplicatedSources().values().stream().flatMap(srcs -> srcs.stream()).count();
			log.warning("#####duplicated.##### " + info.getDuplicatedSources().size() + " kinds, " + count + "items. #####duplicated.#####");
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
		Arrays.stream(target.listFiles())
				.filter(subFile -> subFile.isDirectory())
				.sorted(Comparator.comparing(subFile -> subFile.getPath(), new WindowsExplorerComparator()))
				.forEach(subFile -> collectPhotos(subFile.getPath(), info));
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
		String numbering = null;
		if(Utils.validDateText(photo.getExifInfo(Photo.DATETIME_ORIGINAL)) || Utils.validDateText(photo.getExifInfo(Photo.CREATE_DATE))) {
			String dateText = Utils.validDateText(photo.getExifInfo(Photo.DATETIME_ORIGINAL)) ? photo.getExifInfo(Photo.DATETIME_ORIGINAL) : photo.getExifInfo(Photo.CREATE_DATE);
			
			if(Utils.containsAny(dateText, "+", "-")) {
				if(!dateText.contains(".")) {
					zonedDateTime = ZonedDateTime.parse(dateText, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssXXX"));
				} else {
					zonedDateTime = ZonedDateTime.parse(dateText, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSSXXX"));
				}
				localDateTime = zonedDateTime.toLocalDateTime();
			} else {
				if(dateText.length() == 19) {
					localDateTime = LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
				} else if(dateText.length() == 21) {
					localDateTime = LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.S"));
				} else if(dateText.length() == 22) {
					localDateTime = LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SS"));
				} else if(dateText.length() == 23) {
					localDateTime = LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSS"));
				} else if(dateText.length() == 24) {
					localDateTime = LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSSS"));
				}
			}
			
			// maintains before numbering
			if(name.length() >= 37 && name.indexOf(Constants.NAME_SEPARATOR) == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15
					&& (name.indexOf("+") == 20 || name.indexOf("-") == 20)) {
				// photoSync contains offset format
				numbering = name.substring(32);
			} else if(name.length() >= 31 && name.indexOf(Constants.NAME_SEPARATOR) == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15) {
				// photoSync format
				numbering = name.substring(26);
			}
		} else if(name.startsWith("P") || name.startsWith("V")) {
			// naver cloud format
			String dateStr = name.substring(1, 19);
			localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS"));
			// zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Asia/Seoul"));
			
		} else if(name.length() >= 19 && name.indexOf("-") == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15) {
			// photoSync old version format
			localDateTime = LocalDateTime.parse(name.substring(0, 19), DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss.SSS"));
			
		} else if(name.length() >= 37 && name.indexOf(Constants.NAME_SEPARATOR) == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15
				&& (name.indexOf("+") == 20 || name.indexOf("-") == 20)) {
			// photoSync contains offset format
			zonedDateTime = ZonedDateTime.parse(name.substring(0, 25), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_CONTAINS_OFFSET));
			localDateTime = zonedDateTime.toLocalDateTime();
			// maintains before numbering
			numbering = name.substring(32);
			
		} else if(name.length() >= 31 && name.indexOf(Constants.NAME_SEPARATOR) == 8 && name.indexOf(Constants.MILLISEC_SEPARATOR) == 15) {
			// photoSync format
			localDateTime = LocalDateTime.parse(name.substring(0, 19), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT));
			// maintains before numbering
			numbering = name.substring(26);
		} else if(Utils.validDateText(photo.getExifInfo(Photo.FILE_MODIFICATION_DATE))) {
			zonedDateTime = ZonedDateTime.parse(photo.getExifInfo(Photo.FILE_MODIFICATION_DATE), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssXXX"));
			localDateTime = zonedDateTime.toLocalDateTime();
		}
		
		if(localDateTime == null) {
			log.severe(photo.getSource());
			throw new NullPointerException();
		}
		
		if(zonedDateTime == null) {
			if(Utils.validDateText(photo.getExifInfo(Photo.FILE_MODIFICATION_DATE))) {
				ZoneId zoneId = ZonedDateTime.parse(photo.getExifInfo(Photo.FILE_MODIFICATION_DATE), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ssXXX")).getZone();
				zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);
			}
		}
		
		photo.setLocalDateTime(localDateTime);
		photo.setZonedDateTime(zonedDateTime);
		
		// numbering
		if(numbering == null) {
			if(name.startsWith("KAWA")) {
				// gaeul
//			numbering = "0" + name.substring(4, 8);
				numbering = "0" + name.substring(4);
			} else if(name.startsWith("lyr ")) {
				// snap
				numbering = "1" + Utils.lPad(name.substring(name.indexOf("(") + 1, name.lastIndexOf(")")), 4, "0");
			} else if(name.startsWith("lyr_2 ")) {
				// snap
				numbering = "2" + Utils.lPad(name.substring(name.indexOf("(") + 1, name.lastIndexOf(")")), 4, "0");
			}
		}
		
		photo.setOriginalNumber(numbering);
	}
	
	/**
	 * rename photos
	 * before call, collectPhotos() call required
	 * 
	 * @param info
	 */
	private void renamePhotos(RenamePhotoCriteria criteria, RenamePhotoInfo info) {
		Comparator<Photo> photoComparator = Comparator.comparing(Photo::getLocalDateTime, Comparator.nullsLast(Comparator.naturalOrder()))
				.thenComparing(photo -> Paths.get(photo.getSource()).getFileName().toString(), new WindowsExplorerComparator());
		
		// check duplication
		Map<String, List<String>> newNameCounts = info.getPhotosMap().values().stream()
				.flatMap(photos -> photos.stream())
				.sorted(photoComparator)
				.collect(Collectors.groupingBy(photo -> {
					NameCriteria uniqueNameCriteria = new NameCriteria();
					uniqueNameCriteria.setAppendOriginal(criteria.isAppendOriginal());
					uniqueNameCriteria.setContainsExt(true);
					
					return getNewName(photo, uniqueNameCriteria);
				}, Collectors.mapping(Photo::getSource, Collectors.toList())));
		
		for(List<Photo> photos : info.getPhotosMap().values()) {
			// sort for numbering
			photos.sort(photoComparator);
			
			int number = photos.stream().mapToInt(photo -> {
						if(photo.getOriginalNumber() == null) {
							return 0;
						}
						
						return Integer.parseInt(photo.getOriginalNumber().substring(0, 5));
					}).max().orElse(0) + 1;
			
			for(Photo photo : photos) {
				try {
					File originFile = new File(photo.getSource());
					
					NameCriteria uniqueNameCriteria = new NameCriteria();
					uniqueNameCriteria.setAppendOriginal(criteria.isAppendOriginal());
					uniqueNameCriteria.setContainsExt(true);
					String uniqueNewName = getNewName(photo, uniqueNameCriteria);
					
					boolean duplication = newNameCounts.get(uniqueNewName).size() > 1;
					int sequence = newNameCounts.get(uniqueNewName).indexOf(photo.getSource()) + 1;
					
					NameCriteria nameCriteria = new NameCriteria();
					nameCriteria.setDuplication(duplication);
					nameCriteria.setNumbering(photo.getOriginalNumber() != null ? photo.getOriginalNumber() : criteria.isNumbering() ? String.valueOf(number++) : null);
					nameCriteria.setSequence(criteria.isAutoSequence() ? sequence : null);
					nameCriteria.setAppendOriginal(criteria.isAppendOriginal());
					nameCriteria.setContainsExt(true);
					String newName = getNewName(photo, nameCriteria);
					
					// rename
					String newSource = criteria.isTest() ? originFile.getParent() + "\\" + newName : fileService.renameFile(photo.getSource(), newName);
					photo.setSource(newSource);
					
					info.addCompletedPhotoCount();
					log.info(info.getLog("renamed. -", "- " + Utils.skipDir(originFile.getParent(), info.getRootDirectory(), Constants.ROOT_DIRECTORY) + " - " + originFile.getName() +  " -> " + newName));
					
					if(duplication) {
						info.getDuplicatedSources().computeIfAbsent(uniqueNewName, k -> new ArrayList<>()).add(photo.getSource());
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
	 * @param sequence
	 * if duplication append orignal file name
	 * @return
	 */
	private String getNewName(Photo photo, NameCriteria criteria) {
		File file = new File(photo.getSource());
		int pos = file.getName().lastIndexOf(".");
		String name = file.getName().substring(0, pos);
		String extension = file.getName().substring(pos + 1).toLowerCase();
		
		ZonedDateTime dateTime = photo.getZonedDateTime();
		// set +00:00
		if(dateTime == null) {
			dateTime = ZonedDateTime.of(photo.getLocalDateTime(), ZoneOffset.UTC);
		}
		
		String datetimeName = dateTime.format(DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT));
		StringBuilder newName = new StringBuilder();
		
		// yyyyMMdd_HHmmss.SSS_+0900_00001_00001.jpg
		newName.append(datetimeName);
		newName.append(Constants.NAME_SEPARATOR).append(criteria.getSequence() == null ? "00001" : Utils.lPad(String.valueOf(criteria.getSequence()), 5, "0"));
		
		String numbering = criteria.getNumbering() == null ? "00000" : Utils.lPad(criteria.getNumbering(), 5, "0");
		newName.append(Constants.NAME_SEPARATOR).append(numbering);
		if(criteria.isAppendOriginal() || criteria.isDuplication() && criteria.getSequence() == null) {
			newName.append(Constants.NAME_SEPARATOR).append(name);
		}
		
		if(criteria.isContainsExt()) {
			if(extension.equals("jpeg")) {
				extension = "jpg";
			}
			newName.append(".").append(extension);
		}
		
		return newName.toString();
	}
	
	/**
	 * update all photos meta data inside the folder
	 */
	public void updatePhotos(String source) {
		long start = System.currentTimeMillis();
		
		UpdatePhotoInfo info = new UpdatePhotoInfo();
		
		File file = new File(source);
		info.setRootDirectory(file.isFile() ? file.getParent() : file.getPath());
		info.setFailedPhotoSources(new ArrayList<>());
		
		try {
			updatePhotos(source, info);
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
	
	/**
	 * update date of exif meta data
	 * all photos inside the folder, inside folder... using DFS
	 */
	private void updatePhotos(String source, UpdatePhotoInfo info) {
		File target = new File(source);
		
		// photo
		if(target.isFile()) {
			try {
				Photo photo = exifToolService.getPhoto(target.getPath(), null);
				parseInfo(photo);
				
//				ZonedDateTime zonedDateTime = ZonedDateTime.parse(target.getName(), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_CONTAINS_OFFSET));
//				LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
//				boolean success = exifToolService.updateDate(photo.getSource(), zonedDateTime, localDateTime);
				
				boolean success = exifToolService.updateDate(photo.getSource(), photo.getZonedDateTime(), photo.getLocalDateTime());
				
				String dateTime = null;
				if(photo.getZonedDateTime() != null) {
					dateTime = photo.getZonedDateTime().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSSXXX"));
				} else {
					dateTime = photo.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss.SSS"));
				}
				if(success) {
					info.addCompletedPhotoCount();
					log.info(info.getLog("updated. -", "- " + dateTime + " - " + Utils.skipDir(photo.getSource(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
				} else {
					info.addFailPhotoCount();
					info.getFailedPhotoSources().add(photo.getSource());
					log.info(info.getLog("failed. -", "- " + dateTime + " - " + Utils.skipDir(photo.getSource(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
				}
			} catch(IOException | InterruptedException e) {
				log.severe("failed updatePhotos: " + target.getPath() + "\n" + e.toString());
				return;
			}
			
			return;
		}
		
		// folder
		info.addReadDirectoryCount();
		log.info(info.getLog("read dir -", "- " + Utils.skipDir(target.getPath(), info.getRootDirectory(), Constants.ROOT_DIRECTORY)));
		
		// folder
		File[] subFiles = target.listFiles();
		for (File subFile : subFiles) {
			updatePhotos(subFile.getPath(), info);
		}
	}
}