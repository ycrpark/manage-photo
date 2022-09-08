package execute;

import service.FileService;

public class MoveFiles {
    public static FileService fileService = FileService.getInstance();

    /**
     * move all files inside the folder, inside folder...
     * files inside the folder are moved, and the folder is deleted.
     * when duplicate file name, print log without moving.
     */
    public static void main(String[] args) {
        // file or directory path
        String source = "C:\\Users\\ycrpa\\Downloads\\test\\move";
        String movePath = "C:\\Users\\ycrpa\\Downloads\\test\\move2";
        fileService.moveFiles(source, movePath);
    }
}
