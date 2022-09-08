package model;

import java.util.List;
import java.util.Map;

public class RenamePhotoCriteria {
    /**
     * do auto sequencing or not
     */
    private boolean autoSequence;

    /**
     * add number at autoSequence. by folder name
     */
    private Map<String, Integer> baseAutoSequences;

    /**
     * do numbering or not
     */
    private boolean numbering;

    /**
     * override renumbering folder names
     */
    private List<String> renumberingDirectories;

    /**
     * append original file name or not
     */
    private boolean appendOriginal;

    /**
     * share numbering directories
     */
    private List<String> mergeDirectorySuffixes;

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

    public Map<String, Integer> getBaseAutoSequences() {
        return baseAutoSequences;
    }

    public void setBaseAutoSequences(Map<String, Integer> baseAutoSequences) {
        this.baseAutoSequences = baseAutoSequences;
    }

    public boolean isNumbering() {
        return numbering;
    }

    public void setNumbering(boolean numbering) {
        this.numbering = numbering;
    }

    public List<String> getRenumberingDirectories() {
        return renumberingDirectories;
    }

    public void setRenumberingDirectories(List<String> renumberingDirectories) {
        this.renumberingDirectories = renumberingDirectories;
    }

    public boolean isAppendOriginal() {
        return appendOriginal;
    }

    public void setAppendOriginal(boolean appendOriginal) {
        this.appendOriginal = appendOriginal;
    }

    public List<String> getMergeDirectorySuffixes() {
        return mergeDirectorySuffixes;
    }

    public void setMergeDirectorySuffixes(List<String> mergeDirectorySuffixes) {
        this.mergeDirectorySuffixes = mergeDirectorySuffixes;
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
