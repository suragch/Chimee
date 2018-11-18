package net.studymongolian.chimee;

import android.content.Context;
import android.util.AttributeSet;

import net.studymongolian.mongollibrary.KeyboardQwerty;

public class KeyboardComputer extends KeyboardQwerty {

    public KeyboardComputer(Context context) {
        super(context);
    }

    public KeyboardComputer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardComputer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean shouldShowSuffixesInPopup() {
        return false;
    }
}
