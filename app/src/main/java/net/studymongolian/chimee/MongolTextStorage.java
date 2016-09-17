package net.studymongolian.chimee;

// This class keeps track of the cursor position for rendered and unicode mongolian text
// to be used in a Mongolian EditText.
public class MongolTextStorage {

    private StringBuilder unicodeText = new StringBuilder();
    private MongolUnicodeRenderer renderer = MongolUnicodeRenderer.INSTANCE;
    private String cursorHolder = String.valueOf(MongolUnicodeRenderer.CURSOR_HOLDER);
    private int unicodeIndexForCursor = -1;
    private char space = ' ';
    private char questionMark = '?';
    private char exclamationPoint = '!';
    private char newLine = '\n';


    public int glyphIndexForCursor = -1;

    public String getUnicode() {
        return unicodeText.toString();
    }
    public void setUnicode(String newString) {
        unicodeText.setLength(0);
        unicodeText.append(newString);
        unicodeIndexForCursor = unicodeText.length();
    }

    public String render() {
        StringBuilder tempText = unicodeText;
        tempText.insert(unicodeIndexForCursor, cursorHolder);
        String renderedText = renderer.unicodeToGlyphs(tempText.toString());
        int index = renderedText.indexOf(cursorHolder);
        if (index > -1) {
            renderedText = renderedText.replace(cursorHolder, "");
            glyphIndexForCursor = index;
        }
        return renderedText;
    }

    public void clear() {
        unicodeText.setLength(0);
        unicodeIndexForCursor = 0;
        glyphIndexForCursor = 0;
    }

    public void deleteBackwardsAtGlyphRange(int location, int length) {

        if (length == 0) { // cursor position

            // update unicode index
            updateUnicodeIndex(location);

            // return if at beginning
            if (unicodeIndexForCursor <= 0) {
                return;
            }

            // delete all invisible formatting characters + one visible char
            char character;
            do {
                unicodeIndexForCursor -= 1;
                character = unicodeText.charAt(unicodeIndexForCursor);
                unicodeText.deleteCharAt(unicodeIndexForCursor);

            } while (unicodeIndexForCursor > 0 && isFormattingChar(character));

        }else{ // range of text is selected

            // just delete the current range

            // get unicode range
            int unicodeStart = renderer.getUnicodeIndex(unicodeText.toString(), location);
            int unicodeEnd = renderer.getUnicodeIndex(unicodeText.toString(), location + length);

            // delete range
            unicodeText.delete(unicodeStart, unicodeEnd);
            unicodeIndexForCursor = location;
        }
    }

    private boolean isFormattingChar(char character) {

        return (character == MongolUnicodeRenderer.Uni.FVS1 ||
                character == MongolUnicodeRenderer.Uni.FVS2 ||
                character == MongolUnicodeRenderer.Uni.FVS3 ||
                character == MongolUnicodeRenderer.Uni.MVS ||
                character == MongolUnicodeRenderer.Uni.ZWJ);
    }

    private void updateUnicodeIndex(int glyphIndex) {
        if (glyphIndex != glyphIndexForCursor) {
            glyphIndexForCursor = glyphIndex;
            unicodeIndexForCursor = renderer.getUnicodeIndex(unicodeText.toString(), glyphIndexForCursor);
        }
    }

    public String unicodeForGlyphRange(int location, int length) {

        if (length == 0) { // cursor position

            return "";

        } else { // range of text is selected

            // get unicode range
            int unicodeStart = renderer.getUnicodeIndex(unicodeText.toString(), location);
            int unicodeEnd = renderer.getUnicodeIndex(unicodeText.toString(), location + length);

            return unicodeText.substring(unicodeStart, unicodeEnd).toString();

        }
    }

    public void insertUnicodeForGlyphRange(int location, int length, String unicodeToInsert) {

        // FIXME: this method assumes no emoji

        if (length == 0) { // caret position

            // if glyph index has changed, need to update unicode index
            updateUnicodeIndex(location);

        }else{ // range of text is selected

            // get unicode range
            int unicodeStart = renderer.getUnicodeIndex(unicodeText.toString(), location);
            int unicodeEnd = renderer.getUnicodeIndex(unicodeText.toString(), location + length);

            // delete range
            unicodeText.delete(unicodeStart, unicodeEnd);
            unicodeIndexForCursor = location;

        }

        // insert new unicode
        unicodeText.insert(unicodeIndexForCursor, unicodeToInsert);
        unicodeIndexForCursor += unicodeToInsert.length();

    }

