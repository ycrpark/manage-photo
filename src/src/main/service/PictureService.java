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

import src.main.model.Picture;
import src.main.model.Result;

public class PictureService {
	private static final Logger log = Logger.getGlobal();
	
	private static final String ROOT_DIRECTORY_KEY = "ROOT";
	
	private PictureService() {
	}
	
	private static class Loader {
		public static final PictureService INSTANCE = new PictureService();
	}
	
	public static PictureService getInstance() {
		return Loader.INSTANCE;
	}
	
	public static ExifToolService exifToolService = ExifToolService.getInstance();
	
	public void changePictureName(String source) {
		Result result = new Result();
		Map<String, List<Picture>> picturesMap = new HashMap<>();
		
		try {
			collectPictures(source, result, picturesMap);
			log.info("#####collect completed, dirCnt: " + result.getCollectDirectoryCount() + ", picCnt: " + result.getCollectPictureCount());
			if(result.getErrorMessage() == null) {
				changeCollectedPictureName(result, picturesMap);
				log.info("#####changeName completed, picCnt: " + result.getCompletedPictureCount() + " / " + result.getCollectPictureCount());
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
	private void collectPictures(String source, Result result, Map<String, List<Picture>> picturesMap) {
		File target = new File(source);
		
		// folder
		if(!target.isFile()) {
			result.addCollectDirectoryCount();
			log.info("collected. dirCnt: " + result.getCollectDirectoryCount() + ", picCnt: " + result.getCollectPictureCount() + " - " + target.getPath());
			
			for(File file : target.listFiles()) {
				collectPictures(file.getPath(), result, picturesMap);
				if(result.getErrorMessage() != null) {
					return;
				}
			}
			
			return;
		}
		
		Picture picture = null;
		try {
			picture = exifToolService.getPicture(target.getPath());
		} catch(IOException | InterruptedException e) {
			log.severe("failed read metadata: " + target.getPath() + "\n" + e.toString());
			result.setErrorMessage("failed read metadata. src: " + target.getPath());
			return;
		}
		
		String picturesKey = target.getParent();
		// if root directory
		if(picturesKey == null) {
			picturesKey = ROOT_DIRECTORY_KEY;
		}
		
		List<Picture> pictures = picturesMap.computeIfAbsent(picturesKey, key -> new LinkedList<>());
		pictures.add(picture);
		result.addCollectPictureCount();
		log.info("collected. dirCnt: " + result.getCollectDirectoryCount() + ", picCnt: " + result.getCollectPictureCount() + " - " + target.getPath());
	}
	
	private void changeCollectedPictureName(Result result, Map<String, List<Picture>> picturesMap) {
		for(List<Picture> pictures : picturesMap.values()) {
			pictures.sort(Comparator.comparing(Picture::getTakenDate, Comparator.nullsLast(Comparator.naturalOrder())));
			
			int number = 1;
			for(Picture picture : pictures) {
				try {
					File originFile = new File(picture.getSource());
					
					changePictureName(picture, number++);
					result.addCompletedPictureCount();
					
					File newFile = new File(picture.getSource());
					
					log.info("changed, picCnt: " + result.getCompletedPictureCount() + " / " + result.getCollectPictureCount()
							+ " - " + originFile.getParent() + " [" + originFile.getName() +  " -> " + newFile.getName() + "]");
				} catch(IOException e) {
					log.severe("failed rename: " + picture.getSource() + "\n" + e.toString());
					result.setErrorMessage("failed rename: " + picture.getSource());
					return;
				}
			}
		}
	}
	
	public void changePictureName(Picture picture, Integer number) throws IOException {
		if(picture == null) {
			return;
		}
		
		LocalDateTime dateTime = LocalDateTime.parse(picture.getTakenDate(), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
		String date = dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String time = dateTime.format(DateTimeFormatter.ofPattern("HHmmss"));
		
		File file = new File(picture.getSource());
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
		
		Path path = Paths.get(picture.getSource());
		Files.move(path, path.resolveSibling(newName));
		
		file = new File(file.getParent() + "\\" + newName);
		picture.setSource(file.getPath());
	}
}