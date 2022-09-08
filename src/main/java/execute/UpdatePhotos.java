package execute;

import service.PhotoService;

public class UpdatePhotos {
    public static PhotoService photoService = PhotoService.getInstance();

    /**
     * update all photos date meta data
     * target is photo or all photos in a folder, inside folder...
     */
    public static void main(String[] args) {
        // photo file or directory path
        String source = "C:\\Users\\ycrpa\\Downloads\\새 폴더\\test";
        photoService.updatePhotos(source);
    }
}
