package src.main.model;

public class NameCriteria {
	private boolean duplication;
	
	private Integer sequence;
	
	private Integer number;
	
	private boolean appendOriginal;
	
	private boolean containsExt;
	
	public boolean isDuplication() {
		return duplication;
	}
	
	public void setDuplication(boolean duplication) {
		this.duplication = duplication;
	}
	
	public Integer getSequence() {
		return sequence;
	}
	
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public boolean isAppendOriginal() {
		return appendOriginal;
	}
	
	public void setAppendOriginal(boolean appendOriginal) {
		this.appendOriginal = appendOriginal;
	}
	
	public boolean isContainsExt() {
		return containsExt;
	}
	
	public void setContainsExt(boolean containsExt) {
		this.containsExt = containsExt;
	}
	
}
