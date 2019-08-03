package net.studymongolian.chimee;

import android.content.Context;
import android.util.AttributeSet;

import net.studymongolian.mongollibrary.KeyboardAeiou;

public class KeyboardChagaanTolgoi extends KeyboardAeiou {

    public KeyboardChagaanTolgoi(Context context) {
        super(context);
    }

    public KeyboardChagaanTolgoi(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardChagaanTolgoi(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean shouldShowSuffixesInPopup() {
        return false;
    }
}
