package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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
    // FIXME: addOnChangedListener reports that entire text was changed rather than replacement range

    private MongolTextStorage textStorage = new MongolTextStorage();
    private SizeChangedListener listener;

    // Constructors

    public MongolEditText(Context context) {
        super(context);
        this.listener = null;
    }

    public MongolEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.listener = null;
    }

    public MongolEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.listener = null;
    }

    // Custom listener

    public interface SizeChangedListener {
        public void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    public void addSizeChangedListener(SizeChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("TAG", "onSizeChanged: ");
        if (listener != null) listener.onSizeChanged(w, h, oldw, oldh);
    }



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

    public void insertMongolText(char unicode) {
        insertMongolText(String.valueOf(unicode));
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

    public String secondMongolWordBeforeCursor() {
        return textStorage.unicodeSecondWordBeforeCursor(getSelectionStart());
    }




    // testing

//    private int angle = 90;
//    private final Matrix rotateMatrix = new Matrix();
//    private final Rect viewRectRotated = new Rect();
//    private final RectF tempRectF1 = new RectF();
//    private final RectF tempRectF2 = new RectF();
//    private final float[] viewTouchPoint = new float[2];
//    private final float[] childTouchPoint = new float[2];
//    private boolean angleChanged = true;
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        if (angleChanged) {
//            final RectF layoutRect = tempRectF1;
//            final RectF layoutRectRotated = tempRectF2;
//            layoutRect.set(0, 0, right - left, bottom - top);
//            rotateMatrix.setRotate(angle, layoutRect.centerX(), layoutRect.centerY());
//            rotateMatrix.postScale(-1, 1);
//            rotateMatrix.mapRect(layoutRectRotated, layoutRect);
//            layoutRectRotated.round(viewRectRotated);
//            angleChanged = false;
//        }
//        final View view = getView();
//        if (view != null) {
//            view.layout(viewRectRotated.left, viewRectRotated.top, viewRectRotated.right,
//                    viewRectRotated.bottom);
//        }
//    }

}
