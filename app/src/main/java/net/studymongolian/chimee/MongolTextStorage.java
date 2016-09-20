package net.studymongolian.chimee;

import android.text.Editable;
import android.text.SpannableStringBuilder;

// This class keeps track of the cursor position for rendered and unicode mongolian text
// to be used in a Mongolian EditText.
public class MongolTextStorage {

    private Editable unicodeText = new SpannableStringBuilder();
    private MongolUnicodeRenderer renderer = MongolUnicodeRenderer.INSTANCE;
    private String cursorHolder = String.valueOf(MongolUnicodeRenderer.CURSOR_HOLDER);
    private int unicodeIndexForCursor = -1;
    private char space = ' ';
    private char questionMark = '?';
    private char exclamationPoint = '!';
    private char newLine = '\n';


    public int glyphIndexForCursor = -1;

    public Editable getUnicode() {
        return unicodeText;
    }
    public void setUnicode(CharSequence newString) {
        unicodeText.clear();
        unicodeText.append(newString);
        unicodeIndexForCursor = unicodeText.length();
    }

    public String render() {
        StringBuilder tempText = new StringBuilder(unicodeText);
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
        unicodeText.clear();
        unicodeIndexForCursor = 0;
        glyphIndexForCursor = 0;
    }

    public void deleteBackwardsAtGlyphRange(int startIndex, int endIndex) {

        if (startIndex >= endIndex) { // cursor position

            // update unicode index
            updateUnicodeIndex(startIndex);

            // return if at beginning
            if (unicodeIndexForCursor <= 0) {
                return;
            }

            // delete all invisible formatting characters + one visible char
            char character;
            do {
                unicodeIndexForCursor -= 1;
                character = unicodeText.charAt(unicodeIndexForCursor);
                unicodeText.delete(unicodeIndexForCursor, unicodeIndexForCursor + 1);

            } while (unicodeIndexForCursor > 0 && isFormattingChar(character));

        }else{ // range of text is selected

            // just delete the current range

            // get unicode range
            int unicodeStart = renderer.getUnicodeIndex(unicodeText.toString(), startIndex);
            int unicodeEnd = renderer.getUnicodeIndex(unicodeText.toString(), endIndex);

            // delete range
            unicodeText.delete(unicodeStart, unicodeEnd);
            unicodeIndexForCursor = unicodeStart; // FIXME in Swift version (incorrectly set to glyph location)
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

    public String unicodeForGlyphRange(int startIndex, int endIndex) {

        if (startIndex >= endIndex) { // cursor position

            return "";

        } else { // range of text is selected

            // get unicode range
            int unicodeStart = renderer.getUnicodeIndex(unicodeText.toString(), startIndex);
            int unicodeEnd = renderer.getUnicodeIndex(unicodeText.toString(), endIndex);

            return unicodeText.subSequence(unicodeStart, unicodeEnd).toString();

        }
    }

    public void insertUnicodeForGlyphRange(int startIndex, int endIndex, String unicodeToInsert) {

        // FIXME: this method assumes no emoji

        if (startIndex >= endIndex) { // caret position

            // if glyph index has changed, need to update unicode index
            updateUnicodeIndex(startIndex);

        }else{ // range of text is selected

            // get unicode range
            int unicodeStart = renderer.getUnicodeIndex(unicodeText.toString(), startIndex);
            int unicodeEnd = renderer.getUnicodeIndex(unicodeText.toString(), endIndex);

            // delete range
            unicodeText.delete(unicodeStart, unicodeEnd);
            unicodeIndexForCursor = unicodeStart;

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
    /// - returns: String array of length 2: {first word from cursor, second word from cursor}
    public String[] unicodeTwoWordsBeforeCursor(int glyphIndex) {

        // if glyph index has changed, need to update unicode index
        updateUnicodeIndex(glyphIndex);

        int startPosition = unicodeIndexForCursor - 1;
        // empty
        if (startPosition < 0) {
            return new String[] {"", ""};
        }
        // not mongolian char or nnbs
        if (!renderer.isMongolian(unicodeText.charAt(startPosition)) &&
                unicodeText.charAt(startPosition) != MongolUnicodeRenderer.Uni.NNBS) {
            return new String[] {"", ""};
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
            return new String[] {wordsArray[0], ""};
        }else if (wordsArray.length > 1) {
            return new String[] {wordsArray[wordsArray.length - 1], wordsArray[wordsArray.length - 2]};
        }else{
            return new String[] {"", ""};
        }
    }

}