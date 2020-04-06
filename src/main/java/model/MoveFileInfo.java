package model;

import java.util.List;

import util.Utils;


public class MoveFileInfo {
	private String movePath;
	
	/**
	 * root path
	 */
	private String rootDirectory;
	
	/**
	 * duplicate file names
	 */
	private List<String> duplicatedFileSources;
	
	private int moveFileCount;
	
	private int deleteFolderCount;
	
	public String getMovePath() {
		return movePath;
	}
	
	public void setMovePath(String movePath) {
		this.movePath = movePath;
	}
	
	public String getRootDirectory() {
		return rootDirectory;
	}
	
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	public List<String> getDuplicatedFileSources() {
		return duplicatedFileSources;
	}
	
	public void setDuplicatedFileSources(List<String> duplicatedFileSources) {
		this.duplicatedFileSources = duplicatedFileSources;
	}
	
	public int getMoveFileCount() {
		return moveFileCount;
	}
	
	public void setMoveFileCount(int moveFileCount) {
		this.moveFileCount = moveFileCount;
	}
	
	public void addMoveFileCount() {
		moveFileCount++;
	}
	
	public int getDeleteFolderCount() {
		return deleteFolderCount;
	}
	
	public void setDeleteFolderCount(int deleteFolderCount) {
		this.deleteFolderCount = deleteFolderCount;
	}
	
	public void addDeleteFolderCount() {
		deleteFolderCount++;
	}
	
	public String getLog(String prepend, String append) {
		StringBuilder sb = new StringBuilder();
		if(prepend != null) {
			sb.append(prepend);
			sb.append(" ");
		}
		
		sb.append("delFolder: ");
		sb.append(Utils.lPad(String.valueOf(deleteFolderCount), 2, " "));
		sb.append(" / moveFile: ");
		sb.append(Utils.lPad(String.valueOf(moveFileCount), 5, " "));
		
		if(append != null) {
			sb.append(" ");
			sb.append(append);
		}
		
		return sb.toString();
	}
}
