package net.studymongolian.chimee;

// TODO remove references to these names in the Strings.xml file so that this is the only source
enum KeyboardType {
    Qwerty ("ᠺᠣᠮᠫᠢᠦ᠋ᠲ᠋ᠧᠷ"),
    Aeiou ("ᠴᠠᠭᠠᠨ ᠲᠣᠯᠤᠭᠠᠢ"),
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

