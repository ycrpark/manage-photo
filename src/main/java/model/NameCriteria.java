package model;

public class NameCriteria {
    private boolean duplication;

    private Integer sequence;

    private String numbering;

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

    public String getNumbering() {
        return numbering;
    }

    public void setNumbering(String numbering) {
        this.numbering = numbering;
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
