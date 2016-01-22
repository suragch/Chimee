package net.studymongolian.chimee;

import java.util.HashMap;

/*
 * Chimee Mongol Unicode Rendering Engine
 * Version 2.1.7
 * 
 * Current version needs to be used with Almas font glyphs
 * copied to PUA starting at \uE360. To use different glyph
 * encodings, adjust the GLYPH_* static final constants below.
 * These PUA encodings are only to be used internally for glyph
 * selection. All external text should use Unicode.
 */
public class MongolUnicodeRenderer {

    // static final constants are declared at end of class for readability

    // private class variables
    private HashMap<String, String> mIsolateMap; // <Unicode, glyph>
    private HashMap<String, String> mInitialMap; // <Unicode, glyph>
    private HashMap<String, String> mMedialMap; // <Unicode, glyph>
    private HashMap<String, String> mFinalMap; // <Unicode, glyph>
    private HashMap<String, String> mSuffixMap; // <Unicode, complete_suffix_glyph_string>

    public enum Location {
        ISOLATE, INITIAL, MEDIAL, FINAL, NOT_MONGOLIAN
    }

    // Constructor
    public MongolUnicodeRenderer() {
        init();
    }

    public String unicodeToGlyphs(String inputString) {
        StringBuilder outputString = new StringBuilder();
        StringBuilder subString = new StringBuilder();

        if (inputString == null || inputString.length() == 0) {
            return "";
        }

        // Loop through characters in string
        char[] charArray = inputString.toCharArray();
        boolean isMongolSubString = isMongolian(charArray[0]);
        for (int i = 0; i < charArray.length; i++) {

            if (isMongolian(charArray[i]) || charArray[i] == NNBS || charArray[i] == CURSOR_HOLDER) {

                if (isMongolSubString) {
                    // Add Mongol chars to current Mongol word
                    subString.append(charArray[i]);
                } else {

                    // Append supstring to output string and reset
                    outputString.append(subString.toString());
                    subString.setLength(0);
                    subString.append(charArray[i]);

                }
                isMongolSubString = true;

            } else { // non-Mongol character

                if (isMongolSubString) {
                    // break up word from suffixes
                    String[] parts = subString.toString().split(String.valueOf(NNBS), -1);
                    for (int j = 0; j < parts.length; j++) {
                        if (j == 0) {
                            // Convert mongol word to glyphs and add to output string
                            outputString.append(convertWord(parts[j]));
                        } else {
                            String tempSuffix = parts[j].replace(String.valueOf(CURSOR_HOLDER), "");
                            outputString.append(NNBS);
                            if (mSuffixMap.containsKey(tempSuffix)) {
                                outputString.append(mSuffixMap.get(tempSuffix));
                                if (parts[j].contains(String.valueOf(CURSOR_HOLDER))) {
                                    outputString.append(CURSOR_HOLDER);
                                }
                            } else {
                                outputString.append(convertWord(parts[j]));
                            }
                        }
                    }

                    // reset substring
                    subString.setLength(0);
                    subString.append(charArray[i]);

                } else {
                    // Add nonMongol chars to string
                    subString.append(charArray[i]);
                }
                isMongolSubString = false;
            }
        }

        // Add any final substring
        if (subString.length() > 0) {
            if (isMongolSubString) {
                // TODO This is not DRY code, see above
                // break up word from suffixes
                String[] parts = subString.toString().split(String.valueOf(NNBS), -1);
                for (int j = 0; j < parts.length; j++) {
                    if (j == 0) {
                        // Convert mongol word to glyphs and add to output string
                        outputString.append(convertWord(parts[j]));
                    } else {
                        String tempSuffix = parts[j].replace(String.valueOf(CURSOR_HOLDER), "");
                        outputString.append(NNBS);
                        if (mSuffixMap.containsKey(tempSuffix)) {
                            outputString.append(mSuffixMap.get(tempSuffix));
                            if (parts[j].contains(String.valueOf(CURSOR_HOLDER))) {
                                outputString.append(CURSOR_HOLDER);
                            }
                        } else {
                            outputString.append(convertWord(parts[j]));
                        }
                    }
                }
            } else {
                outputString.append(subString.toString());
            }
        }

        return outputString.toString();
    }

    /**
     * Used to get the unicode character position index from a touch event that gives a glyph
     * position index
     *
     * @param unicodeString This is the string that produced the glyph string param
     * @param glyphIndex
     * @return the Unicode character position
     */
    public int getUnicodeIndex(String unicodeString, int glyphIndex) {

        // TODO This will be slow for long strings
        String glyphString = unicodeToGlyphs(unicodeString);
        // TODO sometimes this displays differently (angli delete "l", touch end)

        // error catching
        if (glyphIndex >= glyphString.length()) {
            return unicodeString.length();
        }

        // Find the matching group between spaces
        int glyphSpaceCount = 0;
        int glyphSpaceIndex = 0;
        for (int i = 0; i < glyphIndex; i++) {
            if (glyphString.charAt(i) == ' ') {
                glyphSpaceCount++;
                glyphSpaceIndex = i;
            }
        }
        String glyphGroup = glyphString.substring(glyphSpaceIndex);
        int unicodeSpaceCount = 0;
        int unicodeSpaceIndex = 0;
        if (glyphSpaceCount > 0) {
            for (int i = 0; i < unicodeString.length(); i++) {
                if (unicodeString.charAt(i) == ' ') {
                    unicodeSpaceCount++;
                    unicodeSpaceIndex = i;
                    if (unicodeSpaceCount == glyphSpaceCount) {
                        break;
                    }
                }
            }
        }

        String unicodeGroup = unicodeString.substring(unicodeSpaceIndex);

        // increment until glyphs match
        int groupGlyphIndex = glyphIndex - glyphSpaceIndex;
        int groupUnicodeIndex = groupGlyphIndex;
        boolean isMedial = false;
        if (groupGlyphIndex > 0 && groupGlyphIndex < glyphGroup.length()) {
            isMedial = isMongolianGlyphAlphabet(glyphGroup.charAt(groupGlyphIndex))
                    && isMongolianGlyphAlphabet(glyphGroup.charAt(groupGlyphIndex - 1));
        }
        for (int i = groupGlyphIndex; i < unicodeGroup.length(); i++) {
            if (!isFVS(unicodeGroup.charAt(i))) {
                if (isMedial) {
                    if (glyphGroup.substring(0, groupGlyphIndex).equals(
                            unicodeToGlyphs(unicodeGroup.substring(0, i) + ZWJ))) {
                        groupUnicodeIndex = i;
                        break;
                    }
                } else {
                    if (glyphGroup.substring(0, groupGlyphIndex).equals(
                            unicodeToGlyphs(unicodeGroup.substring(0, i)))) {
                        groupUnicodeIndex = i;
                        break;
                    }
                }

            }
        }

        return unicodeSpaceIndex + groupUnicodeIndex;
    }

