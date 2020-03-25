package src.main.model;

public class Result {
	private int collectDirectoryCount;
	private int collectPhotoCount;
	private int completedPhotoCount;
	
	private String errorMessage;
	
	public int getCollectDirectoryCount() {
		return collectDirectoryCount;
	}
	
	public void setCollectDirectoryCount(int collectDirectoryCount) {
		this.collectDirectoryCount = collectDirectoryCount;
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
	
	public void addCollectDirectoryCount() {
		collectDirectoryCount++;
	}
	
	public void addCollectPhotoCount() {
		collectPhotoCount++;
	}
	
	public void addCompletedPhotoCount() {
		completedPhotoCount++;
	}
	
}
