package net.studymongolian.chimee;

// TODO remove references to these names in the Strings.xml file so that this is the only source
enum KeyboardType {
    Qwerty ("ᠴᠠᠭᠠᠨ ᠲᠣᠯᠤᠭᠠᠢ"),
    Aeiou ("ᠺᠣᠮᠫᠢᠦ᠋ᠲ᠋ᠧᠷ"),
    English ("ᠠᠩᠭᠯᠢ"),
    Cyrillic ("ᠺᠢᠷᠢᠯ"),
    Unselected ("unselected");

    private final String name;

    private KeyboardType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}

//public final class KeyboardType {
//
//    public static final String Qwerty = getString(R.string.keyboard_aeiou_short);
//    public static final String Aeiou = "Mode 2";
//    public static final String English = "Mode 3";
//    public static final String Cyrillic = "Mode 2";
//    public static final String Unselected = "Mode 3";
//
//    private KeyboardType() { }
//}