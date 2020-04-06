package src.main.model;

import java.util.List;
import java.util.Map;

public class RenamePhotoCriteria {
	/**
	 * do auto sequencing or not
	 */
	private boolean autoSequence;
	
	/**
	 * do numbering or not
	 */
	private boolean numbering;
	
	/**
	 * append original file name or not
	 */
	private boolean appendOriginal;
	
	/**
	 * share numbering directories
	 */
	private List<String> mergeDirectirySuffixes;
	
	/**
	 * maintains nunbering of file name Map<file prefix, List<directory path>>
	 */
	private Map<String, List<String>> maintainsNumberingDirectories;
	
	/**
	 * is test execute do not change file name
	 */
	private boolean test;
	
	public boolean isAutoSequence() {
		return autoSequence;
	}
	
	public void setAutoSequence(boolean autoSequence) {
		this.autoSequence = autoSequence;
	}
	
	public boolean isNumbering() {
		return numbering;
	}
	
	public void setNumbering(boolean numbering) {
		this.numbering = numbering;
	}
	
	public boolean isAppendOriginal() {
		return appendOriginal;
	}
	
	public void setAppendOriginal(boolean appendOriginal) {
		this.appendOriginal = appendOriginal;
	}
	
	public List<String> getMergeDirectirySuffixes() {
		return mergeDirectirySuffixes;
	}
	
	public void setMergeDirectirySuffixes(List<String> mergeDirectirySuffixes) {
		this.mergeDirectirySuffixes = mergeDirectirySuffixes;
	}
	
	public Map<String, List<String>> getMaintainsNumberingDirectories() {
		return maintainsNumberingDirectories;
	}
	
	public void setMaintainsNumberingDirectories(Map<String, List<String>> maintainsNumberingDirectories) {
		this.maintainsNumberingDirectories = maintainsNumberingDirectories;
	}
	
	public boolean isTest() {
		return test;
	}
	
	public void setTest(boolean test) {
		this.test = test;
	}
}
