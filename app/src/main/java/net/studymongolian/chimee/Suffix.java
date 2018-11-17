package net.studymongolian.chimee;

public class Suffix {

    private String suffix;
    private WordGender gender;
    private SuffixType type;

    // constructor
    Suffix(String suffix, WordGender gender, SuffixType type) {
        this.suffix = suffix;
        this.gender = gender;
        this.type = type;
    }

    public static enum WordGender {
        Neutral(0),
        Masculine(1),
        Feminine(2);

        private final int id;
        WordGender(int id) { this.id = id; }
        public int getValue() { return id; }
    }

    public static enum WordEnding {
        Nil(0),
        Vowel(1),
        N(2),
        BigDress(3), // b, g, d, r, s
        OtherConsonant(4); // not N or BGDRS

        private final int id;
        WordEnding(int id) { this.id = id; }
        public int getValue() { return id; }
    }

    public static enum SuffixType {
        VowelOnly(0),
        NOnly(1),
        ConsonantNonN(2),
        ConsonantsAll(3),
        BigDress(4),
        NotBigDress(5),
        All(6);

        private final int id;
        SuffixType(int id) { this.id = id; }
        public int getValue() { return id; }
    }

    // getters
    public String getSuffix() {
        return suffix;
    }
    public WordGender getWordGender() {
        return gender;
    }
    public SuffixType getSuffixType() {
        return type;
    }
}
