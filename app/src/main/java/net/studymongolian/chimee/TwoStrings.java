package net.studymongolian.chimee;

// This class is meant to replecate the functionality of a Swift String tuple
public class TwoStrings {
    private final String firstString;
    private final String secondString;

    public TwoStrings(String firstString, String secondString) {
        this.firstString = firstString;
        this.secondString = secondString;
    }

    public String getFirstString() {
        return this.firstString;
    }

    public String getSecondString() {
        return this.secondString;
    }

}
