package model;

import java.util.List;

public class NumberingPhotoCriteria {
	/**
	 * is test execute do not change file name
	 */
	private boolean test;
	
	/**
	 * share numbering directory names
	 */
	private List<String> directories;
	
	/**
	 * append original number
	 */
	private boolean appendOriginal;
	
	/**
	 * derived directory names
	 */
	private List<String> derivedDirectories;
	
	public boolean isTest() {
		return test;
	}
	
	public void setTest(boolean test) {
		this.test = test;
	}
	
	public List<String> getDirectories() {
		return directories;
	}
	
	public void setDirectories(List<String> directories) {
		this.directories = directories;
	}
	
	public boolean isAppendOriginal() {
		return appendOriginal;
	}
	
	public void setAppendOriginal(boolean appendOriginal) {
		this.appendOriginal = appendOriginal;
	}
	
	public List<String> getDerivedDirectories() {
		return derivedDirectories;
	}
	
	public void setDerivedDirectories(List<String> derivedDirectories) {
		this.derivedDirectories = derivedDirectories;
	}
	
}
