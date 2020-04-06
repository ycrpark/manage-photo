package model;

import java.util.List;
import java.util.Map;

import util.Utils;

public class NumberingPhotoInfo {
	/**
	 * collected photos for numbering by folder
	 */
	private Map<String, List<Photo>> photosMap;
	
	/**
	 * duplicate file names
	 */
	private Map<String, List<String>> duplicatedSources;
	
	/**
	 * merged directory names
	 */
	private Map<String, List<String>> mergedDirectories;
	
	/**
	 * renumbered directory paths
	 */
	private List<String> renumberedDirectories;
	
	/**
	 * before rename, after sources
	 */
	private Map<String, String> renameSources;
	
	/**
	 * root path
	 */
	private String rootDirectory;
	
	private int readDirectoryCount;
	
	private int readPhotoCount;
	
	private int collectPhotoCount;
	
	private int completedPhotoCount;
	
	private int renamedPhotoCount;
	
	private String errorMessage;
	
	public Map<String, List<Photo>> getPhotosMap() {
		return photosMap;
	}
	
	public void setPhotosMap(Map<String, List<Photo>> photosMap) {
		this.photosMap = photosMap;
	}
	
	public Map<String, List<String>> getDuplicatedSources() {
		return duplicatedSources;
	}
	
	public void setDuplicatedSources(Map<String, List<String>> duplicatedSources) {
		this.duplicatedSources = duplicatedSources;
	}
	
	public Map<String, List<String>> getMergedDirectories() {
		return mergedDirectories;
	}
	
	public void setMergedDirectories(Map<String, List<String>> mergedDirectories) {
		this.mergedDirectories = mergedDirectories;
	}
	
	public List<String> getRenumberedDirectories() {
		return renumberedDirectories;
	}
	
	public void setRenumberedDirectories(List<String> renumberedDirectories) {
		this.renumberedDirectories = renumberedDirectories;
	}
	
	public Map<String, String> getRenameSources() {
		return renameSources;
	}
	
	public void setRenameSources(Map<String, String> renameSources) {
		this.renameSources = renameSources;
	}
	
	public int getReadDirectoryCount() {
		return readDirectoryCount;
	}
	
	public void setReadDirectoryCount(int readDirectoryCount) {
		this.readDirectoryCount = readDirectoryCount;
	}
	
	public int getCollectPhotoCount() {
		return collectPhotoCount;
	}
	
	public void setCollectPhotoCount(int collectPhotoCount) {
		this.collectPhotoCount = collectPhotoCount;
	}
	
	public int getCompletedPhotoCount() {
		return completedPhotoCount;
	}
	
	public void setCompletedPhotoCount(int completedPhotoCount) {
		this.completedPhotoCount = completedPhotoCount;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getRootDirectory() {
		return rootDirectory;
	}
	
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	public int getReadPhotoCount() {
		return readPhotoCount;
	}
	
	public void setReadPhotoCount(int readPhotoCount) {
		this.readPhotoCount = readPhotoCount;
	}
	
	public void addReadDirectoryCount() {
		readDirectoryCount++;
	}
	
	public void addReadPhotoCount() {
		readPhotoCount++;
	}
	
	public void addCollectPhotoCount() {
		collectPhotoCount++;
	}
	
	public void addCompletedPhotoCount() {
		completedPhotoCount++;
	}
	
	public int getRenamedPhotoCount() {
		return renamedPhotoCount;
	}
	
	public void setRenamedPhotoCount(int renamedPhotoCount) {
		this.renamedPhotoCount = renamedPhotoCount;
	}
	
	public void addRenamedPhotoCount() {
		renamedPhotoCount++;
	}
	
	public String getLog(String prepend, String append) {
		StringBuilder sb = new StringBuilder();
		if(prepend != null) {
			sb.append(prepend);
			sb.append(" ");
		}
		
		sb.append("dir: ");
		sb.append(Utils.lPad(String.valueOf(readDirectoryCount), 2, " "));
		sb.append(" / read: ");
		sb.append(Utils.lPad(String.valueOf(readPhotoCount), 5, " "));
		sb.append(" / collect: ");
		sb.append(Utils.lPad(String.valueOf(collectPhotoCount), 5, " "));
		sb.append(" / complete: ");
		sb.append(Utils.lPad(String.valueOf(completedPhotoCount), 5, " "));
		sb.append(" / renamed: ");
		sb.append(Utils.lPad(String.valueOf(renamedPhotoCount), 5, " "));
		
		if(append != null) {
			sb.append(" ");
			sb.append(append);
		}
		
		return sb.toString();
	}
}
