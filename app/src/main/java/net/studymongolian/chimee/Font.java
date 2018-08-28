package net.studymongolian.chimee;

public class Font {

    private String displayName;
    private String fileLocation;

    Font(String displayName, String fileLocation) {
        this.displayName = displayName;
        this.fileLocation = fileLocation;
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getFileLocation() {
        return fileLocation;
    }
}
