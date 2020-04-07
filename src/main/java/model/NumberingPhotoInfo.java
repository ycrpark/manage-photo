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
	
	private int readDirectoryCount;
	
	private int collectPhotoCount;
	
	private int completedPhotoCount;
	
	private int renamedPhotoCount;
	
	private int readDerivedDirectoryCount;
	
	private int collectDerivedPhotoCount;
	
	private int completedDerivedPhotoCount;
	
	private int renamedDerivedPhotoCount;
	
	private boolean collectedOrigin;
	
	private boolean renumberedOrigin;
	
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
	
	public void addReadDirectoryCount() {
		readDirectoryCount++;
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
	
	public int getReadDerivedDirectoryCount() {
		return readDerivedDirectoryCount;
	}
	
	public void setReadDerivedDirectoryCount(int readDerivedDirectoryCount) {
		this.readDerivedDirectoryCount = readDerivedDirectoryCount;
	}
	
	public void addReadDerivedDirectoryCount() {
		readDerivedDirectoryCount++;
	}
	
	public int getCollectDerivedPhotoCount() {
		return collectDerivedPhotoCount;
	}
	
	public void setCollectDerivedPhotoCount(int collectDerivedPhotoCount) {
		this.collectDerivedPhotoCount = collectDerivedPhotoCount;
	}
	
	public void addCollectDerivedPhotoCount() {
		collectDerivedPhotoCount++;
	}
	
	public int getCompletedDerivedPhotoCount() {
		return completedDerivedPhotoCount;
	}
	
	public void setCompletedDerivedPhotoCount(int completedDerivedPhotoCount) {
		this.completedDerivedPhotoCount = completedDerivedPhotoCount;
	}
	
	public void addCompletedDerivedPhotoCount() {
		completedDerivedPhotoCount++;
	}
	
	public int getRenamedDerivedPhotoCount() {
		return renamedDerivedPhotoCount;
	}
	
	public void setRenamedDerivedPhotoCount(int renamedDerivedPhotoCount) {
		this.renamedDerivedPhotoCount = renamedDerivedPhotoCount;
	}
	
	public void addRenamedDerivedPhotoCount() {
		renamedDerivedPhotoCount++;
	}
	
	public boolean isCollectedOrigin() {
		return collectedOrigin;
	}
	
	public void setCollectedOrigin(boolean collectedOrigin) {
		this.collectedOrigin = collectedOrigin;
	}
	
	public boolean isRenumberedOrigin() {
		return renumberedOrigin;
	}
	
	public void setRenumberedOrigin(boolean renumberedOrigin) {
		this.renumberedOrigin = renumberedOrigin;
	}
	
	public String getLog(String prepend, String append) {
		StringBuilder sb = new StringBuilder();
		if(prepend != null) {
			sb.append(prepend);
			sb.append(" ");
		}
		
		sb.append("dir: ");
		sb.append(Utils.lPad(String.valueOf(readDirectoryCount), 2, " "));
		sb.append(" / collect: ");
		sb.append(Utils.lPad(String.valueOf(collectPhotoCount), 5, " "));
		sb.append(" / complete: ");
		sb.append(Utils.lPad(String.valueOf(completedPhotoCount), 5, " "));
		sb.append(" / renamed: ");
		sb.append(Utils.lPad(String.valueOf(renamedPhotoCount), 5, " "));
		sb.append(" // dir: ");
		sb.append(Utils.lPad(String.valueOf(readDerivedDirectoryCount), 2, " "));
		sb.append(" / collect: ");
		sb.append(Utils.lPad(String.valueOf(collectDerivedPhotoCount), 5, " "));
		sb.append(" / complete: ");
		sb.append(Utils.lPad(String.valueOf(completedDerivedPhotoCount), 5, " "));
		sb.append(" / renamed: ");
		sb.append(Utils.lPad(String.valueOf(renamedDerivedPhotoCount), 5, " "));
		
		if(append != null) {
			sb.append(" ");
			sb.append(append);
		}
		
		return sb.toString();
	}
}
