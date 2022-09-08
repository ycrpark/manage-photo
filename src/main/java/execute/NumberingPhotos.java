package execute;
import java.awt.*;
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
//		directories.add("C:\\Users\\ycrpa\\Downloads\\test\\동기스튜디오");
		
//		directories.add("C:\\Users\\ycrpa\\GoogleDrive\\사진\\웨딩촬영 RAW");


//		directories.add("D:\\GoogleDrive\\사진\\가족");
//		directories.add("D:\\GoogleDrive\\사진\\결혼식");
//		directories.add("D:\\GoogleDrive\\사진\\결혼식DVD");

//		directories.add("D:\\GoogleDrive\\사진\\결혼식스냅");

//		directories.add("D:\\GoogleDrive\\사진\\곰");
//		directories.add("D:\\GoogleDrive\\사진\\곰곰");
//		directories.add("D:\\GoogleDrive\\사진\\곰집");
//		directories.add("D:\\GoogleDrive\\사진\\다낭");

//		directories.add("D:\\GoogleDrive\\사진\\학교촬영");

//		directories.add("D:\\GoogleDrive\\사진\\동기엠티무주");
//		directories.add("D:\\GoogleDrive\\사진\\동기엠티무주기타");

//		directories.add("D:\\GoogleDrive\\사진\\동네");

//		directories.add("D:\\GoogleDrive\\사진\\동네스튜디오");

//		directories.add("D:\\GoogleDrive\\사진\\라인");

//		directories.add("D:\\GoogleDrive\\사진\\백업");

//		directories.add("D:\\GoogleDrive\\사진\\신혼여행");
//		directories.add("D:\\GoogleDrive\\사진\\신혼여행세노테");

//		directories.add("D:\\사진기타\\웨딩촬영RAW");

//		directories.add("D:\\GoogleDrive\\사진\\웨딩포토테이블");
//		directories.add("D:\\GoogleDrive\\사진\\졸업식");

//		directories.add("D:\\GoogleDrive\\사진\\졸업앨범촬영");
//		directories.add("D:\\GoogleDrive\\사진\\졸업앨범촬영기타");

//		directories.add("D:\\GoogleDrive\\사진\\카메라");
//		directories.add("D:\\GoogleDrive\\사진\\학교");
//		directories.add("D:\\GoogleDrive\\사진\\학교태안");


//		directories.add("D:\\GoogleDrive\\사진\\개포");
		directories.add("D:\\GoogleDrive\\사진\\카메라");
//		directories.add("C:\\Users\\pp75362\\Desktop\\새 폴더 (2)\\학교기타");

		
		criteria.setDirectories(directories);
		
		// derived directory paths
		// if same number with directories's file, numbered the same
		List<String> derivedDirectories = new ArrayList<>();
//		derivedDirectories.add("C:\\Users\\ycrpa\\Downloads\\test\\동기스튜디오 보정");
		
//		derivedDirectories.add("C:\\Users\\ycrpa\\GoogleDrive\\사진\\웨딩촬영");
//		derivedDirectories.add("C:\\Users\\ycrpa\\GoogleDrive\\사진\\웨딩촬영 보정");
//		derivedDirectories.add("C:\\Users\\ycrpa\\GoogleDrive\\사진\\웨딩촬영 보정기타");

//		derivedDirectories.add("C:\\Users\\pp75362\\Desktop\\새 폴더\\대상\\결혼식스냅");


//		derivedDirectories.add("D:\\GoogleDrive\\사진\\결혼식스냅보정");
//		derivedDirectories.add("D:\\GoogleDrive\\사진\\학교촬영보정");
//		derivedDirectories.add("D:\\GoogleDrive\\사진\\동네스튜디오보정");
//		derivedDirectories.add("D:\\GoogleDrive\\사진\\웨딩촬영");
//		derivedDirectories.add("D:\\GoogleDrive\\사진\\웨딩촬영보정");
//		derivedDirectories.add("D:\\GoogleDrive\\사진\\웨딩촬영보정기타");


		criteria.setDerivedDirectories(derivedDirectories);
		
		// append original number or not
//		criteria.setAppendOriginal(true);
		
		// if true, file name is not changed
		criteria.setTest(true);
		
		numberService.numberingPhotos(criteria);
	}
}
