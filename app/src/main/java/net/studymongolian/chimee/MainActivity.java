package net.studymongolian.chimee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolFont;
import net.studymongolian.mongollibrary.MongolInputMethodManager;
import net.studymongolian.mongollibrary.MongolMenu;
import net.studymongolian.mongollibrary.MongolMenuItem;
import net.studymongolian.mongollibrary.MongolToast;
import net.studymongolian.mongollibrary.MongolTypefaceSpan;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity
        implements ImeContainer.DataSource,
        ImeContainer.OnNonSystemImeListener,
        MongolEditText.ContextMenuCallback,
        ColorChooserDialogFragment.ColorDialogListener,
        FontChooserDialogFragment.FontDialogListener {

    protected static final int SHARE_CHOOSER_REQUEST = 0;
    protected static final int WECHAT_REQUEST = 1;
    protected static final int SETTINGS_REQUEST = 2;
    protected static final int FAVORITE_MESSAGE_REQUEST = 3;
    protected static final int HISTORY_REQUEST = 4;
    protected static final int PHOTO_OVERLAY_REQUEST = 6;

    private static final String TEMP_CACHE_SUBDIR = "images";
    private static final String TEMP_CACHE_FILENAME = "image.png";
    private static final String FILE_PROVIDER_AUTHORITY = "net.studymongolian.chimee.fileprovider";
    private static final int MENU_MARGIN_DP = 4;



    private enum ShareType {
        WeChat,
        Bainu,
        Other
    }

    private enum ImePickerAction {
        NONE,
        CHOOSING,
        CHOSEN
    }

    InputWindow inputWindow;
    CustomImeContainer imeContainer;
    FrameLayout showKeyboardButton;
    String lastSentMessage = "";
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private ImePickerAction mImePickerState = ImePickerAction.NONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        disableRotationForSmallerDevices();
        addGestureDetectorToTopLayout();
        setupKeyboardInput();
        setupKeyboardButton();
        setupInputWindow();
    }

    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    private void disableRotationForSmallerDevices() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void setupKeyboardInput() {
        imeContainer = findViewById(R.id.imeContainer);
        inputWindow = findViewById(R.id.resizingScrollView);
        MongolEditText editText = inputWindow.getEditText();
        MongolInputMethodManager mimm = new MongolInputMethodManager();
        mimm.addEditor(editText);
        mimm.setIme(imeContainer);
        imeContainer.showSystemKeyboardsOption("ᠰᠢᠰᠲ᠋ᠧᠮ");
        imeContainer.setDataSource(this);
        imeContainer.setOnNonSystemImeListener(this);
        getSavedKeyboard();
    }

    private void getSavedKeyboard() {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        String userKeyboard = settings.getString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
                SettingsActivity.MONGOLIAN_KEYBOARD_DEFAULT);
        if (userKeyboard.equals(SettingsActivity.MONGOLIAN_QWERTY_KEYBOARD)) {
            imeContainer.requestNewKeyboard(CustomImeContainer.MONGOL_QWERTY_KEYBOARD_INDEX);
        }
    }

    private void setupKeyboardButton() {
        showKeyboardButton = findViewById(R.id.showKeyboardButton);
        showKeyboardButton.setOnLongClickListener(showKeyboardButtonLongClickListener);
    }

    private void setupInputWindow() {
        MongolEditText editText = inputWindow.getEditText();
        setSavedDraft();
        setSavedColors();
        setSavedFont();
        editText.requestFocus();
        editText.setContextMenuCallbackListener(this);
    }

    private void setSavedDraft() {
        MongolEditText editText = inputWindow.getEditText();
        if (editText.getText().length() == 0) {
            SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
            String savedText = settings.getString(SettingsActivity.DRAFT_KEY, SettingsActivity.DRAFT_DEFAULT);
            editText.setText(savedText);
            int cursorPosition = settings.getInt(SettingsActivity.CURSOR_POSITION_KEY, SettingsActivity.CURSOR_POSITION_DEFAULT);
            if (cursorPosition == 0)
                cursorPosition = savedText.length();
            editText.setSelection(cursorPosition);
        }
    }

    private void setSavedColors() {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        int bgColor = settings.getInt(SettingsActivity.BGCOLOR_KEY,
                SettingsActivity.BGCOLOR_DEFAULT);
        int textColor = settings.getInt(SettingsActivity.TEXTCOLOR_KEY,
                SettingsActivity.TEXTCOLOR_DEFAULT);
        inputWindow.setBackgroundColor(bgColor);
        inputWindow.setTextColor(textColor);
    }

    private void setSavedFont() {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        String font = settings.getString(SettingsActivity.FONT_KEY, SettingsActivity.FONT_DEFAULT);
        Typeface typeface = MongolFont.get(font, getApplicationContext());
        inputWindow.setTypeface(typeface);
    }

    @Override
    public MongolMenu getMongolEditTextContextMenu(MongolEditText met) {
        //final Context context = getContext();
        MongolMenu menu = new MongolMenu(this);
        CharSequence selected = met.getSelectedText();

        // copy, cut
        if (selected.length() > 0) {
            menu.add(new MongolMenuItem(getString(net.studymongolian.mongollibrary.R.string.copy), R.drawable.ic_keyboard_copy_32dp));
            menu.add(new MongolMenuItem(getString(net.studymongolian.mongollibrary.R.string.cut), R.drawable.ic_keyboard_cut_32dp));
        }

        // paste
        menu.add(new MongolMenuItem(getString(net.studymongolian.mongollibrary.R.string.paste), R.drawable.ic_keyboard_paste_32dp));

        // select all
        if (selected.length() < met.getText().length()) {
            menu.add(new MongolMenuItem(getString(net.studymongolian.mongollibrary.R.string.select_all), R.drawable.ic_keyboard_select_all_32dp));
        }

        // color
        menu.add(new MongolMenuItem(getString(R.string.color), R.drawable.ic_color_black_32dp));

        // font
        menu.add(new MongolMenuItem(getString(R.string.font), R.drawable.ic_font_black_32dp));

        menu.setOnMenuItemClickListener(contextMenuItemClickListener);
        return menu;
    }

    MongolMenu.OnMenuItemClickListener contextMenuItemClickListener = new MongolMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MongolMenuItem item) {
            String name = item.getTitle().toString();

            String copy = getString(net.studymongolian.mongollibrary.R.string.copy);
            String cut = getString(net.studymongolian.mongollibrary.R.string.cut);
            String paste = getString(net.studymongolian.mongollibrary.R.string.paste);
            String selectAll = getString(net.studymongolian.mongollibrary.R.string.select_all);
            String color = getString(R.string.color);
            String font = getString(R.string.font);

            if (name.equals(copy)) {
                copySelectedText();
            } else if (name.equals(cut)) {
                cutSelectedText();
            } else if (name.equals(paste)) {
                pasteText();
            } else if (name.equals(selectAll)) {
                inputWindow.getEditText().selectAll();
            } else if (name.equals(color)) {
                openColorChooserDialog();
            } else if (name.equals(font)) {
                openFontChooserDialog();
            } else {
                return false;
            }
            return true;
        }


    };

    private boolean copySelectedText() {
        CharSequence selectedText = inputWindow.getEditText().getSelectedText();
        if (TextUtils.isEmpty(selectedText))
            return false;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null, selectedText);
        if (clipboard == null) return false;
        clipboard.setPrimaryClip(clip);
        return true;
    }
    private void cutSelectedText() {
        boolean copiedSuccessfully = copySelectedText();
        if (copiedSuccessfully) {
            MongolEditText met = inputWindow.getEditText();
            int start = met.getSelectionStart();
            int end = met.getSelectionEnd();
            met.getText().delete(start, end);
        }
    }

    private void pasteText() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return;
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null) return;
        ClipData.Item item = clip.getItemAt(0);
        if (item == null) return;
        CharSequence text = item.getText();
        if (text == null) return;
        MongolEditText met = inputWindow.getEditText();
        int start = met.getSelectionStart();
        int end = met.getSelectionEnd();
        met.getText().replace(start, end, text);
    }

    private void openColorChooserDialog() {
        int bgColor = getInputWindowBackgroundColor();
        int fgColor = getSelectedTextColor();
        DialogFragment dialog = ColorChooserDialogFragment.newInstance(bgColor, fgColor);
        dialog.show(getSupportFragmentManager(), "ColorChooserDialogFragment");

    }

    private int getInputWindowBackgroundColor() {
        int bgColor = Color.WHITE;
        Drawable background = inputWindow.getBackground();
        if (background instanceof ColorDrawable)
            bgColor = ((ColorDrawable) background).getColor();
        return bgColor;
    }

    private int getSelectedTextColor() {
        MongolEditText editText = inputWindow.getEditText();

        if (editText.hasSelection()) {
            Editable text = editText.getText();
            ForegroundColorSpan[] spans = text.getSpans(0, text.length(), ForegroundColorSpan.class);
            if (spans.length > 0)
                return spans[0].getForegroundColor();
        }

        return editText.getTextColor();
    }

    private void openFontChooserDialog() {
        DialogFragment dialog = new FontChooserDialogFragment();
        dialog.show(getSupportFragmentManager(), "FontChooserDialogFragment");
    }

    private void addGestureDetectorToTopLayout() {
        FrameLayout topLayout = findViewById(R.id.flTop);
        mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        topLayout.setOnTouchListener(touchListener);
    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mScaleDetector.onTouchEvent(event);
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        showWeChatButtonIfInstalled(menu);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();

        // save draft unicode text that is in the input window in case user accidentally closes app
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String text = inputWindow.getText().toString();
        int cursorPosition = inputWindow.getEditText().getSelectionStart();
        editor.putString(SettingsActivity.DRAFT_KEY, text);
        editor.putInt(SettingsActivity.CURSOR_POSITION_KEY, cursorPosition);
        editor.apply();
    }

    private void showWeChatButtonIfInstalled(Menu menu) {
        String weChatMessageTool = "com.tencent.mm.ui.tools.ShareImgUI";
        Intent shareIntent = new Intent();
        shareIntent.setType("image/png");
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.name.equals(weChatMessageTool)) {
                    MenuItem item = menu.findItem(R.id.main_action_wechat);
                    item.setVisible(true);
                    break;
                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(mImePickerState == ImePickerAction.CHOOSING) {
            mImePickerState = ImePickerAction.CHOSEN;
        } else if(mImePickerState == ImePickerAction.CHOSEN) {
            showSystemKeyboard();
            mImePickerState = ImePickerAction.NONE;
        }
    }

    // ImeContainer.DataSource methods

    @Override
    public void onRequestWordsStartingWith(String text) {
        new GetWordsStartingWith(this).execute(text);
    }

    @Override
    public void onWordFinished(String word, String previousWord) {
        new AddOrUpdateDictionaryWordsTask(this).execute(word, previousWord);
    }

    @Override
    public void onCandidateClick(int position, String word, String previousWordInEditor) {
        addSpace();
        new RespondToCandidateClick(this).execute(word, previousWordInEditor);
    }

    private void addSpace() {
        InputConnection ic = imeContainer.getInputConnection();
        if (ic == null) return;
        ic.commitText(" ", 1);
    }

    @Override
    public void onCandidateLongClick(int position, String word, String previousWordInEditor) {
        new DeleteWord(this, position).execute(word, previousWordInEditor);
    }

    @Override
    public void onSystemKeyboardRequest() {
        hideInAppKeyboard();
        showSystemKeyboard();
    }

    @Override
    public void onHideKeyboardRequest() {
        hideInAppKeyboard();
    }

    public void onShowKeyboardButtonClick(View view) {
        showInAppKeyboard();
        hideSystemKeyboard();
    }

    private View.OnLongClickListener showKeyboardButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showSystemKeyboardChooser();
            return true;
        }
    };

    private void hideInAppKeyboard() {
        imeContainer.setVisibility(View.GONE);
        showKeyboardButton.setVisibility(View.VISIBLE);
    }

    private void showInAppKeyboard() {
        imeContainer.setVisibility(View.VISIBLE);
        //adjustInputWindowHeightIfNeeded();
        showKeyboardButton.setVisibility(View.INVISIBLE);
    }

