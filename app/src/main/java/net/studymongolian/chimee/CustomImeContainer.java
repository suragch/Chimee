package net.studymongolian.chimee;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;

import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class CustomImeContainer extends ImeContainer {

    // these are the indexes of the Mongolian keyboards as defined in XML
    static final int MONGOL_AEIOU_KEYBOARD_INDEX = 0;
    static final int MONGOL_QWERTY_KEYBOARD_INDEX = 1;

    KeyboardEmoji mEmojiKeyboard;

    public CustomImeContainer(Context context) {
        super(context);
    }

    public CustomImeContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImeContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected List<Drawable> getToolButtonItems() {
        List<Drawable> images = new ArrayList<>();
        images.add(getHideKeyboardImage());
        images.add(getKeyboardNavigationImage());
        images.add(getEmojiImage());
        return images;
    }

    @Override
    public void onToolItemClick(int position) {
        switch (position) {
            case 0:
                hideImeContainer();
                break;
            case 1:
                toggleTempKeyboardView(getNavigationView());
                break;
            case 2:
                toggleTempKeyboardView(getEmojiKeyboard());
                break;
            default:
                throw new IllegalArgumentException("Undefined tool item");
        }
    }

    private Drawable getHideKeyboardImage() {
        return ContextCompat.getDrawable(this.getContext(), R.drawable.ic_keyboard_down_32dp);
    }

    private Drawable getKeyboardNavigationImage() {
        return ContextCompat.getDrawable(this.getContext(), R.drawable.ic_navigation_32dp);
    }

    private Drawable getEmojiImage() {
        return ContextCompat.getDrawable(this.getContext(), R.drawable.ic_keyboard_emoji_32dp);
    }

    protected KeyboardEmoji getEmojiKeyboard() {
        if (mEmojiKeyboard != null) return mEmojiKeyboard;
        Keyboard currentKeyboard = getCurrentKeyboard();
        Keyboard.StyleBuilder builder = new Keyboard.StyleBuilder();
        builder.typeface(currentKeyboard.getTypeface())
                .primaryTextSizePx(currentKeyboard.getPrimaryTextSize())
                .primaryTextColor(currentKeyboard.getPrimaryTextColor())
                .keyColor(currentKeyboard.getKeyColor())
                .keyPressedColor(currentKeyboard.getKeyPressedColor())
                .keyBorderColor(currentKeyboard.getBorderColor())
                .keyBorderRadius(currentKeyboard.getBorderRadius())
                .keyBorderWidth(currentKeyboard.getBorderWidth())
                .keySpacing(currentKeyboard.getKeySpacing())
                .popupBackgroundColor(currentKeyboard.getPopupBackgroundColor())
                .popupHighlightColor(currentKeyboard.getPopupHighlightColor())
                .popupTextColor(currentKeyboard.getPopupTextColor())
                .candidatesLocation(currentKeyboard.getCandidatesLocation());
        KeyboardEmoji keyboardEmoji = new KeyboardEmoji(getContext(), builder);
        keyboardEmoji.setOnKeyboardListener(this);
        mEmojiKeyboard = keyboardEmoji;
        return keyboardEmoji;
    }

    @Override
    public void requestNewKeyboard(int index) {
        super.requestNewKeyboard(index);
        saveSelectedKeyboard(index);
    }

    private void saveSelectedKeyboard(int index) {
        if (index != MONGOL_AEIOU_KEYBOARD_INDEX &&
                index != MONGOL_QWERTY_KEYBOARD_INDEX)
            return;

        SharedPreferences settings = getContext().getSharedPreferences(
                SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        if (index == MONGOL_AEIOU_KEYBOARD_INDEX) {
            editor.putString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
                    SettingsActivity.MONGOLIAN_AEIOU_KEYBOARD);
        } else {
            editor.putString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
                    SettingsActivity.MONGOLIAN_QWERTY_KEYBOARD);
        }
        editor.apply();
    }
}
