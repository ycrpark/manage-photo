package src.main.model;

import java.util.List;

import src.main.util.Utils;

public class UpdatePhotoInfo {
	/**
	 * root path
	 */
	private String rootDirectory;
	
	/**
	 * failed update meta info paths
	 */
	private List<String> failedPhotoSources;
	
	private int readDirectoryCount;
	
	private int completedPhotoCount;
	
	private int failPhotoCount;
	
	public String getRootDirectory() {
		return rootDirectory;
	}
	
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}
	
	public List<String> getFailedPhotoSources() {
		return failedPhotoSources;
	}
	
	public void setFailedPhotoSources(List<String> failedPhotoSources) {
		this.failedPhotoSources = failedPhotoSources;
	}
	
	public int getReadDirectoryCount() {
		return readDirectoryCount;
	}
	
	public void setReadDirectoryCount(int readDirectoryCount) {
		this.readDirectoryCount = readDirectoryCount;
	}
	
	public void addReadDirectoryCount() {
		readDirectoryCount++;
	}
	
	public int getCompletedPhotoCount() {
		return completedPhotoCount;
	}
	
	public void setCompletedPhotoCount(int completedPhotoCount) {
		this.completedPhotoCount = completedPhotoCount;
	}
	
	public void addCompletedPhotoCount() {
		completedPhotoCount++;
	}
	
	public int getFailPhotoCount() {
		return failPhotoCount;
	}
	
	public void setFailPhotoCount(int failPhotoCount) {
		this.failPhotoCount = failPhotoCount;
	}
	
	public void addFailPhotoCount() {
		failPhotoCount++;
	}
	
	public String getLog(String prepend, String append) {
		StringBuilder sb = new StringBuilder();
		if(prepend != null) {
			sb.append(prepend);
			sb.append(" ");
		}
		
		sb.append("readDir: ");
		sb.append(Utils.lPad(String.valueOf(readDirectoryCount), 2, " "));
		sb.append(" / completed: ");
		sb.append(Utils.lPad(String.valueOf(completedPhotoCount), 4, " "));
		sb.append(" / failed: ");
		sb.append(Utils.lPad(String.valueOf(failPhotoCount), 4, " "));
		
		if(append != null) {
			sb.append(" ");
			sb.append(append);
		}
		
		return sb.toString();
	}
}