//    private void adjustInputWindowHeightIfNeeded() {
//        int inputWindowHeight = inputWindow.getHeight();
//        FrameLayout topLayout = findViewById(R.id.flTop);
//        int topLayoutHeight = topLayout.getHeight();
//        int imeContainerHeight = imeContainer.getHeight();
//        final int availableHeight = topLayoutHeight - imeContainerHeight;
//        //LinearLayout rootLayout = findViewById(R.id.root_layout);
//        //rootLayout.requestLayout();
////        if (inputWindowHeight > availableHeight) {
////            CharSequence text = inputWindow.getText();
////            inputWindow.getEditText().setText("");
////            inputWindow.getEditText().setText(text);
////
//////            inputWindow.setIsManualScaling(true);
//////            inputWindow.setDesiredHeight(availableHeight);
//////            inputWindow.post(new Runnable() {
//////                @Override
//////                public void run() {
//////                    inputWindow.setDesiredHeight(availableHeight);
//////                }
//////            });
////        }
//            //inputWindow.setDesiredHeight(availableHeight);
//    }

    private void showSystemKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im == null) return;
        im.showSoftInput(inputWindow.getEditText(), 0);
    }

    private void hideSystemKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im == null) return;
        im.hideSoftInputFromWindow(inputWindow.getWindowToken(), 0);
    }

    private void showSystemKeyboardChooser() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im == null) return;
        im.showInputMethodPicker();
        mImePickerState = ImePickerAction.CHOOSING;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_action_wechat:
                shareMenuItemClick();
                //shareTo(ShareType.WeChat);
                return true;
            case R.id.main_action_photo:
                //photoActionBarClick();
                return true;
            case R.id.main_action_favorite:
                //favoriteActionBarClick();
                return true;
            case R.id.main_action_overflow:
                overflowMenuItemClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareMenuItemClick() {
        View shareButton = findViewById(R.id.main_action_wechat);
        MongolMenu menu = new MongolMenu(this);
        final MongolMenuItem weChat = new MongolMenuItem(getString(R.string.menu_item_share_wechat), R.drawable.ic_wechat_black_24dp);
        final MongolMenuItem bainu = new MongolMenuItem(getString(R.string.menu_item_share_bainu), R.drawable.ic_bainu_black_24dp);
        final MongolMenuItem other = new MongolMenuItem(getString(R.string.menu_item_share), R.drawable.ic_share_black_24dp);
        menu.add(weChat);
        menu.add(bainu);
        menu.add(other);
        menu.setOnMenuItemClickListener(new MongolMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MongolMenuItem item) {
                if (item == weChat) {
                    shareTo(ShareType.WeChat);
                } else if (item == bainu) {
                    shareTo(ShareType.Bainu);
                } else {
                    shareTo(ShareType.Other);
                }
                return true;
            }
        });

        int[] location = new int[2];
        shareButton.getLocationInWindow(location);
        int gravity = Gravity.NO_GRAVITY;
        int marginPx = convertDpToPx(MENU_MARGIN_DP);
        int xOffset = location[0];
        int yOffset = location[1] + marginPx;

        menu.showAtLocation(shareButton, gravity, xOffset, yOffset);
    }

    public void shareTo(ShareType shareDestination) {

        CharSequence message = inputWindow.getText();

        if (TextUtils.isEmpty(message)) {
            notifyUserOfEmptyMessage();
            return;
        }

        saveMessageToHistory(message);

        inputWindow.setCursorVisible(false);
        Bitmap bitmap = inputWindow.getBitmap(); // getBitmapFromInputWindow();
        inputWindow.setCursorVisible(true);


        boolean successfullySaved = saveBitmapToCacheDir(bitmap);
        if (!successfullySaved) return;
        Uri imageUri = getUriForSavedImage();
        if (imageUri == null) return;
        Intent shareIntent = getShareIntent(imageUri);
        switch (shareDestination) {
            case WeChat:
                shareToWeChat(shareIntent);
                break;
            case Bainu:
                shareToBainu(imageUri);
                break;
            case Other:
                shareToSystemApp(shareIntent);
                break;
        }


        // Show cursor again
        inputWindow.setCursorVisible(true);

    }

    private void overflowMenuItemClick() {
        View overflowMenuButton = findViewById(R.id.main_action_overflow);
        MongolMenu menu = new MongolMenu(this);
        menu.add(new MongolMenuItem(getString(R.string.menu_item_open), R.drawable.ic_folder_open_black_24dp));
        menu.add(new MongolMenuItem(getString(R.string.menu_item_settings), R.drawable.ic_settings_black_24dp));
        menu.setOnMenuItemClickListener(new MongolMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MongolMenuItem item) {
                MongolToast.makeText(getApplicationContext(), item.getTitle(), MongolToast.LENGTH_SHORT).show();
                return true;
            }
        });

        int[] location = new int[2];
        overflowMenuButton.getLocationInWindow(location);
        int gravity = Gravity.TOP | Gravity.RIGHT;
        int marginPx = convertDpToPx(MENU_MARGIN_DP);
        int yOffset = location[1] + marginPx;

        menu.showAtLocation(overflowMenuButton, gravity, marginPx, yOffset);
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