    private String convertWord(String mongolWord) {

        // Error checking
        if (mongolWord == null || mongolWord.length() == 0) {
            return "";
        }

        final int MAXIMUM_SEARCH_LENGTH = 4; // max length in HashMap is 4(?).
        String formattedMongolWord = "";
        StringBuilder returnString = new StringBuilder();

        // Check if cursor holder is present
        boolean startsWithCursorHolder = mongolWord.startsWith(String.valueOf(CURSOR_HOLDER));
        boolean endsWithCursorHolder = mongolWord.endsWith(String.valueOf(CURSOR_HOLDER));
        if (mongolWord.equals(String.valueOf(CURSOR_HOLDER))) {
            return mongolWord;
        } else if (startsWithCursorHolder) {
            formattedMongolWord = mongolWord.substring(1);
        } else if (endsWithCursorHolder) {
            formattedMongolWord = mongolWord.substring(0, mongolWord.length() - 1);
        } else {
            formattedMongolWord = mongolWord;
        }

        // apply rules
        formattedMongolWord = preFormatter(formattedMongolWord);

        // Check whole word in isolate table
        if (formattedMongolWord.length() <= MAXIMUM_SEARCH_LENGTH
                && mIsolateMap.containsKey(formattedMongolWord)) {

            returnString.append(String.valueOf(mIsolateMap.get(formattedMongolWord)));

            if (startsWithCursorHolder) {
                return String.valueOf(CURSOR_HOLDER) + returnString.toString();
            } else if (endsWithCursorHolder) {
                return returnString.toString() + String.valueOf(CURSOR_HOLDER);
            } else {
                return returnString.toString();
            }
        }

        // initialize variables
        int initialEndIndex = 0;
        int finalStartIndex = 0;
        int medialStartIndex = 0;
        int medialEndIndex = 0;

        // Find longest matching initial (search long to short) TODO is this slow?
        String subString = "";
        String match = "";
        int start;
        if (formattedMongolWord.length() > MAXIMUM_SEARCH_LENGTH) {
            start = MAXIMUM_SEARCH_LENGTH;
        } else {
            start = formattedMongolWord.length() - 1;
        }
        for (int i = start; i > 0; i--) {
            subString = formattedMongolWord.substring(0, i);
            if (mInitialMap.containsKey(subString)) {
                match = mInitialMap.get(subString);
                initialEndIndex = i;
                break;
            }
        }
        if (match == null || match.length() == 0) {
            // Log.e("app", "Initial not found");
            // System.out.println("Initial not found");
        }
        if (startsWithCursorHolder) {
            returnString.append(CURSOR_HOLDER);
        }
        returnString.append(match);

        // Find longest matching final (search long to short) TODO is this slow?
        String finalGlyph = "";
        if (formattedMongolWord.length() > MAXIMUM_SEARCH_LENGTH + initialEndIndex) {
            start = formattedMongolWord.length() - MAXIMUM_SEARCH_LENGTH;
        } else {
            start = initialEndIndex;
        }
        for (int i = start; i < formattedMongolWord.length(); i++) {
            subString = formattedMongolWord.substring(i, formattedMongolWord.length());
            if (mFinalMap.containsKey(subString)) {
                finalGlyph = mFinalMap.get(subString);
                finalStartIndex = i;
                break;
            }
        }
        if (finalGlyph.length() == 0 || finalGlyph == null) {
            // Log.e("app", "Final not found");
            // System.out.println("Final not found");
        }

        // Find string of medials (search long to short) TODO is this slow?
        match = "";
        medialStartIndex = initialEndIndex;
        medialEndIndex = finalStartIndex; // substring endindex is exclusive
        boolean matchFound = false;
        while (medialStartIndex < finalStartIndex) {

            if (medialStartIndex + MAXIMUM_SEARCH_LENGTH < medialEndIndex) {
                start = medialStartIndex + MAXIMUM_SEARCH_LENGTH;
            } else {
                start = medialEndIndex;
            }
            for (int i = start; i > medialStartIndex; i--) {
                subString = formattedMongolWord.substring(medialStartIndex, i);
                if (mMedialMap.containsKey(subString)) {
                    match = mMedialMap.get(subString);
                    // returnString.append(mMedialMap.get(subString));
                    medialStartIndex = i;
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) {
                // Log.e("app", "medial not found");
                // System.out.println("medial not found");
                break;
            }
            returnString.append(match);
            // medialStartIndex = medialEndIndex;
            match = "";
        }

        // Return [ini + med + fin]
        returnString.append(finalGlyph);
        if (endsWithCursorHolder) {
            returnString.append(CURSOR_HOLDER);
        }
        return returnString.toString();
    }

    private String preFormatter(String mongolWord) {
        // This method applies context based formatting rules by adding the appropriate FVS character
        // TODO This method is slow because every rule has to loop through the word. However, this was intentional in order to separate the rules for easier debugging


        StringBuilder word = new StringBuilder();
        word.append(mongolWord);

        // MVS rule (only formats A/E after the MVS)
        // Consonant before is formatted by lookup table
        // If A/E is not final then ignore MVS (mingg-a -> minggan)
        for (int i = word.length() - 2; i >= 0; i--) {
            if (word.charAt(i) == MVS) {
                // following char is a vowel
                if (i == word.length() - 2
                        && (word.charAt(i + 1) == UNI_A || word.charAt(i + 1) == UNI_E)) {
                    // insert FVS2 (this is the lower form of FVS1)
                    word.insert(i + 2, FVS2);
                } else if (i == word.length() - 2 && (word.charAt(i + 1) == ZWJ)) {
                    // This will still allow consonant to display correctly
                } else { // following letter is not final A/E or ZWJ
                    // ignore MVS
                    word.deleteCharAt(i);
                }
            }
        }

        // Only allow the NG/B/P/F/K/KH and G/Q ligature if A/O/U or MVS follows
        for (int i = word.length() - 3; i >= 0; i--) {
            // this char is NG/B/P/F/K/KH
            if (word.charAt(i) == UNI_ANG || word.charAt(i) == UNI_BA || word.charAt(i) == UNI_PA || word.charAt(i) == UNI_FA || word.charAt(i) == UNI_KA || word.charAt(i) == UNI_KHA) {
                // following char is Q/G
                if (word.charAt(i + 1) == UNI_QA || word.charAt(i + 1) == UNI_GA) {
                    // following char is not A/O/U or MVS (MVS allows NG+G/Q ligature)
                    if (!isMasculineVowel(word.charAt(i + 2)) && word.charAt(i + 2) != MVS) {
                        // insert ZWJ to prevent ligature between NG/B/P/F/K/KH and G/Q
                        word.insert(i + 1, ZWJ);
                    }
                }
            }
        }

        // *** OE/UE long tooth in first syllable for non ligatures rule ***
        // (long tooth ligatures are handled by the hash tables)
        if (word.length() > 2) {
            // second char is OE or UE
            if (word.charAt(1) == UNI_OE || word.charAt(1) == UNI_UE) {
                // first char not a vowel or ligature consonant (B/P/Q/G/F/K/KH)
                if (!isVowel(word.charAt(0)) && word.charAt(0) != UNI_BA && word.charAt(0) != UNI_PA && word.charAt(0) != UNI_QA && word.charAt(0) != UNI_GA && word.charAt(0) != UNI_FA && word.charAt(0) != UNI_KA && word.charAt(0) != UNI_KHA) {
                    if (!isFVS(word.charAt(2))) {
                        // insert FVS1 after OE/UE
                        word.insert(2, FVS1);
                    }
                }
                // second char is FVS and third char is OE or UE
            } else if (isFVS(word.charAt(1)) && word.length() > 3 && (word.charAt(2) == UNI_OE || word.charAt(2) == UNI_UE)) {
                // first char not a vowel or ligature consonant (B/P/Q/G/F/K/KH)
                if (!isVowel(word.charAt(0)) && word.charAt(0) != UNI_BA && word.charAt(0) != UNI_PA && word.charAt(0) != UNI_QA && word.charAt(0) != UNI_GA && word.charAt(0) != UNI_FA && word.charAt(0) != UNI_KA && word.charAt(0) != UNI_KHA) {
                    if (!isFVS(word.charAt(3))) {
                        // insert FVS1 after OE/UE
                        word.insert(3, FVS1);
                    }
                }
            }
        }

        // *** medial N rule ***
        for (int i = word.length() - 2; i > 0; i--) {
            if (word.charAt(i) == UNI_NA) {
                // following char is a vowel
                if (isVowel(word.charAt(i + 1))) {
                    // insert FVS1
                    word.insert(i + 1, FVS1);
                }
            }
        }

        // *** medial D rule ***
        for (int i = word.length() - 2; i > 0; i--) {
            if (word.charAt(i) == UNI_DA) {
                // following char is a vowel
                if (isVowel(word.charAt(i + 1))) {
                    // insert FVS1
                    word.insert(i + 1, FVS1);
                }
            }
        }

        // GA rules
        if (word.charAt(0) == UNI_GA) {

            // Initial GA
            if (word.length() > 1 && isConsonant(word.charAt(1))) {
                // *** Initial GA before consonant rule ***
                // make it a feminine initial GA
                word.insert(1, FVS2);
            }
        }
        for (int i = word.length() - 1; i > 0; i--) {
            if (word.charAt(i) == UNI_GA) {

                // final GA
                boolean isMasculineWord = false;
                if (i == word.length() - 1) {

                    // **** feminine final GA rule ****
                    for (int j = i - 1; j >= 0; j--) {
                        // vowel I also defaults to feminine
                        if (isMasculineVowel(word.charAt(j))) {
                            isMasculineWord = true;
                            break;
                        }
                    }
                    if (!isMasculineWord) {
                        // make it a feminine final GA
                        word.insert(i + 1, FVS2);
                    }

                } else { // medial GA

                    // **** dotted medial masculine GA rule ****
                    if (isMasculineVowel(word.charAt(i + 1))) {
                        // add the dots
                        word.insert(i + 1, FVS1);

                        // **** feminine medial GA rule ****
                    } else if (isConsonant(word.charAt(i + 1))) {
                        boolean isFeminineWord = false;
                        isMasculineWord = false;


                        if (isConsonant(word.charAt(i - 1)) || word.charAt(i - 1) == ZWJ) {
                            // This means we have consonant+GA+consonant (ex. ANGGLI)
                            // Although the whole word may not actually be feminine, still use the feminine medial GA
                            isFeminineWord = true;
                        }else{
                            // check before GA for gender of vowel
                            for (int j = i - 1; j >= 0; j--) {
                                if (isFeminineVowel(word.charAt(j))) {
                                    isFeminineWord = true;
                                    break;
                                } else if (isMasculineVowel(word.charAt(j))) {
                                    isMasculineWord = true;
                                    break;
                                }
                            }
                        }



                        if (isFeminineWord) {
                            // make it a feminine medial GA
                            word.insert(i + 1, FVS3);
                        } else if (!isMasculineWord) {

                            // couldn't be determined by looking before
                            // so check after GA for no masculine vowel
                            isMasculineWord = false;
                            for (int j = i + 1; j < word.length(); j++) {
                                // vowel I also defaults to feminine
                                if (isMasculineVowel(word.charAt(j))) {
                                    isMasculineWord = true;
                                    break;
                                }
                            }
                            if (!isMasculineWord) {
                                // make it a feminine medial GA, Thus, I defaults to feminine GA
                                word.insert(i + 1, FVS3);
                            }
                        }
                    }
                }

            }
        } // End of GA rules

        // *** medial Y rule ***
        // upturn the Y before any vowel except I (when YI follows vowel)
        for (int i = word.length() - 2; i > 0; i--) {
            if (word.charAt(i) == UNI_YA) {
                char nextChar = word.charAt(i + 1);
                char prevChar = word.charAt(i - 1);
                // following char is a vowel besides I (or previous char is consonant)
                if ((isVowel(nextChar) && nextChar != UNI_I) || (!isVowel(prevChar)) && !isFVS(nextChar) && nextChar != MVS) {
                    // insert FVS1 (hooked Y)
                    word.insert(i + 1, FVS1);
                }
            }
        }

        // *** medial W rule ***
        // Use the hooked W before any vowel
        for (int i = word.length() - 2; i > 0; i--) {
            if (word.charAt(i) == UNI_WA) {
                if (isVowel(word.charAt(i + 1))) {
                    // insert FVS1 (hooked W)
                    word.insert(i + 1, FVS1);
                }
            }
        }

        // *** AI, EI, OI, UI medial I diphthong rule ***
        for (int i = word.length() - 2; i > 0; i--) {
            if (word.charAt(i) == UNI_I) {
                // previous char is a masculine vowel or E and next char is not FVS
                if ((isMasculineVowel(word.charAt(i - 1)) || word.charAt(i - 1) == UNI_E)
                        && !isFVS(word.charAt(i + 1))) {
                    // insert FVS3 (double tooth medial I)
                    word.insert(i + 1, FVS3);
                }
            }
        }

        return word.toString();
    }

    public static boolean isVowel(char character) {
        return (character >= UNI_A && character <= UNI_EE);
    }

    private boolean isMasculineVowel(char character) {
        return (character == UNI_A || character == UNI_O || character == UNI_U);
    }

    private boolean isFeminineVowel(char character) {
        return (character == UNI_E || character == UNI_EE || character == UNI_OE || character == UNI_UE);
    }

    public boolean isConsonant(char character) {
        return (character >= UNI_NA && character <= UNI_CHI);
    }

    private boolean isFVS(char character) {
        return (character >= FVS1 && character <= FVS3);
    }

    public static boolean isMongolian(char character) {
        // Mongolian letters, MVS, FVS1-3, NIRUGU, ZWJ, (but not NNBS)
        return ((character >= UNI_A && character <= UNI_CHI)
                || (character >= MONGOLIAN_NIRUGU && character <= MVS) || character == ZWJ);
    }

    public boolean isBGDRS(char character) {
        // This method is not used internally, only for external use.
        return (character == UNI_BA || character == UNI_GA || character == UNI_DA
                || character == UNI_RA || character == UNI_SA);
    }

    public static boolean isMongolianAlphabet(char character) {
        // This method is not used internally, only for external use.
        return (character >= UNI_A && character <= UNI_CHI);
    }

    private boolean isMongolianGlyphAlphabet(char character) {
        return (character >= GLYPH_ISOL_A && character <= GLYPH_INIT_KHA_MEDI_UE_FVS1);
    }

    public boolean isMasculineWord(String word) {
        // This method is not used internally, only for external use.
        if (word == null || word.equals("")) {
            return false;
        }
        char[] characters = word.toCharArray();
        for (int i = characters.length - 1; i >= 0; i--) {
            if (characters[i] == UNI_A || characters[i] == UNI_O || characters[i] == UNI_U) {
                return true;
            }
        }
        return false;
    }

    public boolean isFeminineWord(String word) {
        // This method is not used internally, only for external use.
        if (word == null || word.equals("")) {
            return false;
        }
        char[] characters = word.toCharArray();
        for (int i = characters.length - 1; i >= 0; i--) {
            if (characters[i] == UNI_E || characters[i] == UNI_OE || characters[i] == UNI_UE
                    || characters[i] == UNI_EE) {
                return true;
            }
        }
        return false;
    }

    public String getIsolate(String lookup) {
        if (mIsolateMap.containsKey(lookup)) {
            return mIsolateMap.get(lookup);
        } else {
            return "";
        }
    }

    public String getInitial(String lookup) {
        if (mInitialMap.containsKey(lookup)) {
            return mInitialMap.get(lookup);
        } else {
            return "";
        }
    }

    public String getMedial(String lookup) {
        if (mMedialMap.containsKey(lookup)) {
            return mMedialMap.get(lookup);
        } else {
            return "";
        }
    }

    public String getFinal(String lookup) {
        if (mFinalMap.containsKey(lookup)) {
            return mFinalMap.get(lookup);
        } else {
            return "";
        }
    }

    private void init() {

        // This is a lot of initialization. Possibly slow?
        initIsolated();
        initInitial();
        initMedial();
        initFinal();
        initSuffixes();

    }

    private void initIsolated() {

        // NOTE: assuming MAXIMUM_SEARCH_LENGTH = 4

        mIsolateMap = new HashMap<String, String>();

        // Single letters
        mIsolateMap.put("" + UNI_A, "" + GLYPH_ISOL_A);
        mIsolateMap.put("" + UNI_A + FVS1, "" + GLYPH_ISOL_A_FVS1);
        mIsolateMap.put("" + UNI_E, "" + GLYPH_ISOL_E);
        mIsolateMap.put("" + UNI_E + FVS1, "" + GLYPH_ISOL_E_FVS1);
        mIsolateMap.put("" + UNI_I, "" + GLYPH_ISOL_I);
        mIsolateMap.put("" + UNI_I + FVS1, "" + GLYPH_ISOL_I_FVS1);
        mIsolateMap.put("" + UNI_O, "" + GLYPH_ISOL_O);
        mIsolateMap.put("" + UNI_O + FVS1, "" + GLYPH_ISOL_O_FVS1);
        mIsolateMap.put("" + UNI_U, "" + GLYPH_ISOL_U);
        mIsolateMap.put("" + UNI_U + FVS1, "" + GLYPH_ISOL_U_FVS1);
        mIsolateMap.put("" + UNI_U + FVS2, "" + GLYPH_ISOL_U_FVS2);  // I am adding this myself
        mIsolateMap.put("" + UNI_OE, "" + GLYPH_ISOL_OE);
        mIsolateMap.put("" + UNI_OE + FVS1, "" + GLYPH_ISOL_OE_FVS1);
        mIsolateMap.put("" + UNI_UE, "" + GLYPH_ISOL_UE);
        mIsolateMap.put("" + UNI_UE + FVS1, "" + GLYPH_ISOL_UE_FVS1);
        mIsolateMap.put("" + UNI_UE + FVS2, "" + GLYPH_ISOL_UE_FVS2);
        mIsolateMap.put("" + UNI_UE + FVS3, "" + GLYPH_ISOL_UE_FVS3);  // I am adding this myself
        mIsolateMap.put("" + UNI_EE, "" + GLYPH_ISOL_EE);
        mIsolateMap.put("" + UNI_EE + FVS1, "" + GLYPH_ISOL_EE_FVS1);
        mIsolateMap.put("" + UNI_NA, "" + GLYPH_ISOL_NA);
        mIsolateMap.put("" + UNI_NA + FVS1, "" + GLYPH_ISOL_NA_FVS1);
        mIsolateMap.put("" + UNI_ANG, "" + GLYPH_ISOL_ANG);
        mIsolateMap.put("" + UNI_BA, "" + GLYPH_ISOL_BA);
        mIsolateMap.put("" + UNI_PA, "" + GLYPH_ISOL_PA);
        mIsolateMap.put("" + UNI_QA, "" + GLYPH_ISOL_QA);
        mIsolateMap.put("" + UNI_QA + FVS1, "" + GLYPH_ISOL_QA_FVS1);
        mIsolateMap.put("" + UNI_QA + FVS2, "" + GLYPH_ISOL_QA_FVS2);
        mIsolateMap.put("" + UNI_QA + FVS3, "" + GLYPH_ISOL_QA_FVS3);
        mIsolateMap.put("" + UNI_GA, "" + GLYPH_ISOL_GA);
        mIsolateMap.put("" + UNI_GA + FVS1, "" + GLYPH_ISOL_GA_FVS1);
        mIsolateMap.put("" + UNI_GA + FVS2, "" + GLYPH_ISOL_GA_FVS2);
        mIsolateMap.put("" + UNI_GA + FVS3, "" + GLYPH_ISOL_GA_FVS3);
        mIsolateMap.put("" + UNI_MA, "" + GLYPH_ISOL_MA);
        mIsolateMap.put("" + UNI_LA, "" + GLYPH_ISOL_LA);
        mIsolateMap.put("" + UNI_SA, "" + GLYPH_ISOL_SA);
        mIsolateMap.put("" + UNI_SHA, "" + GLYPH_ISOL_SHA);
        mIsolateMap.put("" + UNI_TA, "" + GLYPH_ISOL_TA);
        mIsolateMap.put("" + UNI_TA + FVS1, "" + GLYPH_ISOL_TA_FVS1);
        mIsolateMap.put("" + UNI_DA, "" + GLYPH_ISOL_DA);
        mIsolateMap.put("" + UNI_CHA, "" + GLYPH_ISOL_CHA);
        mIsolateMap.put("" + UNI_JA, "" + GLYPH_ISOL_JA);
        mIsolateMap.put("" + UNI_JA + FVS1, "" + GLYPH_ISOL_JA_FVS1);
        mIsolateMap.put("" + UNI_YA, "" + GLYPH_ISOL_YA);
        mIsolateMap.put("" + UNI_RA, "" + GLYPH_ISOL_RA);
        mIsolateMap.put("" + UNI_WA, "" + GLYPH_ISOL_WA);
        mIsolateMap.put("" + UNI_FA, "" + GLYPH_ISOL_FA);
        mIsolateMap.put("" + UNI_KA, "" + GLYPH_ISOL_KA);
        mIsolateMap.put("" + UNI_KHA, "" + GLYPH_ISOL_KHA);
        mIsolateMap.put("" + UNI_TSA, "" + GLYPH_ISOL_TSA);
        mIsolateMap.put("" + UNI_ZA, "" + GLYPH_ISOL_ZA);
        mIsolateMap.put("" + UNI_HAA, "" + GLYPH_ISOL_HAA);
        mIsolateMap.put("" + UNI_ZRA, "" + GLYPH_ISOL_ZRA);
        mIsolateMap.put("" + UNI_LHA, "" + GLYPH_ISOL_LHA);
        mIsolateMap.put("" + UNI_ZHI, "" + GLYPH_ISOL_ZHI);
        mIsolateMap.put("" + UNI_CHI, "" + GLYPH_ISOL_CHI);

        // Double letters
        mIsolateMap.put("" + UNI_BA + UNI_A, "" + GLYPH_INIT_BA_FINA_A);
        mIsolateMap.put("" + UNI_BA + UNI_E, "" + GLYPH_INIT_BA_FINA_E);
        mIsolateMap.put("" + UNI_BA + UNI_I, "" + GLYPH_INIT_BA_FINA_I);
        mIsolateMap.put("" + UNI_BA + UNI_O, "" + GLYPH_INIT_BA_FINA_O);
        mIsolateMap.put("" + UNI_BA + UNI_U, "" + GLYPH_INIT_BA_FINA_U);
        mIsolateMap.put("" + UNI_BA + UNI_OE, "" + GLYPH_INIT_BA_FINA_OE);
        mIsolateMap.put("" + UNI_BA + UNI_UE, "" + GLYPH_INIT_BA_FINA_UE);
        mIsolateMap.put("" + UNI_BA + UNI_EE, "" + GLYPH_INIT_BA_FINA_EE);
        mIsolateMap.put("" + UNI_PA + UNI_A, "" + GLYPH_INIT_PA_FINA_A);
        mIsolateMap.put("" + UNI_PA + UNI_E, "" + GLYPH_INIT_PA_FINA_E);
        mIsolateMap.put("" + UNI_PA + UNI_I, "" + GLYPH_INIT_PA_FINA_I);
        mIsolateMap.put("" + UNI_PA + UNI_O, "" + GLYPH_INIT_PA_FINA_O);
        mIsolateMap.put("" + UNI_PA + UNI_U, "" + GLYPH_INIT_PA_FINA_U);
        mIsolateMap.put("" + UNI_PA + UNI_OE, "" + GLYPH_INIT_PA_FINA_OE);
        mIsolateMap.put("" + UNI_PA + UNI_UE, "" + GLYPH_INIT_PA_FINA_UE);
        mIsolateMap.put("" + UNI_PA + UNI_EE, "" + GLYPH_INIT_PA_FINA_EE);
        mIsolateMap.put("" + UNI_QA + UNI_E, "" + GLYPH_INIT_QA_FINA_E);
        mIsolateMap.put("" + UNI_QA + UNI_I, "" + GLYPH_INIT_QA_FINA_I);
        mIsolateMap.put("" + UNI_QA + UNI_OE, "" + GLYPH_INIT_QA_FINA_OE);
        mIsolateMap.put("" + UNI_QA + UNI_UE, "" + GLYPH_INIT_QA_FINA_UE);
        mIsolateMap.put("" + UNI_QA + UNI_EE, "" + GLYPH_INIT_QA_FINA_EE);
        mIsolateMap.put("" + UNI_GA + UNI_E, "" + GLYPH_INIT_GA_FINA_E);
        mIsolateMap.put("" + UNI_GA + UNI_I, "" + GLYPH_INIT_GA_FINA_I);
        mIsolateMap.put("" + UNI_GA + UNI_OE, "" + GLYPH_INIT_GA_FINA_OE);
        mIsolateMap.put("" + UNI_GA + UNI_UE, "" + GLYPH_INIT_GA_FINA_UE);
        mIsolateMap.put("" + UNI_GA + UNI_EE, "" + GLYPH_INIT_GA_FINA_EE);
        mIsolateMap.put("" + UNI_FA + UNI_A, "" + GLYPH_INIT_FA_FINA_A);
        mIsolateMap.put("" + UNI_FA + UNI_E, "" + GLYPH_INIT_FA_FINA_E);
        mIsolateMap.put("" + UNI_FA + UNI_I, "" + GLYPH_INIT_FA_FINA_I);
        mIsolateMap.put("" + UNI_FA + UNI_O, "" + GLYPH_INIT_FA_FINA_O);
        mIsolateMap.put("" + UNI_FA + UNI_U, "" + GLYPH_INIT_FA_FINA_U);
        mIsolateMap.put("" + UNI_FA + UNI_OE, "" + GLYPH_INIT_FA_FINA_OE);
        mIsolateMap.put("" + UNI_FA + UNI_UE, "" + GLYPH_INIT_FA_FINA_UE);
        mIsolateMap.put("" + UNI_FA + UNI_EE, "" + GLYPH_INIT_FA_FINA_EE);
        mIsolateMap.put("" + UNI_KA + UNI_A, "" + GLYPH_INIT_KA_FINA_A);
        mIsolateMap.put("" + UNI_KA + UNI_E, "" + GLYPH_INIT_KA_FINA_E);
        mIsolateMap.put("" + UNI_KA + UNI_I, "" + GLYPH_INIT_KA_FINA_I);
        mIsolateMap.put("" + UNI_KA + UNI_O, "" + GLYPH_INIT_KA_FINA_O);
        mIsolateMap.put("" + UNI_KA + UNI_U, "" + GLYPH_INIT_KA_FINA_U);
        mIsolateMap.put("" + UNI_KA + UNI_OE, "" + GLYPH_INIT_KA_FINA_OE);
        mIsolateMap.put("" + UNI_KA + UNI_UE, "" + GLYPH_INIT_KA_FINA_UE);
        mIsolateMap.put("" + UNI_KA + UNI_EE, "" + GLYPH_INIT_KA_FINA_EE);
        mIsolateMap.put("" + UNI_KHA + UNI_A, "" + GLYPH_INIT_KHA_FINA_A);
        mIsolateMap.put("" + UNI_KHA + UNI_E, "" + GLYPH_INIT_KHA_FINA_E);
        mIsolateMap.put("" + UNI_KHA + UNI_I, "" + GLYPH_INIT_KHA_FINA_I);
        mIsolateMap.put("" + UNI_KHA + UNI_O, "" + GLYPH_INIT_KHA_FINA_O);
        mIsolateMap.put("" + UNI_KHA + UNI_U, "" + GLYPH_INIT_KHA_FINA_U);
        mIsolateMap.put("" + UNI_KHA + UNI_OE, "" + GLYPH_INIT_KHA_FINA_OE);
        mIsolateMap.put("" + UNI_KHA + UNI_UE, "" + GLYPH_INIT_KHA_FINA_UE);
        mIsolateMap.put("" + UNI_KHA + UNI_EE, "" + GLYPH_INIT_KHA_FINA_EE);
        mIsolateMap.put("" + UNI_QA + UNI_OE + FVS1, "" + GLYPH_INIT_QA_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_QA + UNI_UE + FVS1, "" + GLYPH_INIT_QA_FINA_UE_FVS1);
        mIsolateMap.put("" + UNI_GA + UNI_OE + FVS1, "" + GLYPH_INIT_GA_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_GA + UNI_UE + FVS1, "" + GLYPH_INIT_GA_FINA_UE_FVS1);
        mIsolateMap.put("" + UNI_QA + FVS1 + UNI_E, "" + GLYPH_INIT_QA_FVS1_FINA_E);
        mIsolateMap.put("" + UNI_QA + FVS1 + UNI_I, "" + GLYPH_INIT_QA_FVS1_FINA_I);
        mIsolateMap.put("" + UNI_QA + FVS1 + UNI_OE, "" + GLYPH_INIT_QA_FVS1_FINA_OE);
        mIsolateMap.put("" + UNI_QA + FVS1 + UNI_UE, "" + GLYPH_INIT_QA_FVS1_FINA_UE);
        mIsolateMap.put("" + UNI_QA + FVS1 + UNI_EE, "" + GLYPH_INIT_QA_FVS1_FINA_EE);
        mIsolateMap.put("" + UNI_GA + FVS1 + UNI_E, "" + GLYPH_INIT_GA_FVS1_FINA_E);
        mIsolateMap.put("" + UNI_GA + FVS1 + UNI_I, "" + GLYPH_INIT_GA_FVS1_FINA_I);
        mIsolateMap.put("" + UNI_GA + FVS1 + UNI_OE, "" + GLYPH_INIT_GA_FVS1_FINA_OE);
        mIsolateMap.put("" + UNI_GA + FVS1 + UNI_UE, "" + GLYPH_INIT_GA_FVS1_FINA_UE);
        mIsolateMap.put("" + UNI_GA + FVS1 + UNI_EE, "" + GLYPH_INIT_GA_FVS1_FINA_EE);
        mIsolateMap.put("" + UNI_BA + UNI_OE + FVS1, "" + GLYPH_INIT_BA_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_BA + UNI_UE + FVS1, "" + GLYPH_INIT_BA_FINA_UE_FVS1);
        mIsolateMap.put("" + UNI_PA + UNI_OE + FVS1, "" + GLYPH_INIT_PA_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_PA + UNI_UE + FVS1, "" + GLYPH_INIT_PA_FINA_UE_FVS1);
        mIsolateMap.put("" + UNI_QA + FVS1 + UNI_OE + FVS1, "" + GLYPH_INIT_QA_FVS1_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_QA + FVS1 + UNI_UE + FVS1, "" + GLYPH_INIT_QA_FVS1_FINA_UE_FVS1);
        mIsolateMap.put("" + UNI_GA + FVS1 + UNI_OE + FVS1, "" + GLYPH_INIT_GA_FVS1_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_GA + FVS1 + UNI_UE + FVS1, "" + GLYPH_INIT_GA_FVS1_FINA_UE_FVS1);
        mIsolateMap.put("" + UNI_FA + UNI_OE + FVS1, "" + GLYPH_INIT_FA_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_FA + UNI_UE + FVS1, "" + GLYPH_INIT_FA_FINA_UE_FVS1);
        mIsolateMap.put("" + UNI_KA + UNI_OE + FVS1, "" + GLYPH_INIT_KA_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_KA + UNI_UE + FVS1, "" + GLYPH_INIT_KA_FINA_UE_FVS1);
        mIsolateMap.put("" + UNI_KHA + UNI_OE + FVS1, "" + GLYPH_INIT_KHA_FINA_OE_FVS1);
        mIsolateMap.put("" + UNI_KHA + UNI_UE + FVS1, "" + GLYPH_INIT_KHA_FINA_UE_FVS1);

        // BUU exception (no tooth on first UE)
        mIsolateMap.put("" + UNI_BA + UNI_UE + UNI_UE, "" + GLYPH_INIT_BA_MEDI_U + GLYPH_FINA_UE);

        // Catch other chars
        mIsolateMap.put("" + CURSOR_HOLDER, "" + CURSOR_HOLDER);
        mIsolateMap.put("" + MONGOLIAN_NIRUGU, "" + GLYPH_NIRUGU);
        mIsolateMap.put("" + ZWJ, "");
        mIsolateMap.put("" + NNBS, "" + NNBS);
        mIsolateMap.put("" + MVS, "");
        mIsolateMap.put("" + FVS1, "");
        mIsolateMap.put("" + FVS2, "");
        mIsolateMap.put("" + FVS3, "");

    }

    private void initInitial() {

        // NOTE: assuming MAXIMUM_SEARCH_LENGTH = 4

        mInitialMap = new HashMap<String, String>();

        mInitialMap.put("" + UNI_A, "" + GLYPH_INIT_A);
        mInitialMap.put("" + UNI_A + FVS1, "" + GLYPH_INIT_A_FVS1);
        mInitialMap.put("" + UNI_E, "" + GLYPH_INIT_E);
        mInitialMap.put("" + UNI_E + FVS1, "" + GLYPH_INIT_E_FVS1);
        mInitialMap.put("" + UNI_I, "" + GLYPH_INIT_I);
        mInitialMap.put("" + UNI_I + FVS1, "" + GLYPH_INIT_I_FVS1);
        mInitialMap.put("" + UNI_O, "" + GLYPH_INIT_O);
        mInitialMap.put("" + UNI_O + FVS1, "" + GLYPH_INIT_O_FVS1);
        mInitialMap.put("" + UNI_U, "" + GLYPH_INIT_U);
        mInitialMap.put("" + UNI_U + FVS1, "" + GLYPH_INIT_U_FVS1);
        mInitialMap.put("" + UNI_OE, "" + GLYPH_INIT_OE);
        mInitialMap.put("" + UNI_UE, "" + GLYPH_INIT_UE);
        mInitialMap.put("" + UNI_EE, "" + GLYPH_INIT_EE);
        mInitialMap.put("" + UNI_EE + FVS1, "" + GLYPH_INIT_EE_FVS1);
        mInitialMap.put("" + UNI_UE + FVS1, "" + GLYPH_INIT_UE_FVS1);
        mInitialMap.put("" + UNI_NA, "" + GLYPH_INIT_NA);
        // TODO when is UNI_NA + FVS1 ever used?
        mInitialMap.put("" + UNI_NA + FVS1, "" + GLYPH_INIT_NA_FVS1);
        mInitialMap.put("" + UNI_ANG, "" + GLYPH_INIT_ANG);
        mInitialMap.put("" + UNI_BA, "" + GLYPH_INIT_BA);
        mInitialMap.put("" + UNI_BA + UNI_A, "" + GLYPH_INIT_BA_MEDI_A);
        mInitialMap.put("" + UNI_BA + UNI_E, "" + GLYPH_INIT_BA_MEDI_E);
        mInitialMap.put("" + UNI_BA + UNI_I, "" + GLYPH_INIT_BA_MEDI_I);
        mInitialMap.put("" + UNI_BA + UNI_O, "" + GLYPH_INIT_BA_MEDI_O);
        mInitialMap.put("" + UNI_BA + UNI_U, "" + GLYPH_INIT_BA_MEDI_U);
        mInitialMap.put("" + UNI_BA + UNI_OE, "" + GLYPH_INIT_BA_MEDI_OE);
        mInitialMap.put("" + UNI_BA + UNI_UE, "" + GLYPH_INIT_BA_MEDI_UE);
        mInitialMap.put("" + UNI_BA + UNI_EE, "" + GLYPH_INIT_BA_MEDI_EE);
        mInitialMap.put("" + UNI_PA, "" + GLYPH_INIT_PA);
        mInitialMap.put("" + UNI_PA + UNI_A, "" + GLYPH_INIT_PA_MEDI_A);
        mInitialMap.put("" + UNI_PA + UNI_E, "" + GLYPH_INIT_PA_MEDI_E);
        mInitialMap.put("" + UNI_PA + UNI_I, "" + GLYPH_INIT_PA_MEDI_I);
        mInitialMap.put("" + UNI_PA + UNI_O, "" + GLYPH_INIT_PA_MEDI_O);
        mInitialMap.put("" + UNI_PA + UNI_U, "" + GLYPH_INIT_PA_MEDI_U);
        mInitialMap.put("" + UNI_PA + UNI_OE, "" + GLYPH_INIT_PA_MEDI_OE);
        mInitialMap.put("" + UNI_PA + UNI_UE, "" + GLYPH_INIT_PA_MEDI_UE);
        mInitialMap.put("" + UNI_PA + UNI_EE, "" + GLYPH_INIT_PA_MEDI_EE);
        mInitialMap.put("" + UNI_QA, "" + GLYPH_INIT_QA);
        mInitialMap.put("" + UNI_QA + FVS1, "" + GLYPH_INIT_QA_FVS1);
        mInitialMap.put("" + UNI_QA + UNI_E, "" + GLYPH_INIT_QA_MEDI_E);
        mInitialMap.put("" + UNI_QA + UNI_I, "" + GLYPH_INIT_QA_MEDI_I);
        mInitialMap.put("" + UNI_QA + UNI_OE, "" + GLYPH_INIT_QA_MEDI_OE);
        mInitialMap.put("" + UNI_QA + UNI_UE, "" + GLYPH_INIT_QA_MEDI_UE);
        mInitialMap.put("" + UNI_QA + UNI_EE, "" + GLYPH_INIT_QA_MEDI_EE);
        mInitialMap.put("" + UNI_GA, "" + GLYPH_INIT_GA);
        mInitialMap.put("" + UNI_GA + FVS1, "" + GLYPH_INIT_GA_FVS1);
        mInitialMap.put("" + UNI_GA + UNI_E, "" + GLYPH_INIT_GA_MEDI_E);
        mInitialMap.put("" + UNI_GA + UNI_I, "" + GLYPH_INIT_GA_MEDI_I);
        mInitialMap.put("" + UNI_GA + UNI_OE, "" + GLYPH_INIT_GA_MEDI_OE);
        mInitialMap.put("" + UNI_GA + UNI_UE, "" + GLYPH_INIT_GA_MEDI_UE);
        mInitialMap.put("" + UNI_GA + UNI_EE, "" + GLYPH_INIT_GA_MEDI_EE);
        mInitialMap.put("" + UNI_MA, "" + GLYPH_INIT_MA);
        mInitialMap.put("" + UNI_LA, "" + GLYPH_INIT_LA);
        mInitialMap.put("" + UNI_SA, "" + GLYPH_INIT_SA);
        mInitialMap.put("" + UNI_SHA, "" + GLYPH_INIT_SHA);
        mInitialMap.put("" + UNI_TA, "" + GLYPH_INIT_TA);
        mInitialMap.put("" + UNI_DA, "" + GLYPH_INIT_DA);
        mInitialMap.put("" + UNI_DA + FVS1, "" + GLYPH_INIT_DA_FVS1);
        mInitialMap.put("" + UNI_CHA, "" + GLYPH_INIT_CHA);
        mInitialMap.put("" + UNI_JA, "" + GLYPH_INIT_JA);
        mInitialMap.put("" + UNI_YA, "" + GLYPH_INIT_YA);
        mInitialMap.put("" + UNI_YA + FVS1, "" + GLYPH_INIT_YA_FVS1);
        mInitialMap.put("" + UNI_RA, "" + GLYPH_INIT_RA);
        mInitialMap.put("" + UNI_WA, "" + GLYPH_INIT_WA);
        mInitialMap.put("" + UNI_FA, "" + GLYPH_INIT_FA);
        mInitialMap.put("" + UNI_FA + UNI_A, "" + GLYPH_INIT_FA_MEDI_A);
        mInitialMap.put("" + UNI_FA + UNI_E, "" + GLYPH_INIT_FA_MEDI_E);
        mInitialMap.put("" + UNI_FA + UNI_I, "" + GLYPH_INIT_FA_MEDI_I);
        mInitialMap.put("" + UNI_FA + UNI_O, "" + GLYPH_INIT_FA_MEDI_O);
        mInitialMap.put("" + UNI_FA + UNI_U, "" + GLYPH_INIT_FA_MEDI_U);
        mInitialMap.put("" + UNI_FA + UNI_OE, "" + GLYPH_INIT_FA_MEDI_OE);
        mInitialMap.put("" + UNI_FA + UNI_UE, "" + GLYPH_INIT_FA_MEDI_UE);
        mInitialMap.put("" + UNI_FA + UNI_EE, "" + GLYPH_INIT_FA_MEDI_EE);
        mInitialMap.put("" + UNI_KA, "" + GLYPH_INIT_KA);
        mInitialMap.put("" + UNI_KA + UNI_A, "" + GLYPH_INIT_KA_MEDI_A);
        mInitialMap.put("" + UNI_KA + UNI_E, "" + GLYPH_INIT_KA_MEDI_E);
        mInitialMap.put("" + UNI_KA + UNI_I, "" + GLYPH_INIT_KA_MEDI_I);
        mInitialMap.put("" + UNI_KA + UNI_O, "" + GLYPH_INIT_KA_MEDI_O);
        mInitialMap.put("" + UNI_KA + UNI_U, "" + GLYPH_INIT_KA_MEDI_U);
        mInitialMap.put("" + UNI_KA + UNI_OE, "" + GLYPH_INIT_KA_MEDI_OE);
        mInitialMap.put("" + UNI_KA + UNI_UE, "" + GLYPH_INIT_KA_MEDI_UE);
        mInitialMap.put("" + UNI_KA + UNI_EE, "" + GLYPH_INIT_KA_MEDI_EE);
        mInitialMap.put("" + UNI_KHA, "" + GLYPH_INIT_KHA);
        mInitialMap.put("" + UNI_KHA + UNI_A, "" + GLYPH_INIT_KHA_MEDI_A);
        mInitialMap.put("" + UNI_KHA + UNI_E, "" + GLYPH_INIT_KHA_MEDI_E);
        mInitialMap.put("" + UNI_KHA + UNI_I, "" + GLYPH_INIT_KHA_MEDI_I);
        mInitialMap.put("" + UNI_KHA + UNI_O, "" + GLYPH_INIT_KHA_MEDI_O);
        mInitialMap.put("" + UNI_KHA + UNI_U, "" + GLYPH_INIT_KHA_MEDI_U);
        mInitialMap.put("" + UNI_KHA + UNI_OE, "" + GLYPH_INIT_KHA_MEDI_OE);
        mInitialMap.put("" + UNI_KHA + UNI_UE, "" + GLYPH_INIT_KHA_MEDI_UE);
        mInitialMap.put("" + UNI_KHA + UNI_EE, "" + GLYPH_INIT_KHA_MEDI_EE);
        mInitialMap.put("" + UNI_TSA, "" + GLYPH_INIT_TSA);
        mInitialMap.put("" + UNI_ZA, "" + GLYPH_INIT_ZA);
        mInitialMap.put("" + UNI_HAA, "" + GLYPH_INIT_HAA);
        mInitialMap.put("" + UNI_ZRA, "" + GLYPH_INIT_ZRA);
        mInitialMap.put("" + UNI_LHA, "" + GLYPH_INIT_LHA);
        mInitialMap.put("" + UNI_ZHI, "" + GLYPH_INIT_ZHI);
        mInitialMap.put("" + UNI_CHI, "" + GLYPH_INIT_CHI);
        mInitialMap.put("" + UNI_QA + UNI_OE + FVS1, "" + GLYPH_INIT_QA_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_QA + UNI_UE + FVS1, "" + GLYPH_INIT_QA_MEDI_UE_FVS1);
        mInitialMap.put("" + UNI_GA + UNI_OE + FVS1, "" + GLYPH_INIT_GA_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_GA + UNI_UE + FVS1, "" + GLYPH_INIT_GA_MEDI_UE_FVS1);
        mInitialMap.put("" + UNI_QA + FVS2, "" + GLYPH_INIT_QA_FVS2);
        mInitialMap.put("" + UNI_QA + FVS3, "" + GLYPH_INIT_QA_FVS3);
        mInitialMap.put("" + UNI_QA + FVS1 + UNI_E, "" + GLYPH_INIT_QA_FVS1_MEDI_E);
        mInitialMap.put("" + UNI_QA + FVS1 + UNI_I, "" + GLYPH_INIT_QA_FVS1_MEDI_I);
        mInitialMap.put("" + UNI_QA + FVS1 + UNI_OE, "" + GLYPH_INIT_QA_FVS1_MEDI_OE);
        mInitialMap.put("" + UNI_QA + FVS1 + UNI_UE, "" + GLYPH_INIT_QA_FVS1_MEDI_UE);
        mInitialMap.put("" + UNI_QA + FVS1 + UNI_EE, "" + GLYPH_INIT_QA_FVS1_MEDI_EE);
        mInitialMap.put("" + UNI_GA + FVS2, "" + GLYPH_INIT_GA_FVS2);
        mInitialMap.put("" + UNI_GA + FVS3, "" + GLYPH_INIT_GA_FVS3);
        mInitialMap.put("" + UNI_GA + FVS1 + UNI_E, "" + GLYPH_INIT_GA_FVS1_MEDI_E);
        mInitialMap.put("" + UNI_GA + FVS1 + UNI_I, "" + GLYPH_INIT_GA_FVS1_MEDI_I);
        mInitialMap.put("" + UNI_GA + FVS1 + UNI_OE, "" + GLYPH_INIT_GA_FVS1_MEDI_OE);
        mInitialMap.put("" + UNI_GA + FVS1 + UNI_UE, "" + GLYPH_INIT_GA_FVS1_MEDI_UE);
        mInitialMap.put("" + UNI_GA + FVS1 + UNI_EE, "" + GLYPH_INIT_GA_FVS1_MEDI_EE);
        mInitialMap.put("" + UNI_BA + UNI_OE + FVS1, "" + GLYPH_INIT_BA_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_BA + UNI_UE + FVS1, "" + GLYPH_INIT_BA_MEDI_UE_FVS1);
        mInitialMap.put("" + UNI_PA + UNI_OE + FVS1, "" + GLYPH_INIT_PA_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_PA + UNI_UE + FVS1, "" + GLYPH_INIT_PA_MEDI_UE_FVS1);
        mInitialMap.put("" + UNI_QA + FVS1 + UNI_OE + FVS1, "" + GLYPH_INIT_QA_FVS1_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_QA + FVS1 + UNI_UE + FVS1, "" + GLYPH_INIT_QA_FVS1_MEDI_UE_FVS1);
        mInitialMap.put("" + UNI_GA + FVS1 + UNI_OE + FVS1, "" + GLYPH_INIT_GA_FVS1_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_GA + FVS1 + UNI_UE + FVS1, "" + GLYPH_INIT_GA_FVS1_MEDI_UE_FVS1);
        mInitialMap.put("" + UNI_FA + UNI_OE + FVS1, "" + GLYPH_INIT_FA_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_FA + UNI_UE + FVS1, "" + GLYPH_INIT_FA_MEDI_UE_FVS1);
        mInitialMap.put("" + UNI_KA + UNI_OE + FVS1, "" + GLYPH_INIT_KA_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_KA + UNI_UE + FVS1, "" + GLYPH_INIT_KA_MEDI_UE_FVS1);
        mInitialMap.put("" + UNI_KHA + UNI_OE + FVS1, "" + GLYPH_INIT_KHA_MEDI_OE_FVS1);
        mInitialMap.put("" + UNI_KHA + UNI_UE + FVS1, "" + GLYPH_INIT_KHA_MEDI_UE_FVS1);

        // Non-ligature OE/UE in first syllable (long tooth rule)
        /*mInitialMap.put("" + UNI_NA + UNI_OE, "" + GLYPH_INIT_NA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_NA + UNI_UE, "" + GLYPH_INIT_NA + GLYPH_MEDI_UE_FVS1);
		// TODO when is UNI_NA + FVS1 ever used?
		mInitialMap.put("" + UNI_NA + FVS1 + UNI_OE, "" + GLYPH_INIT_NA_FVS1 + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_NA + FVS1 + UNI_UE, "" + GLYPH_INIT_NA_FVS1 + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_ANG + UNI_OE, "" + GLYPH_INIT_NA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_ANG + UNI_UE, "" + GLYPH_INIT_NA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_MA + UNI_OE, "" + GLYPH_INIT_MA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_MA + UNI_UE, "" + GLYPH_INIT_MA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_LA + UNI_OE, "" + GLYPH_INIT_LA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_LA + UNI_UE, "" + GLYPH_INIT_LA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_SA + UNI_OE, "" + GLYPH_INIT_SA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_SA + UNI_UE, "" + GLYPH_INIT_SA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_SHA + UNI_OE, "" + GLYPH_INIT_SHA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_SHA + UNI_UE, "" + GLYPH_INIT_SHA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_TA + UNI_OE, "" + GLYPH_INIT_TA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_TA + UNI_UE, "" + GLYPH_INIT_TA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_DA + UNI_OE, "" + GLYPH_INIT_DA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_DA + UNI_UE, "" + GLYPH_INIT_DA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_DA + FVS1 + UNI_OE, "" + GLYPH_INIT_DA_FVS1 + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_DA + FVS1 + UNI_UE, "" + GLYPH_INIT_DA_FVS1 + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_CHA + UNI_OE, "" + GLYPH_INIT_CHA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_CHA + UNI_UE, "" + GLYPH_INIT_CHA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_JA + UNI_OE, "" + GLYPH_INIT_JA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_JA + UNI_UE, "" + GLYPH_INIT_JA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_YA + UNI_OE, "" + GLYPH_INIT_YA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_YA + UNI_UE, "" + GLYPH_INIT_YA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_RA + UNI_OE, "" + GLYPH_INIT_RA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_RA + UNI_UE, "" + GLYPH_INIT_RA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_WA + UNI_OE, "" + GLYPH_INIT_WA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_WA + UNI_UE, "" + GLYPH_INIT_WA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_TSA + UNI_OE, "" + GLYPH_INIT_TSA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_TSA + UNI_UE, "" + GLYPH_INIT_TSA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_ZA + UNI_OE, "" + GLYPH_INIT_ZA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_ZA + UNI_UE, "" + GLYPH_INIT_ZA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_HAA + UNI_OE, "" + GLYPH_INIT_HAA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_HAA + UNI_UE, "" + GLYPH_INIT_HAA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_ZRA + UNI_OE, "" + GLYPH_INIT_ZRA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_ZRA + UNI_UE, "" + GLYPH_INIT_ZRA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_LHA + UNI_OE, "" + GLYPH_INIT_LHA + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_LHA + UNI_UE, "" + GLYPH_INIT_LHA + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_ZHI + UNI_OE, "" + GLYPH_INIT_ZHI + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_ZHI + UNI_UE, "" + GLYPH_INIT_ZHI + GLYPH_MEDI_UE_FVS1);
		mInitialMap.put("" + UNI_CHI + UNI_OE, "" + GLYPH_INIT_CHI + GLYPH_MEDI_OE_FVS1);
		mInitialMap.put("" + UNI_CHI + UNI_UE, "" + GLYPH_INIT_CHI + GLYPH_MEDI_UE_FVS1);*/

        // Catch other chars
        mInitialMap.put("" + CURSOR_HOLDER, "" + CURSOR_HOLDER);
        mInitialMap.put("" + MONGOLIAN_NIRUGU, "" + GLYPH_NIRUGU);
        mInitialMap.put("" + ZWJ, "");
        mInitialMap.put("" + NNBS, "" + NNBS);
        mInitialMap.put("" + MVS, "");
        mInitialMap.put("" + FVS1, "");
        mInitialMap.put("" + FVS2, "");
        mInitialMap.put("" + FVS3, "");

    }

    private void initMedial() {

        // NOTE: assuming MAXIMUM_SEARCH_LENGTH = 4

        mMedialMap = new HashMap<String, String>();

        mMedialMap.put("" + UNI_A, "" + GLYPH_MEDI_A);
        mMedialMap.put("" + UNI_A + FVS1, "" + GLYPH_MEDI_A_FVS1);
        mMedialMap.put("" + UNI_E, "" + GLYPH_MEDI_E);
        mMedialMap.put("" + UNI_I, "" + GLYPH_MEDI_I);
        mMedialMap.put("" + UNI_I + FVS1, "" + GLYPH_MEDI_I_FVS1);
        mMedialMap.put("" + UNI_I + FVS2, "" + GLYPH_MEDI_I_FVS2);
        mMedialMap.put("" + UNI_I + FVS3, "" + GLYPH_MEDI_I_FVS3);
        mMedialMap.put("" + UNI_O, "" + GLYPH_MEDI_O);
        mMedialMap.put("" + UNI_O + FVS1, "" + GLYPH_MEDI_O_FVS1);
        mMedialMap.put("" + UNI_U, "" + GLYPH_MEDI_U);
        mMedialMap.put("" + UNI_U + FVS1, "" + GLYPH_MEDI_U_FVS1);
        mMedialMap.put("" + UNI_OE, "" + GLYPH_MEDI_OE);
        mMedialMap.put("" + UNI_OE + FVS1, "" + GLYPH_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_OE + FVS2, "" + GLYPH_MEDI_OE_FVS2);
        mMedialMap.put("" + UNI_UE, "" + GLYPH_MEDI_UE);
        mMedialMap.put("" + UNI_UE + FVS1, "" + GLYPH_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_UE + FVS2, "" + GLYPH_MEDI_UE_FVS2);
        mMedialMap.put("" + UNI_EE, "" + GLYPH_MEDI_EE);
        mMedialMap.put("" + UNI_NA, "" + GLYPH_MEDI_NA);
        mMedialMap.put("" + UNI_NA + FVS1, "" + GLYPH_MEDI_NA_FVS1);
        // TODO GLYPH_MEDI_NA_FVS2 (long stemmed NA) is undefinied in Almas font
        // ignoring for now. (it is only used in Todo script)
        // mMedialMap.put("" + UNI_NA + FVS2, "" + GLYPH_MEDI_NA_FVS1);
        // TODO !!! NON-STANDARD !!!
        // TODO GLYPH_MEDI_NA_FVS3 is undefinied in Unicode
        // using GLYPH_MEDI_NA as a substitute for now.
        // needed to over-ride context in name like Cholmon-Odo
        mMedialMap.put("" + UNI_NA + FVS2, "" + GLYPH_MEDI_NA_FVS2);
        mMedialMap.put("" + UNI_ANG, "" + GLYPH_MEDI_ANG);
        mMedialMap.put("" + UNI_ANG + UNI_QA, "" + GLYPH_MEDI_ANG_MEDI_QA);
        mMedialMap.put("" + UNI_ANG + UNI_GA, "" + GLYPH_MEDI_ANG_MEDI_GA);
        mMedialMap.put("" + UNI_ANG + UNI_QA + FVS1, "" + GLYPH_MEDI_ANG_MEDI_QA);
        mMedialMap.put("" + UNI_ANG + UNI_GA + FVS1, "" + GLYPH_MEDI_ANG_MEDI_GA);
        mMedialMap.put("" + UNI_ANG + UNI_MA, "" + GLYPH_MEDI_ANG_MEDI_MA);
        mMedialMap.put("" + UNI_ANG + UNI_LA, "" + GLYPH_MEDI_ANG_MEDI_LA);
        mMedialMap.put("" + UNI_ANG + UNI_NA + FVS1, "" + GLYPH_MEDI_ANG_MEDI_NA_FVS1);

        mMedialMap.put("" + UNI_BA, "" + GLYPH_MEDI_BA);
        mMedialMap.put("" + UNI_BA + UNI_A, "" + GLYPH_MEDI_BA_MEDI_A);
        mMedialMap.put("" + UNI_BA + UNI_E, "" + GLYPH_MEDI_BA_MEDI_E);
        mMedialMap.put("" + UNI_BA + UNI_I, "" + GLYPH_MEDI_BA_MEDI_I);
        mMedialMap.put("" + UNI_BA + UNI_O, "" + GLYPH_MEDI_BA_MEDI_O);
        mMedialMap.put("" + UNI_BA + UNI_U, "" + GLYPH_MEDI_BA_MEDI_U);
        mMedialMap.put("" + UNI_BA + UNI_OE, "" + GLYPH_MEDI_BA_MEDI_OE);
        mMedialMap.put("" + UNI_BA + UNI_UE, "" + GLYPH_MEDI_BA_MEDI_UE);
        mMedialMap.put("" + UNI_BA + UNI_EE, "" + GLYPH_MEDI_BA_MEDI_EE);
        mMedialMap.put("" + UNI_BA + UNI_MA, "" + GLYPH_MEDI_BA_MEDI_MA);
        mMedialMap.put("" + UNI_BA + UNI_LA, "" + GLYPH_MEDI_BA_MEDI_LA);
        mMedialMap.put("" + UNI_BA + UNI_OE + FVS1, "" + GLYPH_MEDI_BA_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_BA + UNI_UE + FVS1, "" + GLYPH_MEDI_BA_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_BA + UNI_QA, "" + GLYPH_MEDI_BA_MEDI_QA);
        mMedialMap.put("" + UNI_BA + UNI_GA, "" + GLYPH_MEDI_BA_MEDI_GA);
        mMedialMap.put("" + UNI_BA + UNI_NA + FVS1, "" + GLYPH_MEDI_BA_MEDI_NA_FVS1);
        mMedialMap.put("" + UNI_PA, "" + GLYPH_MEDI_PA);
        mMedialMap.put("" + UNI_PA + UNI_A, "" + GLYPH_MEDI_PA_MEDI_A);
        mMedialMap.put("" + UNI_PA + UNI_E, "" + GLYPH_MEDI_PA_MEDI_E);
        mMedialMap.put("" + UNI_PA + UNI_I, "" + GLYPH_MEDI_PA_MEDI_I);
        mMedialMap.put("" + UNI_PA + UNI_O, "" + GLYPH_MEDI_PA_MEDI_O);
        mMedialMap.put("" + UNI_PA + UNI_U, "" + GLYPH_MEDI_PA_MEDI_U);
        mMedialMap.put("" + UNI_PA + UNI_OE, "" + GLYPH_MEDI_PA_MEDI_OE);
        mMedialMap.put("" + UNI_PA + UNI_UE, "" + GLYPH_MEDI_PA_MEDI_UE);
        mMedialMap.put("" + UNI_PA + UNI_EE, "" + GLYPH_MEDI_PA_MEDI_EE);
        mMedialMap.put("" + UNI_PA + UNI_MA, "" + GLYPH_MEDI_PA_MEDI_MA);
        mMedialMap.put("" + UNI_PA + UNI_LA, "" + GLYPH_MEDI_PA_MEDI_LA);
        mMedialMap.put("" + UNI_PA + UNI_OE + FVS1, "" + GLYPH_MEDI_PA_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_PA + UNI_UE + FVS1, "" + GLYPH_MEDI_PA_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_PA + UNI_QA, "" + GLYPH_MEDI_PA_MEDI_QA);
        mMedialMap.put("" + UNI_PA + UNI_GA, "" + GLYPH_MEDI_PA_MEDI_GA);
        mMedialMap.put("" + UNI_PA + UNI_NA + FVS1, "" + GLYPH_MEDI_PA_MEDI_NA_FVS1);
        mMedialMap.put("" + UNI_QA, "" + GLYPH_MEDI_QA);
        mMedialMap.put("" + UNI_QA + FVS1, "" + GLYPH_MEDI_QA_FVS1);
        mMedialMap.put("" + UNI_QA + FVS2, "" + GLYPH_MEDI_QA_FVS2);
        mMedialMap.put("" + UNI_QA + UNI_E, "" + GLYPH_MEDI_QA_MEDI_E);
        mMedialMap.put("" + UNI_QA + UNI_I, "" + GLYPH_MEDI_QA_MEDI_I);
        mMedialMap.put("" + UNI_QA + UNI_OE, "" + GLYPH_MEDI_QA_MEDI_OE);
        mMedialMap.put("" + UNI_QA + UNI_UE, "" + GLYPH_MEDI_QA_MEDI_UE);
        mMedialMap.put("" + UNI_QA + UNI_EE, "" + GLYPH_MEDI_QA_MEDI_EE);
        mMedialMap.put("" + UNI_QA + UNI_OE + FVS1, "" + GLYPH_MEDI_QA_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_QA + UNI_UE + FVS1, "" + GLYPH_MEDI_QA_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_QA + FVS3, "" + GLYPH_MEDI_QA_FVS3);
        mMedialMap.put("" + UNI_QA + FVS1 + UNI_E, "" + GLYPH_MEDI_QA_FVS1_MEDI_E);
        mMedialMap.put("" + UNI_QA + FVS1 + UNI_I, "" + GLYPH_MEDI_QA_FVS1_MEDI_I);
        mMedialMap.put("" + UNI_QA + FVS1 + UNI_OE, "" + GLYPH_MEDI_QA_FVS1_MEDI_OE);
        mMedialMap.put("" + UNI_QA + FVS1 + UNI_UE, "" + GLYPH_MEDI_QA_FVS1_MEDI_UE);
        mMedialMap.put("" + UNI_QA + FVS1 + UNI_EE, "" + GLYPH_MEDI_QA_FVS1_MEDI_EE);
        mMedialMap.put("" + UNI_QA + FVS1 + UNI_OE + FVS1, "" + GLYPH_MEDI_QA_FVS1_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_QA + FVS1 + UNI_UE + FVS1, "" + GLYPH_MEDI_QA_FVS1_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_GA, "" + GLYPH_MEDI_GA);
        mMedialMap.put("" + UNI_GA + FVS1, "" + GLYPH_MEDI_GA_FVS1);
        mMedialMap.put("" + UNI_GA + FVS2, "" + GLYPH_MEDI_GA_FVS2);
        mMedialMap.put("" + UNI_GA + FVS3, "" + GLYPH_MEDI_GA_FVS3);
        mMedialMap.put("" + UNI_GA + UNI_E, "" + GLYPH_MEDI_GA_MEDI_E);
        mMedialMap.put("" + UNI_GA + UNI_I, "" + GLYPH_MEDI_GA_MEDI_I);
        mMedialMap.put("" + UNI_GA + UNI_OE, "" + GLYPH_MEDI_GA_MEDI_OE);
        mMedialMap.put("" + UNI_GA + UNI_UE, "" + GLYPH_MEDI_GA_MEDI_UE);
        mMedialMap.put("" + UNI_GA + UNI_EE, "" + GLYPH_MEDI_GA_MEDI_EE);
        mMedialMap.put("" + UNI_GA + FVS3 + UNI_MA, "" + GLYPH_MEDI_GA_MEDI_MA);
        mMedialMap.put("" + UNI_GA + FVS3 + UNI_LA, "" + GLYPH_MEDI_GA_MEDI_LA);
        mMedialMap.put("" + UNI_GA + UNI_OE + FVS1, "" + GLYPH_MEDI_GA_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_GA + UNI_UE + FVS1, "" + GLYPH_MEDI_GA_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_GA + FVS3 + UNI_NA + FVS1, "" + GLYPH_MEDI_GA_MEDI_NA_FVS1);
        mMedialMap.put("" + UNI_GA + FVS1 + UNI_E, "" + GLYPH_MEDI_GA_FVS1_MEDI_E);
        mMedialMap.put("" + UNI_GA + FVS1 + UNI_I, "" + GLYPH_MEDI_GA_FVS1_MEDI_I);
        mMedialMap.put("" + UNI_GA + FVS1 + UNI_OE, "" + GLYPH_MEDI_GA_FVS1_MEDI_OE);
        mMedialMap.put("" + UNI_GA + FVS1 + UNI_UE, "" + GLYPH_MEDI_GA_FVS1_MEDI_UE);
        mMedialMap.put("" + UNI_GA + FVS1 + UNI_EE, "" + GLYPH_MEDI_GA_FVS1_MEDI_EE);
        mMedialMap.put("" + UNI_GA + FVS1 + UNI_OE + FVS1, "" + GLYPH_MEDI_GA_FVS1_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_GA + FVS1 + UNI_UE + FVS1, "" + GLYPH_MEDI_GA_FVS1_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_MA, "" + GLYPH_MEDI_MA);
        mMedialMap.put("" + UNI_MA + UNI_MA, "" + GLYPH_MEDI_MA_MEDI_MA);
        mMedialMap.put("" + UNI_MA + UNI_LA, "" + GLYPH_MEDI_MA_MEDI_LA);
        mMedialMap.put("" + UNI_LA, "" + GLYPH_MEDI_LA);
        mMedialMap.put("" + UNI_LA + UNI_LA, "" + GLYPH_MEDI_LA_MEDI_LA);
        mMedialMap.put("" + UNI_SA, "" + GLYPH_MEDI_SA);
        mMedialMap.put("" + UNI_SHA, "" + GLYPH_MEDI_SHA);
        mMedialMap.put("" + UNI_TA, "" + GLYPH_MEDI_TA);
        mMedialMap.put("" + UNI_TA + FVS1, "" + GLYPH_MEDI_TA_FVS1);
        mMedialMap.put("" + UNI_TA + FVS2, "" + GLYPH_MEDI_TA_FVS2);
        mMedialMap.put("" + UNI_DA, "" + GLYPH_MEDI_DA);
        mMedialMap.put("" + UNI_DA + FVS1, "" + GLYPH_MEDI_DA_FVS1);
        mMedialMap.put("" + UNI_CHA, "" + GLYPH_MEDI_CHA);
        mMedialMap.put("" + UNI_JA, "" + GLYPH_MEDI_JA);
        mMedialMap.put("" + UNI_YA, "" + GLYPH_MEDI_YA);
        mMedialMap.put("" + UNI_YA + FVS1, "" + GLYPH_MEDI_YA_FVS1);
        mMedialMap.put("" + UNI_RA, "" + GLYPH_MEDI_RA);
        mMedialMap.put("" + UNI_WA, "" + GLYPH_MEDI_WA);
        mMedialMap.put("" + UNI_WA + FVS1, "" + GLYPH_MEDI_WA_FVS1);
        mMedialMap.put("" + UNI_FA, "" + GLYPH_MEDI_FA);
        mMedialMap.put("" + UNI_FA + UNI_A, "" + GLYPH_MEDI_FA_MEDI_A);
        mMedialMap.put("" + UNI_FA + UNI_E, "" + GLYPH_MEDI_FA_MEDI_E);
        mMedialMap.put("" + UNI_FA + UNI_I, "" + GLYPH_MEDI_FA_MEDI_I);
        mMedialMap.put("" + UNI_FA + UNI_O, "" + GLYPH_MEDI_FA_MEDI_O);
        mMedialMap.put("" + UNI_FA + UNI_U, "" + GLYPH_MEDI_FA_MEDI_U);
        mMedialMap.put("" + UNI_FA + UNI_OE, "" + GLYPH_MEDI_FA_MEDI_OE);
        mMedialMap.put("" + UNI_FA + UNI_UE, "" + GLYPH_MEDI_FA_MEDI_UE);
        mMedialMap.put("" + UNI_FA + UNI_EE, "" + GLYPH_MEDI_FA_MEDI_EE);
        mMedialMap.put("" + UNI_FA + UNI_MA, "" + GLYPH_MEDI_FA_MEDI_MA);
        mMedialMap.put("" + UNI_FA + UNI_LA, "" + GLYPH_MEDI_FA_MEDI_LA);
        mMedialMap.put("" + UNI_FA + UNI_OE + FVS1, "" + GLYPH_MEDI_FA_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_FA + UNI_UE + FVS1, "" + GLYPH_MEDI_FA_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_FA + UNI_QA, "" + GLYPH_MEDI_FA_MEDI_QA);
        mMedialMap.put("" + UNI_FA + UNI_GA, "" + GLYPH_MEDI_FA_MEDI_GA);
        mMedialMap.put("" + UNI_FA + UNI_NA + FVS1, "" + GLYPH_MEDI_FA_MEDI_NA_FVS1);
        mMedialMap.put("" + UNI_KA, "" + GLYPH_MEDI_KA);
        mMedialMap.put("" + UNI_KA + UNI_A, "" + GLYPH_MEDI_KA_MEDI_A);
        mMedialMap.put("" + UNI_KA + UNI_E, "" + GLYPH_MEDI_KA_MEDI_E);
        mMedialMap.put("" + UNI_KA + UNI_I, "" + GLYPH_MEDI_KA_MEDI_I);
        mMedialMap.put("" + UNI_KA + UNI_O, "" + GLYPH_MEDI_KA_MEDI_O);
        mMedialMap.put("" + UNI_KA + UNI_U, "" + GLYPH_MEDI_KA_MEDI_U);
        mMedialMap.put("" + UNI_KA + UNI_OE, "" + GLYPH_MEDI_KA_MEDI_OE);
        mMedialMap.put("" + UNI_KA + UNI_UE, "" + GLYPH_MEDI_KA_MEDI_UE);
        mMedialMap.put("" + UNI_KA + UNI_EE, "" + GLYPH_MEDI_KA_MEDI_EE);
        mMedialMap.put("" + UNI_KA + UNI_MA, "" + GLYPH_MEDI_KA_MEDI_MA);
        mMedialMap.put("" + UNI_KA + UNI_LA, "" + GLYPH_MEDI_KA_MEDI_LA);
        mMedialMap.put("" + UNI_KA + UNI_OE + FVS1, "" + GLYPH_MEDI_KA_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_KA + UNI_UE + FVS1, "" + GLYPH_MEDI_KA_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_KA + UNI_QA, "" + GLYPH_MEDI_KA_MEDI_QA);
        mMedialMap.put("" + UNI_KA + UNI_GA, "" + GLYPH_MEDI_KA_MEDI_GA);
        mMedialMap.put("" + UNI_KA + UNI_NA + FVS1, "" + GLYPH_MEDI_KA_MEDI_NA_FVS1);
        mMedialMap.put("" + UNI_KHA, "" + GLYPH_MEDI_KHA);
        mMedialMap.put("" + UNI_KHA + UNI_A, "" + GLYPH_MEDI_KHA_MEDI_A);
        mMedialMap.put("" + UNI_KHA + UNI_E, "" + GLYPH_MEDI_KHA_MEDI_E);
        mMedialMap.put("" + UNI_KHA + UNI_I, "" + GLYPH_MEDI_KHA_MEDI_I);
        mMedialMap.put("" + UNI_KHA + UNI_O, "" + GLYPH_MEDI_KHA_MEDI_O);
        mMedialMap.put("" + UNI_KHA + UNI_U, "" + GLYPH_MEDI_KHA_MEDI_U);
        mMedialMap.put("" + UNI_KHA + UNI_OE, "" + GLYPH_MEDI_KHA_MEDI_OE);
        mMedialMap.put("" + UNI_KHA + UNI_UE, "" + GLYPH_MEDI_KHA_MEDI_UE);
        mMedialMap.put("" + UNI_KHA + UNI_EE, "" + GLYPH_MEDI_KHA_MEDI_EE);
        mMedialMap.put("" + UNI_KHA + UNI_MA, "" + GLYPH_MEDI_KHA_MEDI_MA);
        mMedialMap.put("" + UNI_KHA + UNI_LA, "" + GLYPH_MEDI_KHA_MEDI_LA);
        mMedialMap.put("" + UNI_KHA + UNI_OE + FVS1, "" + GLYPH_MEDI_KHA_MEDI_OE_FVS1);
        mMedialMap.put("" + UNI_KHA + UNI_UE + FVS1, "" + GLYPH_MEDI_KHA_MEDI_UE_FVS1);
        mMedialMap.put("" + UNI_KHA + UNI_QA, "" + GLYPH_MEDI_KHA_MEDI_QA);
        mMedialMap.put("" + UNI_KHA + UNI_GA, "" + GLYPH_MEDI_KHA_MEDI_GA);
        mMedialMap.put("" + UNI_KHA + UNI_NA + FVS1, "" + GLYPH_MEDI_KHA_MEDI_NA_FVS1);
        mMedialMap.put("" + UNI_TSA, "" + GLYPH_MEDI_TSA);
        mMedialMap.put("" + UNI_ZA, "" + GLYPH_MEDI_ZA);
        mMedialMap.put("" + UNI_HAA, "" + GLYPH_MEDI_HAA);
        mMedialMap.put("" + UNI_ZRA, "" + GLYPH_MEDI_ZRA);
        mMedialMap.put("" + UNI_LHA, "" + GLYPH_MEDI_LHA);
        mMedialMap.put("" + UNI_ZHI, "" + GLYPH_MEDI_ZHI);
        mMedialMap.put("" + UNI_CHI, "" + GLYPH_MEDI_CHI);

        // MVS
        mMedialMap.put("" + UNI_NA + MVS, "" + GLYPH_FINA_NA_FVS1);
        mMedialMap.put("" + UNI_ANG + UNI_QA + MVS, "" + GLYPH_MEDI_ANG_FINA_QA);
        mMedialMap.put("" + UNI_ANG + FVS1 + UNI_QA + MVS, "" + GLYPH_MEDI_ANG_FINA_QA);
        mMedialMap.put("" + UNI_ANG + FVS1 + UNI_GA + MVS, "" + GLYPH_MEDI_ANG_FINA_GA);
        mMedialMap.put("" + UNI_ANG + UNI_GA + MVS, "" + GLYPH_MEDI_ANG_FINA_GA);
        mMedialMap.put("" + UNI_BA + MVS, "" + GLYPH_FINA_BA);
        mMedialMap.put("" + UNI_PA + MVS, "" + GLYPH_FINA_PA);
        mMedialMap.put("" + UNI_QA + MVS, "" + GLYPH_FINA_QA);
        mMedialMap.put("" + UNI_GA + MVS, "" + GLYPH_FINA_GA_FVS3);
        mMedialMap.put("" + UNI_MA + MVS, "" + GLYPH_FINA_MA);
        mMedialMap.put("" + UNI_LA + MVS, "" + GLYPH_FINA_LA);
        mMedialMap.put("" + UNI_SA + MVS, "" + GLYPH_FINA_SA);
        mMedialMap.put("" + UNI_SA + FVS1 + MVS, "" + GLYPH_FINA_SA_FVS1);
        mMedialMap.put("" + UNI_SHA + MVS, "" + GLYPH_FINA_SHA);
        mMedialMap.put("" + UNI_TA + MVS, "" + GLYPH_FINA_TA);
        mMedialMap.put("" + UNI_DA + MVS, "" + GLYPH_FINA_DA_FVS1);
        mMedialMap.put("" + UNI_CHA + MVS, "" + GLYPH_FINA_CHA);
        mMedialMap.put("" + UNI_JA + MVS, "" + GLYPH_FINA_JA_FVS1);
        mMedialMap.put("" + UNI_YA + MVS, "" + GLYPH_FINA_YA);
        mMedialMap.put("" + UNI_I + MVS, "" + GLYPH_FINA_YA); // I may be a substitute for YA
        mMedialMap.put("" + UNI_RA + MVS, "" + GLYPH_FINA_RA);
        mMedialMap.put("" + UNI_WA + MVS, "" + GLYPH_FINA_WA);
        mMedialMap.put("" + UNI_FA + MVS, "" + GLYPH_FINA_FA);
        mMedialMap.put("" + UNI_KA + MVS, "" + GLYPH_FINA_KA);
        mMedialMap.put("" + UNI_KHA + MVS, "" + GLYPH_FINA_KHA);
        mMedialMap.put("" + UNI_TSA + MVS, "" + GLYPH_FINA_TSA);
        mMedialMap.put("" + UNI_ZA + MVS, "" + GLYPH_FINA_ZA);
        mMedialMap.put("" + UNI_HAA + MVS, "" + GLYPH_FINA_HAA);
        mMedialMap.put("" + UNI_ZRA + MVS, "" + GLYPH_FINA_ZRA);
        mMedialMap.put("" + UNI_LHA + MVS, "" + GLYPH_FINA_LHA);
        mMedialMap.put("" + UNI_ZHI + MVS, "" + GLYPH_FINA_ZHI);
        mMedialMap.put("" + UNI_CHI + MVS, "" + GLYPH_FINA_CHI);

        // Catch other chars
        mMedialMap.put("" + CURSOR_HOLDER, "" + CURSOR_HOLDER);
        mMedialMap.put("" + MONGOLIAN_NIRUGU, "" + GLYPH_NIRUGU);
        mMedialMap.put("" + ZWJ, "");
        mMedialMap.put("" + NNBS, "" + NNBS);
        mMedialMap.put("" + MVS, "");
        mMedialMap.put("" + FVS1, "");
        mMedialMap.put("" + FVS2, "");
        mMedialMap.put("" + FVS3, "");

    }

    private void initFinal() {

        // NOTE: assuming MAXIMUM_SEARCH_LENGTH = 4

        mFinalMap = new HashMap<String, String>();

        mFinalMap.put("" + UNI_A, "" + GLYPH_FINA_A);
        mFinalMap.put("" + UNI_A + FVS1, "" + GLYPH_FINA_A_FVS1);
        mFinalMap.put("" + UNI_A + FVS2, "" + GLYPH_FINA_A_FVS2);
        mFinalMap.put("" + UNI_E, "" + GLYPH_FINA_E);
        mFinalMap.put("" + UNI_E + FVS1, "" + GLYPH_FINA_E_FVS1);
        mFinalMap.put("" + UNI_E + FVS2, "" + GLYPH_FINA_E_FVS2);
        mFinalMap.put("" + UNI_I, "" + GLYPH_FINA_I);
        mFinalMap.put("" + UNI_O, "" + GLYPH_FINA_O);
        mFinalMap.put("" + UNI_O + FVS1, "" + GLYPH_FINA_O_FVS1);
        mFinalMap.put("" + UNI_U, "" + GLYPH_FINA_U);
        mFinalMap.put("" + UNI_U + FVS1, "" + GLYPH_FINA_U_FVS1);
        mFinalMap.put("" + UNI_OE, "" + GLYPH_FINA_OE);
        mFinalMap.put("" + UNI_OE + FVS1, "" + GLYPH_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_UE, "" + GLYPH_FINA_UE);
        mFinalMap.put("" + UNI_UE + FVS1, "" + GLYPH_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_EE, "" + GLYPH_FINA_EE);
        mFinalMap.put("" + UNI_NA, "" + GLYPH_FINA_NA);
        mFinalMap.put("" + UNI_NA + FVS1, "" + GLYPH_FINA_NA_FVS1);
        mFinalMap.put("" + UNI_ANG, "" + GLYPH_FINA_ANG);
        mFinalMap.put("" + UNI_BA, "" + GLYPH_FINA_BA);
        mFinalMap.put("" + UNI_BA + UNI_A, "" + GLYPH_MEDI_BA_FINA_A);
        mFinalMap.put("" + UNI_BA + UNI_E, "" + GLYPH_MEDI_BA_FINA_E);
        mFinalMap.put("" + UNI_BA + UNI_I, "" + GLYPH_MEDI_BA_FINA_I);
        mFinalMap.put("" + UNI_BA + UNI_O, "" + GLYPH_MEDI_BA_FINA_O);
        mFinalMap.put("" + UNI_BA + UNI_U, "" + GLYPH_MEDI_BA_FINA_U);
        mFinalMap.put("" + UNI_BA + UNI_OE, "" + GLYPH_MEDI_BA_FINA_OE);
        mFinalMap.put("" + UNI_BA + UNI_OE + FVS1, "" + GLYPH_MEDI_BA_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_BA + UNI_UE, "" + GLYPH_MEDI_BA_FINA_UE);
        mFinalMap.put("" + UNI_BA + UNI_UE + FVS1, "" + GLYPH_MEDI_BA_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_BA + UNI_EE, "" + GLYPH_MEDI_BA_FINA_EE);
        mFinalMap.put("" + UNI_PA, "" + GLYPH_FINA_PA);
        mFinalMap.put("" + UNI_PA + UNI_A, "" + GLYPH_MEDI_PA_FINA_A);
        mFinalMap.put("" + UNI_PA + UNI_E, "" + GLYPH_MEDI_PA_FINA_E);
        mFinalMap.put("" + UNI_PA + UNI_I, "" + GLYPH_MEDI_PA_FINA_I);
        mFinalMap.put("" + UNI_PA + UNI_O, "" + GLYPH_MEDI_PA_FINA_O);
        mFinalMap.put("" + UNI_PA + UNI_U, "" + GLYPH_MEDI_PA_FINA_U);
        mFinalMap.put("" + UNI_PA + UNI_OE, "" + GLYPH_MEDI_PA_FINA_OE);
        mFinalMap.put("" + UNI_PA + UNI_OE + FVS1, "" + GLYPH_MEDI_PA_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_PA + UNI_UE, "" + GLYPH_MEDI_PA_FINA_UE);
        mFinalMap.put("" + UNI_PA + UNI_UE + FVS1, "" + GLYPH_MEDI_PA_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_PA + UNI_EE, "" + GLYPH_MEDI_PA_FINA_EE);
        mFinalMap.put("" + UNI_QA, "" + GLYPH_FINA_QA);
        mFinalMap.put("" + UNI_QA + FVS1, "" + GLYPH_FINA_QA_FVS1);
        mFinalMap.put("" + UNI_QA + FVS2, "" + GLYPH_FINA_QA_FVS2);
        mFinalMap.put("" + UNI_QA + UNI_E, "" + GLYPH_MEDI_QA_FINA_E);
        mFinalMap.put("" + UNI_QA + UNI_I, "" + GLYPH_MEDI_QA_FINA_I);
        mFinalMap.put("" + UNI_QA + UNI_OE, "" + GLYPH_MEDI_QA_FINA_OE);
        mFinalMap.put("" + UNI_QA + UNI_OE + FVS1, "" + GLYPH_MEDI_QA_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_QA + UNI_UE, "" + GLYPH_MEDI_QA_FINA_UE);
        mFinalMap.put("" + UNI_QA + UNI_UE + FVS1, "" + GLYPH_MEDI_QA_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_QA + UNI_EE, "" + GLYPH_MEDI_QA_FINA_EE);
        mFinalMap.put("" + UNI_GA, "" + GLYPH_FINA_GA);
        mFinalMap.put("" + UNI_GA + FVS1, "" + GLYPH_FINA_GA_FVS1);
        mFinalMap.put("" + UNI_GA + FVS2, "" + GLYPH_FINA_GA_FVS2);
        // TODO The FSV3 is just to make it compatible with Baiti
        mFinalMap.put("" + UNI_GA + FVS3, "" + GLYPH_FINA_GA_FVS3);
        mFinalMap.put("" + UNI_GA + UNI_E, "" + GLYPH_MEDI_GA_FINA_E);
        mFinalMap.put("" + UNI_GA + UNI_I, "" + GLYPH_MEDI_GA_FINA_I);
        mFinalMap.put("" + UNI_GA + UNI_OE, "" + GLYPH_MEDI_GA_FINA_OE);
        mFinalMap.put("" + UNI_GA + UNI_OE + FVS1, "" + GLYPH_MEDI_GA_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_GA + UNI_UE, "" + GLYPH_MEDI_GA_FINA_UE);
        mFinalMap.put("" + UNI_GA + UNI_UE + FVS1, "" + GLYPH_MEDI_GA_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_GA + UNI_EE, "" + GLYPH_MEDI_GA_FINA_EE);
        mFinalMap.put("" + UNI_MA, "" + GLYPH_FINA_MA);
        mFinalMap.put("" + UNI_LA, "" + GLYPH_FINA_LA);
        mFinalMap.put("" + UNI_SA, "" + GLYPH_FINA_SA);
        mFinalMap.put("" + UNI_SHA, "" + GLYPH_FINA_SHA);
        mFinalMap.put("" + UNI_TA, "" + GLYPH_FINA_TA);
        mFinalMap.put("" + UNI_DA, "" + GLYPH_FINA_DA);
        mFinalMap.put("" + UNI_DA + FVS1, "" + GLYPH_FINA_DA_FVS1);
        mFinalMap.put("" + UNI_CHA, "" + GLYPH_FINA_CHA);
        mFinalMap.put("" + UNI_JA, "" + GLYPH_FINA_JA);
        mFinalMap.put("" + UNI_JA + FVS1, "" + GLYPH_FINA_JA_FVS1);
        mFinalMap.put("" + UNI_YA, "" + GLYPH_FINA_YA);
        mFinalMap.put("" + UNI_RA, "" + GLYPH_FINA_RA);
        mFinalMap.put("" + UNI_WA, "" + GLYPH_FINA_WA);
        mFinalMap.put("" + UNI_WA + FVS1, "" + GLYPH_FINA_WA_FVS1);
        mFinalMap.put("" + UNI_FA, "" + GLYPH_FINA_FA);
        mFinalMap.put("" + UNI_FA + UNI_A, "" + GLYPH_MEDI_FA_FINA_A);
        mFinalMap.put("" + UNI_FA + UNI_E, "" + GLYPH_MEDI_FA_FINA_E);
        mFinalMap.put("" + UNI_FA + UNI_I, "" + GLYPH_MEDI_FA_FINA_I);
        mFinalMap.put("" + UNI_FA + UNI_O, "" + GLYPH_MEDI_FA_FINA_O);
        mFinalMap.put("" + UNI_FA + UNI_U, "" + GLYPH_MEDI_FA_FINA_U);
        mFinalMap.put("" + UNI_FA + UNI_OE, "" + GLYPH_MEDI_FA_FINA_OE);
        mFinalMap.put("" + UNI_FA + UNI_OE + FVS1, "" + GLYPH_MEDI_FA_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_FA + UNI_UE, "" + GLYPH_MEDI_FA_FINA_UE);
        mFinalMap.put("" + UNI_FA + UNI_UE + FVS1, "" + GLYPH_MEDI_FA_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_FA + UNI_EE, "" + GLYPH_MEDI_FA_FINA_EE);
        mFinalMap.put("" + UNI_KA, "" + GLYPH_FINA_KA);
        mFinalMap.put("" + UNI_KA + UNI_A, "" + GLYPH_MEDI_KA_FINA_A);
        mFinalMap.put("" + UNI_KA + UNI_E, "" + GLYPH_MEDI_KA_FINA_E);
        mFinalMap.put("" + UNI_KA + UNI_I, "" + GLYPH_MEDI_KA_FINA_I);
        mFinalMap.put("" + UNI_KA + UNI_O, "" + GLYPH_MEDI_KA_FINA_O);
        mFinalMap.put("" + UNI_KA + UNI_U, "" + GLYPH_MEDI_KA_FINA_U);
        mFinalMap.put("" + UNI_KA + UNI_OE, "" + GLYPH_MEDI_KA_FINA_OE);
        mFinalMap.put("" + UNI_KA + UNI_OE + FVS1, "" + GLYPH_MEDI_KA_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_KA + UNI_UE, "" + GLYPH_MEDI_KA_FINA_UE);
        mFinalMap.put("" + UNI_KA + UNI_UE + FVS1, "" + GLYPH_MEDI_KA_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_KA + UNI_EE, "" + GLYPH_MEDI_KA_FINA_EE);
        mFinalMap.put("" + UNI_KHA, "" + GLYPH_FINA_KHA);
        mFinalMap.put("" + UNI_KHA + UNI_A, "" + GLYPH_MEDI_KHA_FINA_A);
        mFinalMap.put("" + UNI_KHA + UNI_E, "" + GLYPH_MEDI_KHA_FINA_E);
        mFinalMap.put("" + UNI_KHA + UNI_I, "" + GLYPH_MEDI_KHA_FINA_I);
        mFinalMap.put("" + UNI_KHA + UNI_O, "" + GLYPH_MEDI_KHA_FINA_O);
        mFinalMap.put("" + UNI_KHA + UNI_U, "" + GLYPH_MEDI_KHA_FINA_U);
        mFinalMap.put("" + UNI_KHA + UNI_OE, "" + GLYPH_MEDI_KHA_FINA_OE);
        mFinalMap.put("" + UNI_KHA + UNI_OE + FVS1, "" + GLYPH_MEDI_KHA_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_KHA + UNI_UE, "" + GLYPH_MEDI_KHA_FINA_UE);
        mFinalMap.put("" + UNI_KHA + UNI_UE + FVS1, "" + GLYPH_MEDI_KHA_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_KHA + UNI_EE, "" + GLYPH_MEDI_KHA_FINA_EE);
        mFinalMap.put("" + UNI_TSA, "" + GLYPH_FINA_TSA);
        mFinalMap.put("" + UNI_ZA, "" + GLYPH_FINA_ZA);
        mFinalMap.put("" + UNI_HAA, "" + GLYPH_FINA_HAA);
        mFinalMap.put("" + UNI_ZRA, "" + GLYPH_FINA_ZRA);
        mFinalMap.put("" + UNI_LHA, "" + GLYPH_FINA_LHA);
        mFinalMap.put("" + UNI_ZHI, "" + GLYPH_FINA_ZHI);
        mFinalMap.put("" + UNI_CHI, "" + GLYPH_FINA_CHI);
        mFinalMap.put("" + UNI_SA + FVS1, "" + GLYPH_FINA_SA_FVS1);
        mFinalMap.put("" + UNI_SA + FVS2, "" + GLYPH_FINA_SA_FVS2);
        mFinalMap.put("" + UNI_BA + FVS1, "" + GLYPH_FINA_BA_FVS1);
        mFinalMap.put("" + UNI_ANG + UNI_QA, "" + GLYPH_MEDI_ANG_FINA_QA);
        mFinalMap.put("" + UNI_ANG + UNI_GA, "" + GLYPH_MEDI_ANG_FINA_GA);
        mFinalMap.put("" + UNI_QA + FVS1 + UNI_E, "" + GLYPH_MEDI_QA_FVS1_FINA_E);
        mFinalMap.put("" + UNI_QA + FVS1 + UNI_I, "" + GLYPH_MEDI_QA_FVS1_FINA_I);
        mFinalMap.put("" + UNI_QA + FVS1 + UNI_OE, "" + GLYPH_MEDI_QA_FVS1_FINA_OE);
        mFinalMap.put("" + UNI_QA + FVS1 + UNI_OE + FVS1, "" + GLYPH_MEDI_QA_FVS1_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_QA + FVS1 + UNI_UE, "" + GLYPH_MEDI_QA_FVS1_FINA_UE);
        mFinalMap.put("" + UNI_QA + FVS1 + UNI_UE + FVS1, "" + GLYPH_MEDI_QA_FVS1_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_QA + FVS1 + UNI_EE, "" + GLYPH_MEDI_QA_FVS1_FINA_EE);
        mFinalMap.put("" + UNI_NA + FVS2, "" + GLYPH_FINA_NA_FVS2);
        mFinalMap.put("" + UNI_GA + FVS1 + UNI_E, "" + GLYPH_MEDI_GA_FVS1_FINA_E);
        mFinalMap.put("" + UNI_GA + FVS1 + UNI_I, "" + GLYPH_MEDI_GA_FVS1_FINA_I);
        mFinalMap.put("" + UNI_GA + FVS1 + UNI_OE, "" + GLYPH_MEDI_GA_FVS1_FINA_OE);
        mFinalMap.put("" + UNI_GA + FVS1 + UNI_OE + FVS1, "" + GLYPH_MEDI_GA_FVS1_FINA_OE_FVS1);
        mFinalMap.put("" + UNI_GA + FVS1 + UNI_UE, "" + GLYPH_MEDI_GA_FVS1_FINA_UE);
        mFinalMap.put("" + UNI_GA + FVS1 + UNI_UE + FVS1, "" + GLYPH_MEDI_GA_FVS1_FINA_UE_FVS1);
        mFinalMap.put("" + UNI_GA + FVS1 + UNI_EE, "" + GLYPH_MEDI_GA_FVS1_FINA_EE);

        // Final Vowel+YI rule (drop the Y)
        // (preFormatter catches final Consonant+YI)
        mFinalMap.put("" + UNI_YA + UNI_I, "" + GLYPH_FINA_I);

        // MVS
        // TODO handle MVS in preFormatter()?
        mFinalMap.put("" + UNI_NA + MVS, "" + GLYPH_FINA_NA_FVS1);
        mFinalMap.put("" + UNI_ANG + UNI_QA + MVS, "" + GLYPH_MEDI_ANG_FINA_QA);
        mFinalMap.put("" + UNI_ANG + FVS1 + UNI_QA + MVS, "" + GLYPH_MEDI_ANG_FINA_QA);
        mFinalMap.put("" + UNI_ANG + FVS1 + UNI_GA + MVS, "" + GLYPH_MEDI_ANG_FINA_GA);
        mFinalMap.put("" + UNI_ANG + UNI_GA + MVS, "" + GLYPH_MEDI_ANG_FINA_GA);
        mFinalMap.put("" + UNI_QA + MVS, "" + GLYPH_FINA_QA);
        mFinalMap.put("" + UNI_GA + MVS, "" + GLYPH_FINA_GA_FVS3);
        mFinalMap.put("" + UNI_MA + MVS, "" + GLYPH_FINA_MA);
        mFinalMap.put("" + UNI_LA + MVS, "" + GLYPH_FINA_LA);
        mFinalMap.put("" + UNI_JA + MVS, "" + GLYPH_FINA_JA_FVS1);
        mFinalMap.put("" + UNI_YA + MVS, "" + GLYPH_FINA_YA);
        mFinalMap.put("" + UNI_I + MVS, "" + GLYPH_FINA_YA); // I may be a substitute for YA
        mFinalMap.put("" + UNI_RA + MVS, "" + GLYPH_FINA_RA);
        mFinalMap.put("" + UNI_WA + MVS, "" + GLYPH_FINA_WA);

        // Catch other chars
        mFinalMap.put("" + CURSOR_HOLDER, "" + CURSOR_HOLDER);
        mFinalMap.put("" + MONGOLIAN_NIRUGU, "" + GLYPH_NIRUGU);
        mFinalMap.put("" + ZWJ, "");
        mFinalMap.put("" + NNBS, "" + NNBS);
        mFinalMap.put("" + MVS, "");
        mFinalMap.put("" + FVS1, "");
        mFinalMap.put("" + FVS2, "");
        mFinalMap.put("" + FVS3, "");

    }

    private void initSuffixes() {

        mSuffixMap = new HashMap<String, String>();

        // Vocative Case
        mSuffixMap.put("" + UNI_A, "" + GLYPH_FINA_A_FVS1);
        mSuffixMap.put("" + UNI_E, "" + GLYPH_FINA_E_FVS1);

        // Genetive Case
        // YIN
        mSuffixMap.put("" + UNI_YA + UNI_I + UNI_NA, "" + GLYPH_INIT_YA_FVS1 + GLYPH_MEDI_I
                + GLYPH_FINA_NA);
        // UN
        mSuffixMap.put("" + UNI_U + UNI_NA, "" + GLYPH_INIT_U_FVS1 + GLYPH_FINA_NA);
        // UEN
        mSuffixMap.put("" + UNI_UE + UNI_NA, "" + GLYPH_INIT_UE_FVS1 + GLYPH_FINA_NA);
        // U
        mSuffixMap.put("" + UNI_U, "" + GLYPH_ISOL_U_FVS2);
        // UE
        mSuffixMap.put("" + UNI_UE, "" + GLYPH_ISOL_UE_FVS3);

        // Accusative Case
        // I
        mSuffixMap.put("" + UNI_I, "" + GLYPH_ISOL_I_FVS1);
        // YI
        mSuffixMap.put("" + UNI_YA + UNI_I, "" + GLYPH_INIT_YA_FVS1 + GLYPH_FINA_I);

        // Dative-Locative Case
        // DU
        mSuffixMap.put("" + UNI_DA + UNI_U, "" + GLYPH_INIT_DA_FVS1 + GLYPH_FINA_U);
        // DUE
        mSuffixMap.put("" + UNI_DA + UNI_UE, "" + GLYPH_INIT_DA_FVS1 + GLYPH_FINA_UE);
        // TU
        mSuffixMap.put("" + UNI_TA + UNI_U, "" + GLYPH_INIT_TA + GLYPH_FINA_U);
        // TUE
        mSuffixMap.put("" + UNI_TA + UNI_UE, "" + GLYPH_INIT_TA + GLYPH_FINA_UE);
        // DUR
        mSuffixMap.put("" + UNI_DA + UNI_U + UNI_RA, "" + GLYPH_INIT_DA_FVS1 + GLYPH_MEDI_U
                + GLYPH_FINA_RA);
        // DUER
        mSuffixMap.put("" + UNI_DA + UNI_UE + UNI_RA, "" + GLYPH_INIT_DA_FVS1 + GLYPH_MEDI_UE
                + GLYPH_FINA_RA);
        // TUR
        mSuffixMap.put("" + UNI_TA + UNI_U + UNI_RA, "" + GLYPH_INIT_TA + GLYPH_MEDI_U
                + GLYPH_FINA_RA);
        // TUER
        mSuffixMap.put("" + UNI_TA + UNI_UE + UNI_RA, "" + GLYPH_INIT_TA + GLYPH_MEDI_UE
                + GLYPH_FINA_RA);
        // DAQI
        mSuffixMap.put("" + UNI_DA + UNI_A + UNI_QA + UNI_I, "" + GLYPH_INIT_DA_FVS1 + GLYPH_MEDI_A
                + GLYPH_MEDI_QA_FINA_I);
        // DEQI
        mSuffixMap.put("" + UNI_DA + UNI_E + UNI_QA + UNI_I, "" + GLYPH_INIT_DA_FVS1 + GLYPH_MEDI_E
                + GLYPH_MEDI_QA_FINA_I);

        // Ablative Case
        // ACHA
        mSuffixMap.put("" + UNI_A + UNI_CHA + UNI_A, "" + GLYPH_INIT_A_FVS1 + GLYPH_MEDI_CHA
                + GLYPH_FINA_A);
        // ECHE
        mSuffixMap.put("" + UNI_E + UNI_CHA + UNI_E, "" + GLYPH_INIT_E + GLYPH_MEDI_CHA
                + GLYPH_FINA_E);

        // Instrumental Case
        // BAR
        mSuffixMap.put("" + UNI_BA + UNI_A + UNI_RA, "" + GLYPH_INIT_BA_MEDI_A + GLYPH_FINA_RA);
        // BER
        mSuffixMap.put("" + UNI_BA + UNI_E + UNI_RA, "" + GLYPH_INIT_BA_MEDI_E + GLYPH_FINA_RA);
        // IYAR
        mSuffixMap.put("" + UNI_I + UNI_YA + UNI_A + UNI_RA, "" + GLYPH_INIT_I_FVS1 + GLYPH_MEDI_I
                + GLYPH_MEDI_A + GLYPH_FINA_RA);
        // IYER
        mSuffixMap.put("" + UNI_I + UNI_YA + UNI_E + UNI_RA, "" + GLYPH_INIT_I_FVS1 + GLYPH_MEDI_I
                + GLYPH_MEDI_E + GLYPH_FINA_RA);

        // Comitative Case
        // TAI
        mSuffixMap.put("" + UNI_TA + UNI_A + UNI_I, "" + GLYPH_INIT_TA + GLYPH_MEDI_A
                + GLYPH_FINA_I);
        mSuffixMap.put("" + UNI_TA + UNI_A + UNI_YA + UNI_I, "" + GLYPH_INIT_TA + GLYPH_MEDI_A
                + GLYPH_FINA_I);
        // TEI
        mSuffixMap.put("" + UNI_TA + UNI_E + UNI_I, "" + GLYPH_INIT_TA + GLYPH_MEDI_E
                + GLYPH_FINA_I);
        mSuffixMap.put("" + UNI_TA + UNI_E + UNI_YA + UNI_I, "" + GLYPH_INIT_TA + GLYPH_MEDI_E
                + GLYPH_FINA_I);
        // LUG-A
        mSuffixMap.put("" + UNI_LA + UNI_U + UNI_GA + MVS + UNI_A, "" + GLYPH_INIT_LA
                + GLYPH_MEDI_U + GLYPH_FINA_GA_FVS3 + GLYPH_FINA_A_FVS2);
        // LUEGE
        mSuffixMap.put("" + UNI_LA + UNI_UE + UNI_GA + UNI_E, "" + GLYPH_INIT_LA + GLYPH_MEDI_UE
                + GLYPH_MEDI_GA_FINA_E);

        // Reflexive Case
        // BAN
        mSuffixMap.put("" + UNI_BA + UNI_A + UNI_NA, "" + GLYPH_INIT_BA_MEDI_A + GLYPH_FINA_NA);
        // BEN
        mSuffixMap.put("" + UNI_BA + UNI_E + UNI_NA, "" + GLYPH_INIT_BA_MEDI_E + GLYPH_FINA_NA);
        // IYAN
        mSuffixMap.put("" + UNI_I + UNI_YA + UNI_A + UNI_NA, "" + GLYPH_INIT_I_FVS1 + GLYPH_MEDI_I
                + GLYPH_MEDI_A + GLYPH_FINA_NA);
        // IYEN
        mSuffixMap.put("" + UNI_I + UNI_YA + UNI_E + UNI_NA, "" + GLYPH_INIT_I_FVS1 + GLYPH_MEDI_I
                + GLYPH_MEDI_E + GLYPH_FINA_NA);

        // Reflexive+Accusative
        // YUGAN
        mSuffixMap.put("" + UNI_YA + UNI_U + UNI_GA + UNI_A + UNI_NA, "" + GLYPH_INIT_YA
                + GLYPH_MEDI_U + GLYPH_MEDI_GA_FVS1 + GLYPH_MEDI_A + GLYPH_FINA_NA);
        // YUEGEN
        mSuffixMap.put("" + UNI_YA + UNI_UE + UNI_GA + UNI_E + UNI_NA, "" + GLYPH_INIT_YA
                + GLYPH_MEDI_UE + GLYPH_MEDI_GA_MEDI_E + GLYPH_FINA_NA);

        // Reflexive+Dative-Locative
        // DAGAN
        mSuffixMap.put("" + UNI_DA + UNI_A + UNI_GA + UNI_A + UNI_NA, "" + GLYPH_INIT_DA_FVS1
                + GLYPH_MEDI_A + GLYPH_MEDI_GA_FVS1 + GLYPH_MEDI_A + GLYPH_FINA_NA);
        // DEGEN
        mSuffixMap.put("" + UNI_DA + UNI_E + UNI_GA + UNI_E + UNI_NA, "" + GLYPH_INIT_DA_FVS1
                + GLYPH_MEDI_E + GLYPH_MEDI_GA_MEDI_E + GLYPH_FINA_NA);
        // TAGAN
        mSuffixMap.put("" + UNI_TA + UNI_A + UNI_GA + UNI_A + UNI_NA, "" + GLYPH_INIT_TA
                + GLYPH_MEDI_A + GLYPH_MEDI_GA_FVS1 + GLYPH_MEDI_A + GLYPH_FINA_NA);
        // TEGEN
        mSuffixMap.put("" + UNI_TA + UNI_E + UNI_GA + UNI_E + UNI_NA, "" + GLYPH_INIT_TA
                + GLYPH_MEDI_E + GLYPH_MEDI_GA_MEDI_E + GLYPH_FINA_NA);

        // Reflexive+Ablative
        // ACHAGAN
        mSuffixMap.put("" + UNI_A + UNI_CHA + UNI_A + UNI_GA + UNI_A + UNI_NA, ""
                + GLYPH_INIT_A_FVS1 + GLYPH_MEDI_CHA + GLYPH_MEDI_A + GLYPH_MEDI_GA_FVS1
                + GLYPH_MEDI_A + GLYPH_FINA_NA);
        // ECHEGEN
        mSuffixMap.put("" + UNI_E + UNI_CHA + UNI_E + UNI_GA + UNI_E + UNI_NA, "" + GLYPH_INIT_E
                + GLYPH_MEDI_CHA + GLYPH_MEDI_E + GLYPH_MEDI_GA_MEDI_E + GLYPH_FINA_NA);

        // Reflexive+Comitative
        // TAIGAN
        mSuffixMap.put("" + UNI_TA + UNI_A + UNI_I + UNI_GA + UNI_A + UNI_NA, "" + GLYPH_INIT_TA
                + GLYPH_MEDI_A + GLYPH_MEDI_I_FVS3 + GLYPH_MEDI_GA_FVS1 + GLYPH_MEDI_A
                + GLYPH_FINA_NA);
        mSuffixMap.put("" + UNI_TA + UNI_A + UNI_YA + UNI_I + UNI_GA + UNI_A + UNI_NA, ""
                + GLYPH_INIT_TA + GLYPH_MEDI_A + GLYPH_MEDI_I_FVS3 + GLYPH_MEDI_GA_FVS1
                + GLYPH_MEDI_A + GLYPH_FINA_NA);
        // TEIGEN
        mSuffixMap.put("" + UNI_TA + UNI_E + UNI_I + UNI_GA + UNI_E + UNI_NA, "" + GLYPH_INIT_TA
                + GLYPH_MEDI_E + GLYPH_MEDI_I_FVS3 + GLYPH_MEDI_GA_MEDI_E + GLYPH_FINA_NA);
        mSuffixMap.put("" + UNI_TA + UNI_E + UNI_YA + UNI_I + UNI_GA + UNI_E + UNI_NA, ""
                + GLYPH_INIT_TA + GLYPH_MEDI_E + GLYPH_MEDI_I_FVS3 + GLYPH_MEDI_GA_MEDI_E
                + GLYPH_FINA_NA);

        // Plural
        // UD
        mSuffixMap.put("" + UNI_U + UNI_DA, "" + GLYPH_INIT_U_FVS1 + GLYPH_FINA_DA);
        // UED
        mSuffixMap.put("" + UNI_UE + UNI_DA, "" + GLYPH_INIT_UE_FVS1 + GLYPH_FINA_DA);
        // NUGUD
        mSuffixMap.put("" + UNI_NA + UNI_U + UNI_GA + UNI_U + UNI_DA, "" + GLYPH_INIT_NA
                + GLYPH_MEDI_U + GLYPH_MEDI_GA_FVS1 + GLYPH_MEDI_U + GLYPH_FINA_DA);
        // NUEGUED
        mSuffixMap.put("" + UNI_NA + UNI_UE + UNI_GA + UNI_UE + UNI_DA, "" + GLYPH_INIT_NA
                + GLYPH_MEDI_UE + GLYPH_MEDI_GA_MEDI_UE + GLYPH_FINA_DA);
        // NAR
        mSuffixMap.put("" + UNI_NA + UNI_A + UNI_RA, "" + GLYPH_INIT_NA + GLYPH_MEDI_A
                + GLYPH_FINA_RA);
        // NER
        mSuffixMap.put("" + UNI_NA + UNI_E + UNI_RA, "" + GLYPH_INIT_NA + GLYPH_MEDI_E
                + GLYPH_FINA_RA);

        // Question partical
        // UU
        mSuffixMap.put("" + UNI_U + UNI_U, "" + GLYPH_WORD_UU);
        // UEUE
        mSuffixMap.put("" + UNI_UE + UNI_UE, "" + GLYPH_WORD_UU);

    }

    // Class constants
    public static final char ZWJ = '\u200d'; // Zero-width joiner
    public static final char NNBS = '\u202F'; // Narrow No-Break Space
    // Unicode Mongolian Values
    public static final char MONGOLIAN_BIRGA = '\u1800';
    public static final char MONGOLIAN_ELLIPSIS = '\u1801';
    public static final char MONGOLIAN_COMMA = '\u1802';
    public static final char MONGOLIAN_FULL_STOP = '\u1803';
    public static final char MONGOLIAN_COLON = '\u1804';
    public static final char MONGOLIAN_FOUR_DOTS = '\u1805';
    public static final char MONGOLIAN_NIRUGU = '\u180a';
    public static final char FVS1 = '\u180b';
    public static final char FVS2 = '\u180c';
    public static final char FVS3 = '\u180d';
    public static final char MVS = '\u180e'; // MONGOLIAN_VOWEL_SEPARATOR
    public static final char MONGOLIAN_DIGIT_ZERO = '\u1810';
    public static final char MONGOLIAN_DIGIT_ONE = '\u1811';
    public static final char MONGOLIAN_DIGIT_TWO = '\u1812';
    public static final char MONGOLIAN_DIGIT_THREE = '\u1813';
    public static final char MONGOLIAN_DIGIT_FOUR = '\u1814';
    public static final char MONGOLIAN_DIGIT_FIVE = '\u1815';
    public static final char MONGOLIAN_DIGIT_SIX = '\u1816';
    public static final char MONGOLIAN_DIGIT_SEVEN = '\u1817';
    public static final char MONGOLIAN_DIGIT_EIGHT = '\u1818';
    public static final char MONGOLIAN_DIGIT_NINE = '\u1819';
    public static final char UNI_A = '\u1820'; // MONGOLIAN_LETTER_xx
    public static final char UNI_E = '\u1821';
    public static final char UNI_I = '\u1822';
    public static final char UNI_O = '\u1823';
    public static final char UNI_U = '\u1824';
    public static final char UNI_OE = '\u1825';
    public static final char UNI_UE = '\u1826';
    public static final char UNI_EE = '\u1827';
    public static final char UNI_NA = '\u1828';
    public static final char UNI_ANG = '\u1829';
    public static final char UNI_BA = '\u182A';
    public static final char UNI_PA = '\u182B';
    public static final char UNI_QA = '\u182C';
    public static final char UNI_GA = '\u182D';
    public static final char UNI_MA = '\u182E';
    public static final char UNI_LA = '\u182F';
    public static final char UNI_SA = '\u1830';
    public static final char UNI_SHA = '\u1831';
    public static final char UNI_TA = '\u1832';
    public static final char UNI_DA = '\u1833';
    public static final char UNI_CHA = '\u1834';
    public static final char UNI_JA = '\u1835';
    public static final char UNI_YA = '\u1836';
    public static final char UNI_RA = '\u1837';
    public static final char UNI_WA = '\u1838';
    public static final char UNI_FA = '\u1839';
    public static final char UNI_KA = '\u183A';
    public static final char UNI_KHA = '\u183B';
    public static final char UNI_TSA = '\u183C';
    public static final char UNI_ZA = '\u183D';
    public static final char UNI_HAA = '\u183E';
    public static final char UNI_ZRA = '\u183F';
    public static final char UNI_LHA = '\u1840';
    public static final char UNI_ZHI = '\u1841';
    public static final char UNI_CHI = '\u1842';

    // Private Use Area glyph values
    // public static final char CURSOR_HOLDER = '\uE359'; // arbitrary unused char
    public static final char CURSOR_HOLDER = '|';

    private static final char GLYPH_NOTDEF = '\uE360';
    private static final char GLYPH_BIRGA = '\uE364';
    private static final char GLYPH_ELLIPSIS = '\uE365';
    private static final char GLYPH_COMMA = '\uE366';
    private static final char GLYPH_FULL_STOP = '\uE367';
    private static final char GLYPH_COLON = '\uE368';
    private static final char GLYPH_FOUR_DOTS = '\uE369';
    private static final char GLYPH_NIRUGU = '\uE36E';
    private static final char GLYPH_ZERO = '\uE374';
    private static final char GLYPH_ONE = '\uE375';
    private static final char GLYPH_TWO = '\uE376';
    private static final char GLYPH_THREE = '\uE377';
    private static final char GLYPH_FOUR = '\uE378';
    private static final char GLYPH_FIVE = '\uE379';
    private static final char GLYPH_SIX = '\uE37A';
    private static final char GLYPH_SEVEN = '\uE37B';
    private static final char GLYPH_EIGHT = '\uE37C';
    private static final char GLYPH_NINE = '\uE37D';
    private static final char GLYPH_QUESTION_EXCLAMATION = '\uE37E';
    private static final char GLYPH_EXCLAMATION_QUESTION = '\uE37F';
    private static final char GLYPH_ISOL_A = '\uE384';
    private static final char GLYPH_ISOL_A_FVS1 = '\uE385';
    private static final char GLYPH_INIT_A = '\uE386';
    private static final char GLYPH_MEDI_A = '\uE387';
    private static final char GLYPH_MEDI_A_FVS1 = '\uE388';
    private static final char GLYPH_FINA_A = '\uE389';
    private static final char GLYPH_FINA_A_FVS1 = '\uE38A';
    private static final char GLYPH_FINA_A_FVS2 = '\uE38B';
    private static final char GLYPH_ISOL_E = '\uE38C';
    private static final char GLYPH_ISOL_E_FVS1 = '\uE38D';
    private static final char GLYPH_INIT_E = '\uE38E';
    private static final char GLYPH_INIT_E_FVS1 = '\uE38F';
    private static final char GLYPH_MEDI_E = '\uE390';
    private static final char GLYPH_FINA_E = '\uE391';
    private static final char GLYPH_FINA_E_FVS1 = '\uE392';
    private static final char GLYPH_FINA_E_FVS2 = '\uE393';
    private static final char GLYPH_ISOL_I = '\uE394';
    private static final char GLYPH_ISOL_I_FVS1 = '\uE395';
    private static final char GLYPH_INIT_I = '\uE396';
    private static final char GLYPH_INIT_I_FVS1 = '\uE397';
    private static final char GLYPH_MEDI_I = '\uE398';
    private static final char GLYPH_MEDI_I_FVS1 = '\uE399';
    // TODO GLYPH_MEDI_I_FVS2 and GLYPH_MEDI_I_FVS3 have not been standardized in Unicode yet
    // Matching them to Baiti
    private static final char GLYPH_MEDI_I_FVS3 = '\uE39A';
    private static final char GLYPH_FINA_I = '\uE39B';
    private static final char GLYPH_ISOL_O = '\uE39C';
    private static final char GLYPH_ISOL_O_FVS1 = '\uE39D';
    private static final char GLYPH_INIT_O = '\uE39E';
    private static final char GLYPH_INIT_O_FVS1 = '\uE39F';
    private static final char GLYPH_MEDI_O = '\uE3A0';
    private static final char GLYPH_MEDI_O_FVS1 = '\uE3A1';
    private static final char GLYPH_FINA_O = '\uE3A2';
    private static final char GLYPH_FINA_O_FVS1 = '\uE3A3';
    private static final char GLYPH_ISOL_U = '\uE3A6';  // Using Init U gliph
    private static final char GLYPH_ISOL_U_FVS1 = '\uE3A4';
    private static final char GLYPH_ISOL_U_FVS2 = '\uE3A5';
    private static final char GLYPH_INIT_U = '\uE3A6';
    private static final char GLYPH_INIT_U_FVS1 = '\uE3A7';
    private static final char GLYPH_MEDI_U = '\uE3A8';
    private static final char GLYPH_MEDI_U_FVS1 = '\uE3A9';
    private static final char GLYPH_FINA_U = '\uE3AA';
    private static final char GLYPH_FINA_U_FVS1 = '\uE3AB';
    private static final char GLYPH_ISOL_OE = '\uE3AC';
    private static final char GLYPH_ISOL_OE_FVS1 = '\uE3AD';
    private static final char GLYPH_INIT_OE = '\uE3AE';
    private static final char GLYPH_MEDI_OE = '\uE3AF';
    private static final char GLYPH_MEDI_OE_FVS1 = '\uE3B0';
    private static final char GLYPH_MEDI_OE_FVS2 = '\uE3B1';
    private static final char GLYPH_FINA_OE = '\uE3B2';
    private static final char GLYPH_FINA_OE_FVS1 = '\uE3B3';
    private static final char GLYPH_ISOL_UE = '\uE3B6';
    private static final char GLYPH_ISOL_UE_FVS2 = '\uE3C3';
    private static final char GLYPH_ISOL_UE_FVS3 = '\uE3B5';
    private static final char GLYPH_INIT_UE = '\uE3B6';
    private static final char GLYPH_MEDI_UE = '\uE3B7';
    private static final char GLYPH_MEDI_UE_FVS1 = '\uE3B8';
    private static final char GLYPH_MEDI_UE_FVS2 = '\uE3B9';
    private static final char GLYPH_FINA_UE = '\uE3BA';
    private static final char GLYPH_FINA_UE_FVS1 = '\uE3BB';
    private static final char GLYPH_ISOL_EE = '\uE3BC';
    private static final char GLYPH_ISOL_EE_FVS1 = '\uE3BD';
    private static final char GLYPH_INIT_EE = '\uE3BE';
    private static final char GLYPH_INIT_EE_FVS1 = '\uE3BF';
    private static final char GLYPH_MEDI_EE = '\uE3C0';
    private static final char GLYPH_FINA_EE = '\uE3C1';
    private static final char GLYPH_INIT_UE_FVS1 = '\uE3C2';
    private static final char GLYPH_ISOL_UE_FVS1 = '\uE3B4';
    private static final char GLYPH_ISOL_NA = '\uE3C4';
    private static final char GLYPH_ISOL_NA_FVS1 = '\uE3C5';
    private static final char GLYPH_INIT_NA = '\uE3C6';
    private static final char GLYPH_INIT_NA_FVS1 = '\uE3C7';
    private static final char GLYPH_MEDI_NA = '\uE3C8';
    private static final char GLYPH_MEDI_NA_FVS1 = '\uE3C9';
    private static final char GLYPH_MEDI_NA_FVS2 = '\uE3C8'; // same as medial na
    private static final char GLYPH_FINA_NA = '\uE3CA';
    private static final char GLYPH_FINA_NA_FVS1 = '\uE3CB';
    private static final char GLYPH_ISOL_ANG = '\uE3CC';
    private static final char GLYPH_INIT_ANG = '\uE3CD';
    private static final char GLYPH_MEDI_ANG = '\uE3CE';
    private static final char GLYPH_FINA_ANG = '\uE3CF';
    private static final char GLYPH_MEDI_ANG_MEDI_QA = '\uE3D0';
    private static final char GLYPH_MEDI_ANG_MEDI_GA = '\uE3D1';
    private static final char GLYPH_MEDI_ANG_MEDI_MA = '\uE3D2';
    private static final char GLYPH_MEDI_ANG_MEDI_LA = '\uE3D3';
    private static final char GLYPH_ISOL_BA = '\uE3D4';
    private static final char GLYPH_INIT_BA = '\uE3D5';
    private static final char GLYPH_MEDI_BA = '\uE3D6';
    private static final char GLYPH_FINA_BA = '\uE3D7';
    private static final char GLYPH_INIT_BA_FINA_A = '\uE3D8';
    private static final char GLYPH_INIT_BA_MEDI_A = '\uE3D9';
    private static final char GLYPH_MEDI_BA_MEDI_A = '\uE3DA';
    private static final char GLYPH_MEDI_BA_FINA_A = '\uE3DB';
    private static final char GLYPH_INIT_BA_FINA_E = '\uE3DC';
    private static final char GLYPH_INIT_BA_MEDI_E = '\uE3DD';
    private static final char GLYPH_MEDI_BA_MEDI_E = '\uE3DE';
    private static final char GLYPH_MEDI_BA_FINA_E = '\uE3DF';
    private static final char GLYPH_INIT_BA_FINA_I = '\uE3E0';
    private static final char GLYPH_INIT_BA_MEDI_I = '\uE3E1';
    private static final char GLYPH_MEDI_BA_MEDI_I = '\uE3E2';
    private static final char GLYPH_MEDI_BA_FINA_I = '\uE3E3';
    private static final char GLYPH_INIT_BA_FINA_O = '\uE3E4';
    private static final char GLYPH_INIT_BA_MEDI_O = '\uE3E5';
    private static final char GLYPH_MEDI_BA_MEDI_O = '\uE3E6';
    private static final char GLYPH_MEDI_BA_FINA_O = '\uE3E7';
    private static final char GLYPH_INIT_BA_FINA_U = '\uE3E8';
    private static final char GLYPH_INIT_BA_MEDI_U = '\uE3E9';
    private static final char GLYPH_MEDI_BA_MEDI_U = '\uE3EA';
    private static final char GLYPH_MEDI_BA_FINA_U = '\uE3EB';
    private static final char GLYPH_INIT_BA_FINA_OE = '\uE3EC';
    private static final char GLYPH_INIT_BA_MEDI_OE = '\uE3ED';
    private static final char GLYPH_MEDI_BA_MEDI_OE = '\uE3EE';
    private static final char GLYPH_MEDI_BA_FINA_OE = '\uE3EF';
    private static final char GLYPH_MEDI_BA_FINA_OE_FVS1 = '\uE3F0';
    private static final char GLYPH_INIT_BA_FINA_UE = '\uE3F1';
    private static final char GLYPH_INIT_BA_MEDI_UE = '\uE3F2';
    private static final char GLYPH_MEDI_BA_MEDI_UE = '\uE3F3';
    private static final char GLYPH_MEDI_BA_FINA_UE = '\uE3F4';
    private static final char GLYPH_MEDI_BA_FINA_UE_FVS1 = '\uE3F5';
    private static final char GLYPH_INIT_BA_FINA_EE = '\uE3F6';
    private static final char GLYPH_INIT_BA_MEDI_EE = '\uE3F7';
    private static final char GLYPH_MEDI_BA_MEDI_EE = '\uE3F8';
    private static final char GLYPH_MEDI_BA_FINA_EE = '\uE3F9';
    private static final char GLYPH_MEDI_BA_MEDI_MA = '\uE3FA';
    private static final char GLYPH_MEDI_BA_MEDI_LA = '\uE3FB';
    private static final char GLYPH_ISOL_PA = '\uE3FC';
    private static final char GLYPH_INIT_PA = '\uE3FD';
    private static final char GLYPH_MEDI_PA = '\uE3FE';
    private static final char GLYPH_FINA_PA = '\uE3FF';
    private static final char GLYPH_INIT_PA_FINA_A = '\uE400';
    private static final char GLYPH_INIT_PA_MEDI_A = '\uE401';
    private static final char GLYPH_MEDI_PA_MEDI_A = '\uE402';
    private static final char GLYPH_MEDI_PA_FINA_A = '\uE403';
    private static final char GLYPH_INIT_PA_FINA_E = '\uE404';
    private static final char GLYPH_INIT_PA_MEDI_E = '\uE405';
    private static final char GLYPH_MEDI_PA_MEDI_E = '\uE406';
    private static final char GLYPH_MEDI_PA_FINA_E = '\uE407';
    private static final char GLYPH_INIT_PA_FINA_I = '\uE408';
    private static final char GLYPH_INIT_PA_MEDI_I = '\uE409';
    private static final char GLYPH_MEDI_PA_MEDI_I = '\uE40A';
    private static final char GLYPH_MEDI_PA_FINA_I = '\uE40B';
    private static final char GLYPH_INIT_PA_FINA_O = '\uE40C';
    private static final char GLYPH_INIT_PA_MEDI_O = '\uE40D';
    private static final char GLYPH_MEDI_PA_MEDI_O = '\uE40E';
    private static final char GLYPH_MEDI_PA_FINA_O = '\uE40F';
    private static final char GLYPH_INIT_PA_FINA_U = '\uE410';
    private static final char GLYPH_INIT_PA_MEDI_U = '\uE411';
    private static final char GLYPH_MEDI_PA_MEDI_U = '\uE412';
    private static final char GLYPH_MEDI_PA_FINA_U = '\uE413';
    private static final char GLYPH_INIT_PA_FINA_OE = '\uE414';
    private static final char GLYPH_INIT_PA_MEDI_OE = '\uE415';
    private static final char GLYPH_MEDI_PA_MEDI_OE = '\uE416';
    private static final char GLYPH_MEDI_PA_FINA_OE = '\uE417';
    private static final char GLYPH_MEDI_PA_FINA_OE_FVS1 = '\uE418';
    private static final char GLYPH_INIT_PA_FINA_UE = '\uE419';
    private static final char GLYPH_INIT_PA_MEDI_UE = '\uE41A';
    private static final char GLYPH_MEDI_PA_MEDI_UE = '\uE41B';
    private static final char GLYPH_MEDI_PA_FINA_UE = '\uE41C';
    private static final char GLYPH_MEDI_PA_FINA_UE_FVS1 = '\uE41D';
    private static final char GLYPH_INIT_PA_FINA_EE = '\uE41E';
    private static final char GLYPH_INIT_PA_MEDI_EE = '\uE41F';
    private static final char GLYPH_MEDI_PA_MEDI_EE = '\uE420';
    private static final char GLYPH_MEDI_PA_FINA_EE = '\uE421';
    private static final char GLYPH_MEDI_PA_MEDI_MA = '\uE422';
    private static final char GLYPH_MEDI_PA_MEDI_LA = '\uE423';
    private static final char GLYPH_ISOL_QA = '\uE424';
    private static final char GLYPH_ISOL_QA_FVS3 = '\uE425'; // TODO matching Baiti
    private static final char GLYPH_INIT_QA = '\uE426';
    private static final char GLYPH_INIT_QA_FVS1 = '\uE427';
    private static final char GLYPH_MEDI_QA = '\uE428';
    private static final char GLYPH_MEDI_QA_FVS1 = '\uE429';
    private static final char GLYPH_MEDI_QA_FVS2 = '\uE42A';
    private static final char GLYPH_FINA_QA = '\uE42B';
    private static final char GLYPH_FINA_QA_FVS1 = '\uE42C';
    private static final char GLYPH_FINA_QA_FVS2 = '\uE42D';
    private static final char GLYPH_INIT_QA_FINA_E = '\uE42E';
    private static final char GLYPH_INIT_QA_MEDI_E = '\uE42F';
    private static final char GLYPH_MEDI_QA_MEDI_E = '\uE430';
    private static final char GLYPH_MEDI_QA_FINA_E = '\uE431';
    private static final char GLYPH_INIT_QA_FINA_I = '\uE432';
    private static final char GLYPH_INIT_QA_MEDI_I = '\uE433';
    private static final char GLYPH_MEDI_QA_MEDI_I = '\uE434';
    private static final char GLYPH_MEDI_QA_FINA_I = '\uE435';
    private static final char GLYPH_INIT_QA_FINA_OE = '\uE436';
    private static final char GLYPH_INIT_QA_MEDI_OE = '\uE437';
    private static final char GLYPH_MEDI_QA_MEDI_OE = '\uE438';
    private static final char GLYPH_MEDI_QA_FINA_OE = '\uE439';
    private static final char GLYPH_MEDI_QA_FINA_OE_FVS1 = '\uE43A';
    private static final char GLYPH_INIT_QA_FINA_UE = '\uE43B';
    private static final char GLYPH_INIT_QA_MEDI_UE = '\uE43C';
    private static final char GLYPH_MEDI_QA_MEDI_UE = '\uE43D';
    private static final char GLYPH_MEDI_QA_FINA_UE = '\uE43E';
    private static final char GLYPH_MEDI_QA_FINA_UE_FVS1 = '\uE43F';
    private static final char GLYPH_INIT_QA_FINA_EE = '\uE440';
    private static final char GLYPH_INIT_QA_MEDI_EE = '\uE441';
    private static final char GLYPH_MEDI_QA_MEDI_EE = '\uE442';
    private static final char GLYPH_MEDI_QA_FINA_EE = '\uE443';
    private static final char GLYPH_ISOL_GA = '\uE444';
    private static final char GLYPH_ISOL_GA_FVS3 = '\uE445'; // TODO not in Baiti
    private static final char GLYPH_INIT_GA = '\uE446';
    private static final char GLYPH_INIT_GA_FVS1 = '\uE447';
    private static final char GLYPH_MEDI_GA = '\uE448';
    private static final char GLYPH_MEDI_GA_FVS1 = '\uE449';
    private static final char GLYPH_MEDI_GA_FVS2 = '\uE448'; // TODO matching Baiti, not using
    // \uE44A
    private static final char GLYPH_FINA_GA = '\uE44B';
    private static final char GLYPH_FINA_GA_FVS1 = '\uE44B'; // TODO matching Baiti
    private static final char GLYPH_FINA_GA_FVS3 = '\uE44C'; // TODO matching Baiti
    private static final char GLYPH_FINA_GA_FVS2 = '\uE44D';
    private static final char GLYPH_INIT_GA_FINA_E = '\uE44E';
    private static final char GLYPH_INIT_GA_MEDI_E = '\uE44F';
    private static final char GLYPH_MEDI_GA_MEDI_E = '\uE450';
    private static final char GLYPH_MEDI_GA_FINA_E = '\uE451';
    private static final char GLYPH_INIT_GA_FINA_I = '\uE452';
    private static final char GLYPH_INIT_GA_MEDI_I = '\uE453';
    private static final char GLYPH_MEDI_GA_MEDI_I = '\uE454';
    private static final char GLYPH_MEDI_GA_FINA_I = '\uE455';
    private static final char GLYPH_INIT_GA_FINA_OE = '\uE456';
    private static final char GLYPH_INIT_GA_MEDI_OE = '\uE457';
    private static final char GLYPH_MEDI_GA_MEDI_OE = '\uE458';
    private static final char GLYPH_MEDI_GA_FINA_OE = '\uE459';
    private static final char GLYPH_MEDI_GA_FINA_OE_FVS1 = '\uE45A';
    private static final char GLYPH_INIT_GA_FINA_UE = '\uE45B';
    private static final char GLYPH_INIT_GA_MEDI_UE = '\uE45C';
    private static final char GLYPH_MEDI_GA_MEDI_UE = '\uE45D';
    private static final char GLYPH_MEDI_GA_FINA_UE = '\uE45E';
    private static final char GLYPH_MEDI_GA_FINA_UE_FVS1 = '\uE45F';
    private static final char GLYPH_INIT_GA_FINA_EE = '\uE460';
    private static final char GLYPH_INIT_GA_MEDI_EE = '\uE461';
    private static final char GLYPH_MEDI_GA_MEDI_EE = '\uE462';
    private static final char GLYPH_MEDI_GA_FINA_EE = '\uE463';
    private static final char GLYPH_MEDI_GA_MEDI_MA = '\uE464';
    private static final char GLYPH_MEDI_GA_MEDI_LA = '\uE465';
    private static final char GLYPH_ISOL_MA = '\uE466';
    private static final char GLYPH_INIT_MA = '\uE467';
    private static final char GLYPH_MEDI_MA = '\uE468';
    private static final char GLYPH_FINA_MA = '\uE469';
    private static final char GLYPH_ISOL_LA = '\uE46A';
    private static final char GLYPH_INIT_LA = '\uE46B';
    private static final char GLYPH_MEDI_LA = '\uE46C';
    private static final char GLYPH_FINA_LA = '\uE46D';
    private static final char GLYPH_ISOL_SA = '\uE46E';
    private static final char GLYPH_INIT_SA = '\uE46F';
    private static final char GLYPH_MEDI_SA = '\uE470';
    private static final char GLYPH_FINA_SA = '\uE471';
    private static final char GLYPH_ISOL_SHA = '\uE472';
    private static final char GLYPH_INIT_SHA = '\uE473';
    private static final char GLYPH_MEDI_SHA = '\uE474';
    private static final char GLYPH_FINA_SHA = '\uE475';
    private static final char GLYPH_ISOL_TA = '\uE476';
    private static final char GLYPH_ISOL_TA_FVS1 = '\uE477';
    private static final char GLYPH_INIT_TA = '\uE478';
    private static final char GLYPH_MEDI_TA = '\uE479';
    private static final char GLYPH_MEDI_TA_FVS1 = '\uE47A';
    private static final char GLYPH_MEDI_TA_FVS2 = '\uE47B';
    private static final char GLYPH_FINA_TA = '\uE47C';
    private static final char GLYPH_ISOL_DA = '\uE47D';
    private static final char GLYPH_INIT_DA = '\uE47E';
    private static final char GLYPH_INIT_DA_FVS1 = '\uE47F';
    private static final char GLYPH_MEDI_DA = '\uE480';
    private static final char GLYPH_MEDI_DA_FVS1 = '\uE481';
    private static final char GLYPH_FINA_DA = '\uE482';
    private static final char GLYPH_FINA_DA_FVS1 = '\uE483';
    private static final char GLYPH_ISOL_CHA = '\uE484';
    private static final char GLYPH_INIT_CHA = '\uE485';
    private static final char GLYPH_MEDI_CHA = '\uE486';
    private static final char GLYPH_FINA_CHA = '\uE487';
    private static final char GLYPH_ISOL_JA = '\uE488';
    private static final char GLYPH_ISOL_JA_FVS1 = '\uE489';
    private static final char GLYPH_INIT_JA = '\uE48A';
    private static final char GLYPH_MEDI_JA = '\uE48B';
    private static final char GLYPH_FINA_JA = '\uE48C';
    private static final char GLYPH_FINA_JA_FVS1 = '\uE491'; // same as GLYPH_FINA_YA
    private static final char GLYPH_ISOL_YA = '\uE48D';
    private static final char GLYPH_INIT_YA = '\uE48E';
    private static final char GLYPH_INIT_YA_FVS1 = '\uE48F';
    private static final char GLYPH_MEDI_YA = '\uE398'; // same as GLYPH_MEDI_I
    private static final char GLYPH_MEDI_YA_FVS1 = '\uE490'; // TODO matching Baiti
    private static final char GLYPH_FINA_YA = '\uE491';
    private static final char GLYPH_ISOL_RA = '\uE492';
    private static final char GLYPH_INIT_RA = '\uE493';
    private static final char GLYPH_MEDI_RA = '\uE494';
    private static final char GLYPH_FINA_RA = '\uE495';
    private static final char GLYPH_ISOL_WA = '\uE496';
    private static final char GLYPH_INIT_WA = '\uE497';
    private static final char GLYPH_WORD_U = '\uE498';
    private static final char GLYPH_MEDI_WA_FVS1 = '\uE499'; // TODO matching Baiti
    private static final char GLYPH_FINA_WA_FVS1 = '\uE49A'; // TODO matching Baiti
    private static final char GLYPH_FINA_WA = '\uE49B'; // TODO matching Baiti
    private static final char GLYPH_ISOL_FA = '\uE49C';
    private static final char GLYPH_INIT_FA = '\uE49D';
    private static final char GLYPH_MEDI_FA = '\uE49E';
    private static final char GLYPH_FINA_FA = '\uE49F';
    private static final char GLYPH_INIT_FA_FINA_A = '\uE4A0';
    private static final char GLYPH_INIT_FA_MEDI_A = '\uE4A1';
    private static final char GLYPH_MEDI_FA_MEDI_A = '\uE4A2';
    private static final char GLYPH_MEDI_FA_FINA_A = '\uE4A3';
    private static final char GLYPH_INIT_FA_FINA_E = '\uE4A4';
    private static final char GLYPH_INIT_FA_MEDI_E = '\uE4A5';
    private static final char GLYPH_MEDI_FA_MEDI_E = '\uE4A6';
    private static final char GLYPH_MEDI_FA_FINA_E = '\uE4A7';
    private static final char GLYPH_INIT_FA_FINA_I = '\uE4A8';
    private static final char GLYPH_INIT_FA_MEDI_I = '\uE4A9';
    private static final char GLYPH_MEDI_FA_MEDI_I = '\uE4AA';
    private static final char GLYPH_MEDI_FA_FINA_I = '\uE4AB';
    private static final char GLYPH_INIT_FA_FINA_O = '\uE4AC';
    private static final char GLYPH_INIT_FA_MEDI_O = '\uE4AD';
    private static final char GLYPH_MEDI_FA_MEDI_O = '\uE4AE';
    private static final char GLYPH_MEDI_FA_FINA_O = '\uE4AF';
    private static final char GLYPH_INIT_FA_FINA_U = '\uE4B0';
    private static final char GLYPH_INIT_FA_MEDI_U = '\uE4B1';
    private static final char GLYPH_MEDI_FA_MEDI_U = '\uE4B2';
    private static final char GLYPH_MEDI_FA_FINA_U = '\uE4B3';
    private static final char GLYPH_INIT_FA_FINA_OE = '\uE4B4';
    private static final char GLYPH_INIT_FA_MEDI_OE = '\uE4B5';
    private static final char GLYPH_MEDI_FA_MEDI_OE = '\uE4B6';
    private static final char GLYPH_MEDI_FA_FINA_OE = '\uE4B7';
    private static final char GLYPH_MEDI_FA_FINA_OE_FVS1 = '\uE4B8';
    private static final char GLYPH_INIT_FA_FINA_UE = '\uE4B9';
    private static final char GLYPH_INIT_FA_MEDI_UE = '\uE4BA';
    private static final char GLYPH_MEDI_FA_MEDI_UE = '\uE4BB';
    private static final char GLYPH_MEDI_FA_FINA_UE = '\uE4BC';
    private static final char GLYPH_MEDI_FA_FINA_UE_FVS1 = '\uE4BD';
    private static final char GLYPH_INIT_FA_FINA_EE = '\uE4BE';
    private static final char GLYPH_INIT_FA_MEDI_EE = '\uE4BF';
    private static final char GLYPH_MEDI_FA_MEDI_EE = '\uE4C0';
    private static final char GLYPH_MEDI_FA_FINA_EE = '\uE4C1';
    private static final char GLYPH_MEDI_FA_MEDI_MA = '\uE4C2';
    private static final char GLYPH_MEDI_FA_MEDI_LA = '\uE4C3';
    private static final char GLYPH_ISOL_KA = '\uE4C4';
    private static final char GLYPH_INIT_KA = '\uE4C5';
    private static final char GLYPH_MEDI_KA = '\uE4C6';
    private static final char GLYPH_FINA_KA = '\uE4C7';
    private static final char GLYPH_INIT_KA_FINA_A = '\uE4C8';
    private static final char GLYPH_INIT_KA_MEDI_A = '\uE4C9';
    private static final char GLYPH_MEDI_KA_MEDI_A = '\uE4CA';
    private static final char GLYPH_MEDI_KA_FINA_A = '\uE4CB';
    private static final char GLYPH_INIT_KA_FINA_E = '\uE4CC';
    private static final char GLYPH_INIT_KA_MEDI_E = '\uE4CD';
    private static final char GLYPH_MEDI_KA_MEDI_E = '\uE4CE';
    private static final char GLYPH_MEDI_KA_FINA_E = '\uE4CF';
    private static final char GLYPH_INIT_KA_FINA_I = '\uE4D0';
    private static final char GLYPH_INIT_KA_MEDI_I = '\uE4D1';
    private static final char GLYPH_MEDI_KA_MEDI_I = '\uE4D2';
    private static final char GLYPH_MEDI_KA_FINA_I = '\uE4D3';
    private static final char GLYPH_INIT_KA_FINA_O = '\uE4D4';
    private static final char GLYPH_INIT_KA_MEDI_O = '\uE4D5';
    private static final char GLYPH_MEDI_KA_MEDI_O = '\uE4D6';
    private static final char GLYPH_MEDI_KA_FINA_O = '\uE4D7';
    private static final char GLYPH_INIT_KA_FINA_U = '\uE4D8';
    private static final char GLYPH_INIT_KA_MEDI_U = '\uE4D9';
    private static final char GLYPH_MEDI_KA_MEDI_U = '\uE4DA';
    private static final char GLYPH_MEDI_KA_FINA_U = '\uE4DB';
    private static final char GLYPH_INIT_KA_FINA_OE = '\uE4DC';
    private static final char GLYPH_INIT_KA_MEDI_OE = '\uE4DD';
    private static final char GLYPH_MEDI_KA_MEDI_OE = '\uE4DE';
    private static final char GLYPH_MEDI_KA_FINA_OE = '\uE4DF';
    private static final char GLYPH_MEDI_KA_FINA_OE_FVS1 = '\uE4E0';
    private static final char GLYPH_INIT_KA_FINA_UE = '\uE4E1';
    private static final char GLYPH_INIT_KA_MEDI_UE = '\uE4E2';
    private static final char GLYPH_MEDI_KA_MEDI_UE = '\uE4E3';
    private static final char GLYPH_MEDI_KA_FINA_UE = '\uE4E4';
    private static final char GLYPH_MEDI_KA_FINA_UE_FVS1 = '\uE4E5';
    private static final char GLYPH_INIT_KA_FINA_EE = '\uE4E6';
    private static final char GLYPH_INIT_KA_MEDI_EE = '\uE4E7';
    private static final char GLYPH_MEDI_KA_MEDI_EE = '\uE4E8';
    private static final char GLYPH_MEDI_KA_FINA_EE = '\uE4E9';
    private static final char GLYPH_MEDI_KA_MEDI_MA = '\uE4EA';
    private static final char GLYPH_MEDI_KA_MEDI_LA = '\uE4EB';
    private static final char GLYPH_ISOL_KHA = '\uE4EC';
    private static final char GLYPH_INIT_KHA = '\uE4ED';
    private static final char GLYPH_MEDI_KHA = '\uE4EE';
    private static final char GLYPH_FINA_KHA = '\uE4EF';
    private static final char GLYPH_INIT_KHA_FINA_A = '\uE4F0';
    private static final char GLYPH_INIT_KHA_MEDI_A = '\uE4F1';
    private static final char GLYPH_MEDI_KHA_MEDI_A = '\uE4F2';
    private static final char GLYPH_MEDI_KHA_FINA_A = '\uE4F3';
    private static final char GLYPH_INIT_KHA_FINA_E = '\uE4F4';
    private static final char GLYPH_INIT_KHA_MEDI_E = '\uE4F5';
    private static final char GLYPH_MEDI_KHA_MEDI_E = '\uE4F6';
    private static final char GLYPH_MEDI_KHA_FINA_E = '\uE4F7';
    private static final char GLYPH_INIT_KHA_FINA_I = '\uE4F8';
    private static final char GLYPH_INIT_KHA_MEDI_I = '\uE4F9';
    private static final char GLYPH_MEDI_KHA_MEDI_I = '\uE4FA';
    private static final char GLYPH_MEDI_KHA_FINA_I = '\uE4FB';
    private static final char GLYPH_INIT_KHA_FINA_O = '\uE4FC';
    private static final char GLYPH_INIT_KHA_MEDI_O = '\uE4FD';
    private static final char GLYPH_MEDI_KHA_MEDI_O = '\uE4FE';
    private static final char GLYPH_MEDI_KHA_FINA_O = '\uE4FF';
    private static final char GLYPH_INIT_KHA_FINA_U = '\uE500';
    private static final char GLYPH_INIT_KHA_MEDI_U = '\uE501';
    private static final char GLYPH_MEDI_KHA_MEDI_U = '\uE502';
    private static final char GLYPH_MEDI_KHA_FINA_U = '\uE503';
    private static final char GLYPH_INIT_KHA_FINA_OE = '\uE504';
    private static final char GLYPH_INIT_KHA_MEDI_OE = '\uE505';
    private static final char GLYPH_MEDI_KHA_MEDI_OE = '\uE506';
    private static final char GLYPH_MEDI_KHA_FINA_OE = '\uE507';
    private static final char GLYPH_MEDI_KHA_FINA_OE_FVS1 = '\uE508';
    private static final char GLYPH_INIT_KHA_FINA_UE = '\uE509';
    private static final char GLYPH_INIT_KHA_MEDI_UE = '\uE50A';
    private static final char GLYPH_MEDI_KHA_MEDI_UE = '\uE50B';
    private static final char GLYPH_MEDI_KHA_FINA_UE = '\uE50C';
    private static final char GLYPH_MEDI_KHA_FINA_UE_FVS1 = '\uE50D';
    private static final char GLYPH_INIT_KHA_FINA_EE = '\uE50E';
    private static final char GLYPH_INIT_KHA_MEDI_EE = '\uE50F';
    private static final char GLYPH_MEDI_KHA_MEDI_EE = '\uE510';
    private static final char GLYPH_MEDI_KHA_FINA_EE = '\uE511';
    private static final char GLYPH_MEDI_KHA_MEDI_MA = '\uE512';
    private static final char GLYPH_MEDI_KHA_MEDI_LA = '\uE513';
    private static final char GLYPH_ISOL_TSA = '\uE514';
    private static final char GLYPH_INIT_TSA = '\uE515';
    private static final char GLYPH_MEDI_TSA = '\uE516';
    private static final char GLYPH_FINA_TSA = '\uE517';
    private static final char GLYPH_ISOL_ZA = '\uE518';
    private static final char GLYPH_INIT_ZA = '\uE519';
    private static final char GLYPH_MEDI_ZA = '\uE51A';
    private static final char GLYPH_FINA_ZA = '\uE51B';
    private static final char GLYPH_ISOL_HAA = '\uE51C';
    private static final char GLYPH_INIT_HAA = '\uE51D';
    private static final char GLYPH_MEDI_HAA = '\uE51E';
    private static final char GLYPH_FINA_HAA = '\uE51F';
    private static final char GLYPH_ISOL_ZRA = '\uE520';
    private static final char GLYPH_INIT_ZRA = '\uE521';
    private static final char GLYPH_MEDI_ZRA = '\uE522';
    private static final char GLYPH_FINA_ZRA = '\uE523';
    private static final char GLYPH_ISOL_LHA = '\uE524';
    private static final char GLYPH_INIT_LHA = '\uE525';
    private static final char GLYPH_MEDI_LHA = '\uE526';
    private static final char GLYPH_FINA_LHA = '\uE527';
    private static final char GLYPH_ISOL_ZHI = '\uE528';
    private static final char GLYPH_INIT_ZHI = '\uE529';
    private static final char GLYPH_MEDI_ZHI = '\uE52A';
    private static final char GLYPH_FINA_ZHI = '\uE52B';
    private static final char GLYPH_ISOL_CHI = '\uE52C';
    private static final char GLYPH_INIT_CHI = '\uE52D';
    private static final char GLYPH_MEDI_CHI = '\uE52E';
    private static final char GLYPH_FINA_CHI = '\uE52F';
    private static final char GLYPH_FINA_SA_FVS1 = '\uE530';
    private static final char GLYPH_FINA_SA_FVS2 = '\uE531';
    private static final char GLYPH_FINA_BA_FVS1 = '\uE532';
    private static final char GLYPH_WORD_UU = '\uE533';
    private static final char GLYPH_WORD_BUU = '\uE534';
    private static final char GLYPH_MEDI_BA_MEDI_OE_FVS1 = '\uE535';
    private static final char GLYPH_MEDI_BA_MEDI_UE_FVS1 = '\uE536';
    private static final char GLYPH_MEDI_PA_MEDI_OE_FVS1 = '\uE537';
    private static final char GLYPH_MEDI_PA_MEDI_UE_FVS1 = '\uE538';
    private static final char GLYPH_MEDI_QA_MEDI_OE_FVS1 = '\uE539';
    private static final char GLYPH_MEDI_QA_MEDI_UE_FVS1 = '\uE53A';
    private static final char GLYPH_MEDI_GA_MEDI_OE_FVS1 = '\uE53B';
    private static final char GLYPH_MEDI_GA_MEDI_UE_FVS1 = '\uE53C';
    private static final char GLYPH_MEDI_FA_MEDI_OE_FVS1 = '\uE53D';
    private static final char GLYPH_MEDI_FA_MEDI_UE_FVS1 = '\uE53E';
    private static final char GLYPH_MEDI_KA_MEDI_OE_FVS1 = '\uE53F';
    private static final char GLYPH_MEDI_KA_MEDI_UE_FVS1 = '\uE540';
    private static final char GLYPH_MEDI_KHA_MEDI_OE_FVS1 = '\uE541';
    private static final char GLYPH_MEDI_KHA_MEDI_UE_FVS1 = '\uE542';
    private static final char GLYPH_MEDI_MA_MEDI_MA = '\uE544';
    private static final char GLYPH_MEDI_MA_MEDI_LA = '\uE545';
    private static final char GLYPH_MEDI_LA_MEDI_LA = '\uE546';
    private static final char GLYPH_MEDI_ANG_MEDI_NA_FVS1 = '\uE547';
    private static final char GLYPH_MEDI_ANG_FINA_QA = '\uE548';
    private static final char GLYPH_MEDI_ANG_FINA_GA = '\uE549';
    private static final char GLYPH_MEDI_BA_MEDI_QA = '\uE54A';
    private static final char GLYPH_MEDI_BA_MEDI_GA = '\uE54B';
    private static final char GLYPH_MEDI_PA_MEDI_QA = '\uE54C';
    private static final char GLYPH_MEDI_PA_MEDI_GA = '\uE54D';
    private static final char GLYPH_MEDI_FA_MEDI_QA = '\uE54E';
    private static final char GLYPH_MEDI_FA_MEDI_GA = '\uE54F';
    private static final char GLYPH_MEDI_KA_MEDI_QA = '\uE550';
    private static final char GLYPH_MEDI_KA_MEDI_GA = '\uE551';
    private static final char GLYPH_MEDI_KHA_MEDI_QA = '\uE552';
    private static final char GLYPH_MEDI_KHA_MEDI_GA = '\uE553';
    private static final char GLYPH_MEDI_BA_MEDI_NA_FVS1 = '\uE554';
    private static final char GLYPH_MEDI_PA_MEDI_NA_FVS1 = '\uE555';
    private static final char GLYPH_MEDI_GA_MEDI_NA_FVS1 = '\uE556';
    private static final char GLYPH_MEDI_FA_MEDI_NA_FVS1 = '\uE557';
    private static final char GLYPH_MEDI_KA_MEDI_NA_FVS1 = '\uE558';
    private static final char GLYPH_MEDI_KHA_MEDI_NA_FVS1 = '\uE559';
    private static final char GLYPH_INIT_QA_FINA_OE_FVS1 = '\uE55A';
    private static final char GLYPH_INIT_QA_FINA_UE_FVS1 = '\uE55B';
    private static final char GLYPH_INIT_GA_FINA_OE_FVS1 = '\uE55C';
    private static final char GLYPH_INIT_GA_FINA_UE_FVS1 = '\uE55D';
    private static final char GLYPH_INIT_QA_MEDI_OE_FVS1 = '\uE55E';
    private static final char GLYPH_INIT_QA_MEDI_UE_FVS1 = '\uE55F';
    private static final char GLYPH_INIT_GA_MEDI_OE_FVS1 = '\uE560';
    private static final char GLYPH_INIT_GA_MEDI_UE_FVS1 = '\uE561';
    private static final char GLYPH_ISOL_QA_FVS2 = '\uE564';
    private static final char GLYPH_INIT_QA_FVS2 = '\uE565';
    private static final char GLYPH_ISOL_QA_FVS1 = '\uE566'; // TODO matching Baiti
    private static final char GLYPH_INIT_QA_FVS3 = '\uE567';
    private static final char GLYPH_MEDI_QA_FVS3 = '\uE568';
    private static final char GLYPH_INIT_QA_FVS1_FINA_E = '\uE569';
    private static final char GLYPH_INIT_QA_FVS1_MEDI_E = '\uE56A';
    private static final char GLYPH_MEDI_QA_FVS1_MEDI_E = '\uE56B';
    private static final char GLYPH_MEDI_QA_FVS1_FINA_E = '\uE56C';
    private static final char GLYPH_INIT_QA_FVS1_FINA_I = '\uE56D';
    private static final char GLYPH_INIT_QA_FVS1_MEDI_I = '\uE56E';
    private static final char GLYPH_MEDI_QA_FVS1_MEDI_I = '\uE56F';
    private static final char GLYPH_MEDI_QA_FVS1_FINA_I = '\uE570';
    private static final char GLYPH_INIT_QA_FVS1_FINA_OE = '\uE571';
    private static final char GLYPH_INIT_QA_FVS1_MEDI_OE = '\uE572';
    private static final char GLYPH_MEDI_QA_FVS1_MEDI_OE = '\uE573';
    private static final char GLYPH_MEDI_QA_FVS1_FINA_OE = '\uE574';
    private static final char GLYPH_MEDI_QA_FVS1_FINA_OE_FVS1 = '\uE575';
    private static final char GLYPH_INIT_QA_FVS1_FINA_UE = '\uE576';
    private static final char GLYPH_INIT_QA_FVS1_MEDI_UE = '\uE577';
    private static final char GLYPH_MEDI_QA_FVS1_MEDI_UE = '\uE578';
    private static final char GLYPH_MEDI_QA_FVS1_FINA_UE = '\uE579';
    private static final char GLYPH_MEDI_QA_FVS1_FINA_UE_FVS1 = '\uE57A';
    private static final char GLYPH_INIT_QA_FVS1_FINA_EE = '\uE57B';
    private static final char GLYPH_INIT_QA_FVS1_MEDI_EE = '\uE57C';
    private static final char GLYPH_MEDI_QA_FVS1_MEDI_EE = '\uE57D';
    private static final char GLYPH_MEDI_QA_FVS1_FINA_EE = '\uE57E';
    private static final char GLYPH_ISOL_GA_FVS1 = '\uE57F'; // TODO matching Baiti
    private static final char GLYPH_ISOL_GA_FVS2 = '\uE580'; // TODO matching Baiti
    private static final char GLYPH_INIT_GA_FVS3 = '\uE581'; // TODO not in Baiti
    private static final char GLYPH_INIT_GA_FVS2 = '\uE582'; // TODO matching Baiti
    private static final char GLYPH_MEDI_GA_FVS3 = '\uE583';
    private static final char GLYPH_MEDI_WA = '\uE584'; // TODO matching Baiti
    private static final char GLYPH_INIT_A_FVS1 = '\uE585';
    // TODO GLYPH_MEDI_I_FVS2 and GLYPH_MEDI_I_FVS3 have not been standardized in Unicode yet
    // Matching to Baiti
    private static final char GLYPH_MEDI_I_FVS2 = '\uE586';
    private static final char GLYPH_FINA_NA_FVS2 = '\uE587';
    private static final char GLYPH_BIRGA_1 = '\uE588';
    private static final char GLYPH_BIRGA_2 = '\uE589';
    private static final char GLYPH_BIRGA_3 = '\uE58A';
    private static final char GLYPH_BIRGA_4 = '\uE58B';
    private static final char GLYPH_NIRUGU_FVS2 = '\uE58F';
    private static final char GLYPH_NIRUGU_FVS3 = '\uE590';
    private static final char GLYPH_INIT_GA_FVS1_FINA_E = '\uE594';
    private static final char GLYPH_INIT_GA_FVS1_MEDI_E = '\uE595';
    private static final char GLYPH_MEDI_GA_FVS1_MEDI_E = '\uE596';
    private static final char GLYPH_MEDI_GA_FVS1_FINA_E = '\uE597';
    private static final char GLYPH_INIT_GA_FVS1_FINA_I = '\uE598';
    private static final char GLYPH_INIT_GA_FVS1_MEDI_I = '\uE599';
    private static final char GLYPH_MEDI_GA_FVS1_MEDI_I = '\uE59A';
    private static final char GLYPH_MEDI_GA_FVS1_FINA_I = '\uE59B';
    private static final char GLYPH_INIT_GA_FVS1_FINA_OE = '\uE59C';
    private static final char GLYPH_INIT_GA_FVS1_MEDI_OE = '\uE59D';
    private static final char GLYPH_MEDI_GA_FVS1_MEDI_OE = '\uE59E';
    private static final char GLYPH_MEDI_GA_FVS1_FINA_OE = '\uE59F';
    private static final char GLYPH_MEDI_GA_FVS1_FINA_OE_FVS1 = '\uE5A0';
    private static final char GLYPH_INIT_GA_FVS1_FINA_UE = '\uE5A1';
    private static final char GLYPH_INIT_GA_FVS1_MEDI_UE = '\uE5A2';
    private static final char GLYPH_MEDI_GA_FVS1_MEDI_UE = '\uE5A3';
    private static final char GLYPH_MEDI_GA_FVS1_FINA_UE = '\uE5A4';
    private static final char GLYPH_MEDI_GA_FVS1_FINA_UE_FVS1 = '\uE5A5';
    private static final char GLYPH_INIT_GA_FVS1_FINA_EE = '\uE5A6';
    private static final char GLYPH_INIT_GA_FVS1_MEDI_EE = '\uE5A7';
    private static final char GLYPH_MEDI_GA_FVS1_MEDI_EE = '\uE5A8';
    private static final char GLYPH_MEDI_GA_FVS1_FINA_EE = '\uE5A9';
    private static final char GLYPH_MEDI_QA_FVS1_MEDI_OE_FVS1 = '\uE5AA';
    private static final char GLYPH_MEDI_QA_FVS1_MEDI_UE_FVS1 = '\uE5AB';
    private static final char GLYPH_MEDI_GA_FVS1_MEDI_OE_FVS1 = '\uE5AC';
    private static final char GLYPH_MEDI_GA_FVS1_MEDI_UE_FVS1 = '\uE5AD';
    private static final char GLYPH_INIT_BA_FINA_OE_FVS1 = '\uE5B4';
    private static final char GLYPH_INIT_BA_FINA_UE_FVS1 = '\uE5B5';
    private static final char GLYPH_INIT_BA_MEDI_OE_FVS1 = '\uE5B6';
    private static final char GLYPH_INIT_BA_MEDI_UE_FVS1 = '\uE5B7';
    private static final char GLYPH_INIT_PA_FINA_OE_FVS1 = '\uE5B8';
    private static final char GLYPH_INIT_PA_FINA_UE_FVS1 = '\uE5B9';
    private static final char GLYPH_INIT_PA_MEDI_OE_FVS1 = '\uE5BA';
    private static final char GLYPH_INIT_PA_MEDI_UE_FVS1 = '\uE5BB';
    private static final char GLYPH_INIT_QA_FVS1_FINA_OE_FVS1 = '\uE5BC';
    private static final char GLYPH_INIT_QA_FVS1_FINA_UE_FVS1 = '\uE5BD';
    private static final char GLYPH_INIT_QA_FVS1_MEDI_OE_FVS1 = '\uE5BE';
    private static final char GLYPH_INIT_QA_FVS1_MEDI_UE_FVS1 = '\uE5BF';
    private static final char GLYPH_INIT_GA_FVS1_FINA_OE_FVS1 = '\uE5C0';
    private static final char GLYPH_INIT_GA_FVS1_FINA_UE_FVS1 = '\uE5C1';
    private static final char GLYPH_INIT_GA_FVS1_MEDI_OE_FVS1 = '\uE5C2';
    private static final char GLYPH_INIT_GA_FVS1_MEDI_UE_FVS1 = '\uE5C3';
    private static final char GLYPH_INIT_FA_FINA_OE_FVS1 = '\uE5C4';
    private static final char GLYPH_INIT_FA_FINA_UE_FVS1 = '\uE5C5';
    private static final char GLYPH_INIT_FA_MEDI_OE_FVS1 = '\uE5C6';
    private static final char GLYPH_INIT_FA_MEDI_UE_FVS1 = '\uE5C7';
    private static final char GLYPH_INIT_KA_FINA_OE_FVS1 = '\uE5C8';
    private static final char GLYPH_INIT_KA_FINA_UE_FVS1 = '\uE5C9';
    private static final char GLYPH_INIT_KA_MEDI_OE_FVS1 = '\uE5CA';
    private static final char GLYPH_INIT_KA_MEDI_UE_FVS1 = '\uE5CB';
    private static final char GLYPH_INIT_KHA_FINA_OE_FVS1 = '\uE5CC';
    private static final char GLYPH_INIT_KHA_FINA_UE_FVS1 = '\uE5CD';
    private static final char GLYPH_INIT_KHA_MEDI_OE_FVS1 = '\uE5CE';
    private static final char GLYPH_INIT_KHA_MEDI_UE_FVS1 = '\uE5CF';

}
