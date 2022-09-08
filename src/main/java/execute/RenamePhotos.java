package execute;

import model.RenamePhotoCriteria;
import service.PhotoService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenamePhotos {
    public static PhotoService photoService = PhotoService.getInstance();

    /**
     * rename the photo's file name to specified format
     * target is photo or all photos in a folder, inside folder...
     * <p>
     * yyyyMMdd-HHmmss.SSS-sequence-number.extension
     * ex) 20200403-174504.357-00001-00001.jpg
     * <p>
     * sequence: if same datetime, sequencing start at 1
     * number: numbering or unique number of file name
     */
    public static void main(String[] args) {
        // photo file or directory path
//		String source = "C:\\Users\\pp75362\\Desktop\\촬영\\작업1";
//		String source = "D:\\GoogleDrive\\사진";
        String source = "D:\\GoogleDrive\\사진\\웨딩촬영보정";
//		String source = "C:\\Users\\ycrpa\\Downloads\\test\\20191016_155250.010_00001_00000.CR2";

        RenamePhotoCriteria criteria = new RenamePhotoCriteria();
        // auto sequencing
        // if false, append original file name to the rename file name
        criteria.setAutoSequence(true);

        // add base sequence at folder
        criteria.setBaseAutoSequences(Map.ofEntries(
                Map.entry("웨딩촬영", 1),
                Map.entry("웨딩촬영보정", 2),
                Map.entry("웨딩촬영보정기타", 3)
        ));

        // auto numbering by folder. if false, number is 00000
        criteria.setNumbering(true);

        // override new numbering folder names
//		criteria.setRenumberingDirectories(Arrays.asList("결혼식스냅", "결혼식스냅보정"));
        criteria.setRenumberingDirectories(Arrays.asList(""));

        // append original file name at rename file name
//		criteria.setAppendOriginal(true);

        // If a folder's suffix contains list, integrated numbering with the folder excluding the suffix.
        // ex) if Arrays.asList(" etc"), each proceeds like a single folder {["apple", "apple etc"], ["picture", "picture etc"]}
        criteria.setMergeDirectorySuffixes(Arrays.asList("기타"));

        // if folder name contains value list and file name's prefix is key, using numbering
        // if value list is null, target is all files
        // ex) if key is "IMG_", "IMG_0032.jpg" -> 20200403-174504.357-00001-00032.jpg
        // ex) if key is "IMG_", "IMG_0032-abc.jpg" -> 20200403-174504.357-00001-00032-abc.jpg
        Map<String, List<String>> maintainsNumberingDirectories = new HashMap<>();
//		maintainsNumberingDirectories.put("KAWA", null);
//		maintainsNumberingDirectories.put("IMG_", Arrays.asList("동기스튜디오", "동기스튜디오 보정"));
        criteria.setMaintainsNumberingDirectories(maintainsNumberingDirectories);

        // if true, file name is not changed
        criteria.setTest(true);

        photoService.renamePhotos(source, criteria);
    }
}
