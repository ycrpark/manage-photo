package src.main.model;

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
	 * is test execute
	 * do not change file name
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
	
	public boolean isTest() {
		return test;
	}
	
	public void setTest(boolean test) {
		this.test = test;
	}
}