    public void replaceWordAtCursorWith(String replacementString, int glyphIndex) {

        //let myReplacementString = ScalarString(replacementString)

        // if glyph index has changed, need to update unicode index
        updateUnicodeIndex(glyphIndex);

        // get the range of the whole word
        int originalPosition = unicodeIndexForCursor;

        // get the start index
        int startIndex = originalPosition;
        if (originalPosition > 0) {
            for (int i = originalPosition - 1; i >= 0; i--) {

                if (unicodeText.charAt(i) == MongolUnicodeRenderer.Uni.NNBS) {
                    // Stop at NNBS.
                    // Consider it part of the suffix
                    // But consider anything before as a separate word
                    startIndex = i;
                    break;
                } else if (renderer.isMongolian(unicodeText.charAt(i))) {
                    startIndex = i;
                } else if (unicodeText.charAt(i) == space && replacementString.charAt(0) == MongolUnicodeRenderer.Uni.NNBS) {
                    // allow a single space before the word to be replaced if the replacement word starts with NNBS
                    startIndex = i;
                    break;
                } else {
                    break;
                }
            }
        }

        // do a simple insert if not following any Mongol characters
        if (startIndex == originalPosition) {
            unicodeText.insert(startIndex, replacementString);
            unicodeIndexForCursor = startIndex + replacementString.length();
            return;
        }

        // get the end index
        int endIndex = originalPosition;
        for (int i = originalPosition; i < unicodeText.length(); i++) {

            if (renderer.isMongolian(unicodeText.charAt(i))) {
                endIndex = i + 1; // end index is exclusive
            } else {
                break;
            }
        }

        // replace range with new word
        unicodeText = unicodeText.replace(startIndex, endIndex, replacementString);
        unicodeIndexForCursor = startIndex + replacementString.length();
    }

    public char unicodeCharBeforeCursor(int glyphIndex) {

        // if glyph index has changed, need to update unicode index
        updateUnicodeIndex(glyphIndex);


        if (unicodeIndexForCursor > 0) {
            return unicodeText.charAt(unicodeIndexForCursor - 1);
        }

        return Character.MIN_VALUE; // null (\u0000)
    }

    /// Gets the Mongolian word/characters before the cursor position
    ///
    /// - warning: Only gets called if cursor is adjacent to a Mongolian character
    /// - parameter glyphIndex: glyph index (not unicode index) of the cursor
    /// - returns: an optional string of the Mongolian characters before cursor
    public String unicodeOneWordBeforeCursor(int glyphIndex) {

        // if glyph index has changed, need to update unicode index
        updateUnicodeIndex(glyphIndex);

        int startPosition = unicodeIndexForCursor - 1;
        if (startPosition < 0) {
            return "";
        }

        if (!renderer.isMongolian(unicodeText.charAt(startPosition))) {
            return "";
        }

        // Get the word
        StringBuilder word = new StringBuilder();
        for (int i = startPosition; i >= 0; i--) {

            if (unicodeText.charAt(i) == MongolUnicodeRenderer.Uni.NNBS) {
                // Stop at NNBS.
                // Consider it part of the suffix
                // But consider anything before as a separate word
                word.insert(0, unicodeText.charAt(i));
                break;
            } else if (renderer.isMongolian(unicodeText.charAt(i))) {
                word.insert(0, unicodeText.charAt(i));
            } else {
                break;
            }
        }

        return word.toString();
    }

    /// Gets the two Mongolian words before the cursor position
    ///
    /// - warning: Only gets called if cursor is after a Mongolian character
    /// - parameter glyphIndex: glyph index (not unicode index) of the cursor
    /// - returns: tuple of optional strings: (first word from cursor, second word from cursor)
    public TwoStrings unicodeTwoWordsBeforeCursor(int glyphIndex) {

        // if glyph index has changed, need to update unicode index
        updateUnicodeIndex(glyphIndex);

        int startPosition = unicodeIndexForCursor - 1;
        // empty
        if (startPosition < 0) {
            return new TwoStrings("", "");
        }
        // not mongolian char or nnbs
        if (!renderer.isMongolian(unicodeText.charAt(startPosition)) &&
                unicodeText.charAt(startPosition) != MongolUnicodeRenderer.Uni.NNBS) {
            return new TwoStrings("", "");
        }


        // Get the words
        boolean firstWordHasEnded = false; // flag if between words, allow single space/NNBS
        StringBuilder words = new StringBuilder();
        for (int i = startPosition; i >= 0; i--) {

            if (unicodeText.charAt(i) == MongolUnicodeRenderer.Uni.NNBS) {
                // Stop at NNBS.
                // Consider it part of the suffix
                // But consider anything before as a separate word
                words.insert(0, unicodeText.charAt(i));
                if (firstWordHasEnded) {
                    break;
                }
                firstWordHasEnded = true;
                words.insert(0, space); // word delimeter is space
            } else if (unicodeText.charAt(i) == space) {
                // First space is kept as a delimeter between words
                if (firstWordHasEnded) {
                    break;
                }
                firstWordHasEnded = true;
                words.insert(0, unicodeText.charAt(i));
            } else if (renderer.isMongolian(unicodeText.charAt(i))) {
                words.insert(0, unicodeText.charAt(i));
            } else {
                break;
            }
        }

        // return (firstWordBackFromCursor, secondWordBackFromCursor)

        String wordsArray[] = words.toString().trim().split(String.valueOf(space));
        if (wordsArray.length == 1) {
            return new TwoStrings(wordsArray[0], "");
        }else if (wordsArray.length > 1) {
            return new TwoStrings(wordsArray[wordsArray.length - 1], wordsArray[wordsArray.length - 2]);
        }else{
            return new TwoStrings("", "");
        }
    }
    
}