package src.main.model;

public class Result {
	private int collectDirectoryCount;
	private int collectPictureCount;
	private int completedPictureCount;
	
	private String errorMessage;
	
	public int getCollectDirectoryCount() {
		return collectDirectoryCount;
	}
	
	public void setCollectDirectoryCount(int collectDirectoryCount) {
		this.collectDirectoryCount = collectDirectoryCount;
	}
	
	public int getCollectPictureCount() {
		return collectPictureCount;
	}
	
	public void setCollectPictureCount(int collectPictureCount) {
		this.collectPictureCount = collectPictureCount;
	}
	
	public int getCompletedPictureCount() {
		return completedPictureCount;
	}
	
	public void setCompletedPictureCount(int completedPictureCount) {
		this.completedPictureCount = completedPictureCount;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void addCollectDirectoryCount() {
		collectDirectoryCount++;
	}
	
	public void addCollectPictureCount() {
		collectPictureCount++;
	}
	
	public void addCompletedPictureCount() {
		completedPictureCount++;
	}
	
}
