package net.studymongolian.chimee;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * This class allows Mongol Unicode input and font rendering when combined with MongolTextStorage.
 * Everything (characters and indexes) done in this class should be in Unicode. It is the
 * job of MongolTextStorage to do the glyph and index conversions.
 *
 * This class needs to be embedded in a MongolViewGroup to handle the rotation and mirroring.
 *
 */
public class MongolEditText extends EditText {

    // FIXME: many of these methods could give error if text has emoji
    // FIXME: It is inefficient and unnecessary to render everything at every super.setText

    private MongolTextStorage textStorage = new MongolTextStorage();

    // Constructors

    public MongolEditText(Context context) {
        super(context);
    }

    public MongolEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MongolEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    // Overrides
//
//    @Override
//    public Editable getText() {
//        return textStorage.getUnicode();
//    }
//
//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        textStorage.setUnicode(text);
//        super.setText(textStorage.render(), type);
//    }

    // TODO override selection methods

    // Mongol methods

    public String getSelectedText() {
        return textStorage.unicodeForGlyphRange(getSelectionStart(), getSelectionEnd());
    }

    public void insertMongolText(String unicode) {

        // insert or replace selection with unicode
        textStorage.insertUnicodeForGlyphRange(getSelectionStart(), getSelectionEnd(), unicode);

        // render unicode again
        super.setText(textStorage.render());

        // set caret position
        this.setSelection(textStorage.glyphIndexForCursor);
    }

    public void replaceWordAtCursorWith(String replacementString) {

        textStorage.replaceWordAtCursorWith(replacementString, getSelectionStart());

        // render unicode again
        super.setText(textStorage.render());

        // set caret position
        this.setSelection(textStorage.glyphIndexForCursor);
    }

    public void deleteBackward() {

        // delete unicode backward
        textStorage.deleteBackwardsAtGlyphRange(getSelectionStart(), getSelectionEnd());

        // render unicode again
        super.setText(textStorage.render());

        // set caret position
        this.setSelection(textStorage.glyphIndexForCursor);
    }

    public char unicodeCharBeforeCursor() {
        return textStorage.unicodeCharBeforeCursor(getSelectionStart());
    }

    public String mongolWordBeforeCursor() {
        return textStorage.unicodeOneWordBeforeCursor(getSelectionStart());
    }

    /// - returns: String array of length 2: {first word from cursor, second word from cursor}
    public String[] twoMongolWordsBeforeCursor() {
        return textStorage.unicodeTwoWordsBeforeCursor(getSelectionStart());
    }

}
