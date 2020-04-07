package execute;
import java.util.ArrayList;
import java.util.List;

import model.NumberingPhotoCriteria;
import service.NumberService;

public class NumberingPhotos {
	public static NumberService numberService = NumberService.getInstance();
	
	/**
	 * renumbering photos
	 */
	public static void main(String[] args) {
		NumberingPhotoCriteria criteria = new NumberingPhotoCriteria();
		
		// target directory paths
		// If there are multiple, directories are integrated numbered
		List<String> directories = new ArrayList<>();
		directories.add("C:\\Users\\ycrpa\\Downloads\\test\\동기스튜디오");
//		directories.add("C:\\Users\\ycrpa\\GoogleDrive\\사진");
		criteria.setDirectories(directories);
		
		// derived directory paths
		// if same number with directories's file, numbered the same
		List<String> derivedDirectories = new ArrayList<>();
		derivedDirectories.add("C:\\Users\\ycrpa\\Downloads\\test\\동기스튜디오 보정");
		criteria.setDerivedDirectories(derivedDirectories);
		
		// append original number or not
//		criteria.setAppendOriginal(true);
		
		// if true, file name is not changed
		criteria.setTest(true);
		
		numberService.numberingPhotos(criteria);
	}
}