//    private Bitmap getBitmapFromInputWindow() {
//        MongolEditText editText = inputWindow.getEditText();
//        FrameLayout wrapper = findViewById(R.id.inputWindowWrapper);
//        int editTextWidth = editText.getWidth();
//        int inputWidth = wrapper.getWidth();
//        int height = editText.getHeight();
//        Bitmap bitmap;
//        if (editTextWidth < inputWidth) {
//            bitmap = Bitmap.createBitmap(inputWidth, height, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            wrapper.draw(canvas);
//        } else {
//            bitmap = Bitmap.createBitmap(editTextWidth, height, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            editText.draw(canvas);
//        }
//        return bitmap;
//    }

    private boolean saveBitmapToCacheDir(Bitmap bitmap) {
        Context context = getApplicationContext();
        try {
            File cachePath = new File(context.getCacheDir(), TEMP_CACHE_SUBDIR);
            //noinspection ResultOfMethodCallIgnored
            cachePath.mkdirs();
            FileOutputStream stream =
                    new FileOutputStream(cachePath + File.separator + TEMP_CACHE_FILENAME);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Uri getUriForSavedImage() {
        Context context = getApplicationContext();
        File imagePath = new File(context.getCacheDir(), TEMP_CACHE_SUBDIR);
        File newFile = new File(imagePath, TEMP_CACHE_FILENAME);
        return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, newFile);
    }

    private Intent getShareIntent(Uri imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setDataAndType(imageUri, getContentResolver().getType(imageUri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        return shareIntent;
    }

    private void shareToWeChat(Intent shareIntent) {
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareImgUI");
        shareIntent.setComponent(comp);
        startActivityForResult(shareIntent, WECHAT_REQUEST);
    }

    private void shareToBainu(Uri imageUri) {

    }

    private void shareToSystemApp(Intent shareIntent) {
        startActivity(Intent.createChooser(shareIntent, null));
    }

    private void notifyUserOfEmptyMessage() {
        MongolToast.makeText(this,
                getString(R.string.toast_message_empty),
                MongolToast.LENGTH_LONG)
                .show();
    }

    private void saveMessageToHistory(CharSequence message) {
        String messageText = message.toString();
        if (!lastSentMessage.equals(messageText)) {
            new SaveMessageToHistory().execute(messageText);
            lastSentMessage = messageText;
        }
    }


    @Override
    public void onColorDialogPositiveClick(int chosenBackgroundColor,
                                           int chosenForegroundColor) {
        inputWindow.setBackgroundColor(chosenBackgroundColor);
        MongolEditText editText = inputWindow.getEditText();
        Editable text = editText.getText();
        if (editText.hasSelection()) {
            ForegroundColorSpan fgSpan = new ForegroundColorSpan(chosenForegroundColor);
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            text.setSpan(fgSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            // remove fg color spans
            ForegroundColorSpan[] spans = text.getSpans(0, text.length(), ForegroundColorSpan.class);
            for (ForegroundColorSpan span : spans) {
                text.removeSpan(span);
            }
            // set text all one color
            inputWindow.setTextColor(chosenForegroundColor);
            saveColors(chosenBackgroundColor, chosenForegroundColor);
        }
    }

    private void saveColors(int background, int foreground) {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SettingsActivity.BGCOLOR_KEY, background);
        editor.putInt(SettingsActivity.TEXTCOLOR_KEY, foreground);
        editor.apply();
    }

    @Override
    public void onFontDialogPositiveClick(Font chosenFont) {
        MongolEditText editText = inputWindow.getEditText();
        Editable text = editText.getText();
        Typeface typeface = MongolFont.get(chosenFont.getFileLocation(), getApplicationContext());
        if (editText.hasSelection()) {
            MongolTypefaceSpan fontSpan = new MongolTypefaceSpan(typeface);
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            text.setSpan(fontSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            // remove font spans
            MongolTypefaceSpan[] spans = text.getSpans(0, text.length(), MongolTypefaceSpan.class);
            for (MongolTypefaceSpan span : spans) {
                text.removeSpan(span);
            }
            // set font for whole textview
            inputWindow.setTypeface(typeface);
            saveFont(chosenFont.getFileLocation());
        }
    }

    private void saveFont(String font) {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SettingsActivity.FONT_KEY, font);
        editor.apply();
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        int initialHeight;
        float initialTextSize;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            inputWindow.setIsManualScaling(true);
            initialHeight = inputWindow.getHeight();
            initialTextSize = inputWindow.getTextSize();
            mScaleFactor = 1.0f;
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            if (isVerticalScaling(detector)) {
                resizeInputWindow(initialHeight);
            } else {
                resizeText(initialTextSize);
            }
            return true;
        }

        private boolean isVerticalScaling(ScaleGestureDetector detector) {
            float spanX = detector.getCurrentSpanX();
            float spanY = detector.getCurrentSpanY();
            return spanY > spanX;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            inputWindow.setIsManualScaling(false);
            super.onScaleEnd(detector);
        }
    }

    private void resizeText(float initialTextSize) {
        float newSize = initialTextSize * mScaleFactor;
        float sizeSp = newSize / getResources().getDisplayMetrics().scaledDensity;
        Log.i("TAG", "resizeText: " + mScaleFactor);
        inputWindow.setTextSize(sizeSp);
    }

    private void resizeInputWindow(int initialHeight) {
        int newHeight = (int) (initialHeight * mScaleFactor);
        //Log.i("TAG", "resizeInputWindow: " + mScaleFactor);
        inputWindow.setDesiredHeight(newHeight);
    }

    private class SaveMessageToHistory extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String messageText = params[0];

            try {
                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(
                        getApplicationContext());
                dbAdapter.addHistoryMessage(messageText);
            } catch (Exception e) {
                Log.e("app", e.toString());
            }
            return null;
        }
    }


    private static class GetWordsStartingWith extends AsyncTask<String, Integer, List<String>> {

        private WeakReference<MainActivity> activityReference;

        GetWordsStartingWith(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String prefix = params[0];

            Context context = activityReference.get();

            List<String> words = new ArrayList<>();
            Cursor cursor = UserDictionary.Words.queryPrefix(context, prefix);
            if (cursor == null) return words;
            int indexWord = cursor.getColumnIndex(UserDictionary.Words.WORD);
            while (cursor.moveToNext()) {
                words.add(cursor.getString(indexWord));
            }
            cursor.close();
            return words;

        }

        @Override
        protected void onPostExecute(List<String> result) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (result.size() > 0)
                activity.imeContainer.setCandidates(result);
            else
                activity.imeContainer.clearCandidates();
        }
    }

    private static class AddOrUpdateDictionaryWordsTask extends AsyncTask<String, Integer, Void> {

        private WeakReference<MainActivity> activityReference;

        AddOrUpdateDictionaryWordsTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];
            Context context = activityReference.get();
            insertUpdateWord(context, word);

            UserDictionary.Words.addFollowing(context, previousWord, word);
            return null;
        }

    }

    private static void insertUpdateWord(Context context, String word) {
        if (context == null) return;

        int id = UserDictionary.Words.incrementFrequency(context, word);
        if (id < 0) {
            UserDictionary.Words.addWord(context, word);
        }

    }


    private static class RespondToCandidateClick extends AsyncTask<String, Integer, List<String>> {

        private WeakReference<MainActivity> activityReference;

        RespondToCandidateClick(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<String> doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];
            MainActivity activity = activityReference.get();


            UserDictionary.Words.incrementFrequency(activity, word);
            UserDictionary.Words.addFollowing(activity, previousWord, word);
            return UserDictionary.Words.getFollowing(activity, word);
        }

        @Override
        protected void onPostExecute(List<String> followingWords) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (followingWords.size() == 0) {
                activity.imeContainer.clearCandidates();
            } else {
                activity.imeContainer.setCandidates(followingWords);
            }
        }
    }

    private static class DeleteWord extends AsyncTask<String, Integer, Void> {

        private WeakReference<MainActivity> activityReference;
        private int index;

        DeleteWord(MainActivity context, int index) {
            activityReference = new WeakReference<>(context);
            this.index = index;
        }

        @Override
        protected Void doInBackground(String... params) {
            String word = params[0];
            String previousWord = params[1];
            Context context = activityReference.get();
            UserDictionary.Words.deleteWord(context, word);
            UserDictionary.Words.deleteFollowingWord(context, previousWord, word);
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.imeContainer.removeCandidate(index);
        }
    }

}