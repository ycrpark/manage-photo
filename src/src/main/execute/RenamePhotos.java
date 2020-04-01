package src.main.execute;
import src.main.service.PhotoService;

public class RenamePhotos {
	public static PhotoService photoService = PhotoService.getInstance();
	
	/**
	 * rename the photo's file name to specified format
	 * target is photo or all photos in a folder, inside folder...
	 * ex) yyyyMMdd-HHmmss-0001-originName.jpg (taken date)
	 */
	public static void main(String[] args) {
		// photo file or directory path
		String source = "C:\\Users\\ycrpa\\Downloads\\test";
		photoService.renamePhotos(source, false, true);
	}
}
