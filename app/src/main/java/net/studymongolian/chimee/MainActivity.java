package net.studymongolian.chimee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.MongolAlertDialog;
import net.studymongolian.mongollibrary.MongolCode;
import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolFont;
import net.studymongolian.mongollibrary.MongolInputMethodManager;
import net.studymongolian.mongollibrary.MongolMenu;
import net.studymongolian.mongollibrary.MongolMenuItem;
import net.studymongolian.mongollibrary.MongolToast;
import net.studymongolian.mongollibrary.MongolTypefaceSpan;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity
        implements ImeContainer.OnNonSystemImeListener,
        ImeDataSourceHelper.DataSourceHelperListener,
        MongolEditText.ContextMenuCallback,
        ColorChooserDialogFragment.ColorDialogListener,
        FontChooserDialogFragment.FontDialogListener {

    private static final int SHARE_CHOOSER_REQUEST = 0;
    private static final int WECHAT_REQUEST = 1;
    private static final int BAINU_REQUEST = 2;
    private static final int SETTINGS_REQUEST = 3;
    private static final int FAVORITE_MESSAGE_REQUEST = 4;
    private static final int OPEN_REQUEST = 6;
    private static final int SAVE_REQUEST = 7;

    private static final String TEMP_CACHE_SUBDIR = "images";
    private static final String TEMP_CACHE_FILENAME = "image.png";
    private static final String FILE_PROVIDER_AUTHORITY = "net.studymongolian.chimee.fileprovider";
    private static final int MENU_MARGIN_DP = 4;
    private static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    private static final String BAINU_PACKAGE_NAME = "com.zuga.im";
    private static final String BAINU_DOWNLOAD_SITE = "http://www.zuga-tech.net";


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
        setInputWindowContent();
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
        imeContainer.showSystemKeyboardsOption(getString(R.string.keyboard_show_system_keyboards));

        imeContainer.setDataSource(new ImeDataSourceHelper(this));
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
        setSavedColors();
        setSavedFont();
        editText.requestFocus();
        editText.setContextMenuCallbackListener(this);
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

    private void setInputWindowContent() {
        setSavedDraft();
//        String textFromReader = getPossibleReaderActivityIntentText();
//        if (textFromReader == null) {
//        }else {
//            MongolEditText editText = inputWindow.getEditText();
//            editText.setText(textFromReader);
//        }
    }

//    private String getPossibleReaderActivityIntentText() {
//        Bundle extras = getIntent().getExtras();
//        if (extras == null) return null;
//        return extras.getString(ReaderActivity.READER_TEXT_KEY);
//    }

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
            MongolEditText editText = inputWindow.getEditText();

            if (name.equals(copy)) {
                editText.copySelectedText();
            } else if (name.equals(cut)) {
                editText.cutSelectedText();
            } else if (name.equals(paste)) {
                editText.pasteText();
            } else if (name.equals(selectAll)) {
                editText.selectAll();
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

//    private boolean copySelectedText() {
//        CharSequence selectedText = inputWindow.getEditText().getSelectedText();
//        if (TextUtils.isEmpty(selectedText))
//            return false;
//        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText(null, selectedText);
//        if (clipboard == null) return false;
//        clipboard.setPrimaryClip(clip);
//        return true;
//    }
//    private void cutSelectedText() {
//        boolean copiedSuccessfully = copySelectedText();
//        if (copiedSuccessfully) {
//            MongolEditText met = inputWindow.getEditText();
//            int start = met.getSelectionStart();
//            int end = met.getSelectionEnd();
//            met.getText().delete(start, end);
//        }
//    }
//
//    private void pasteText() {
//        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        if (clipboard == null) return;
//        ClipData clip = clipboard.getPrimaryClip();
//        if (clip == null) return;
//        ClipData.Item item = clip.getItemAt(0);
//        if (item == null) return;
//        CharSequence text = item.getText();
//        if (text == null) return;
//        MongolEditText met = inputWindow.getEditText();
//        int start = met.getSelectionStart();
//        int end = met.getSelectionEnd();
//        met.getText().replace(start, end, text);
//    }

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mImePickerState == ImePickerAction.CHOOSING) {
            mImePickerState = ImePickerAction.CHOSEN;
        } else if (mImePickerState == ImePickerAction.CHOSEN) {
            showSystemKeyboard();
            mImePickerState = ImePickerAction.NONE;
        }
    }

    // ImeDataSourceHelper.DataSourceHelperListener methods

    @Override
    public CustomImeContainer getImeContainer() {
        return imeContainer;
    }

    @Override
    public Context getContext() {
        return this;
    }

//    // ImeContainer.DataSource methods
//
//    @Override
//    public void onRequestWordsStartingWith(String text) {
//        if (text.startsWith(String.valueOf(MongolCode.Uni.NNBS))) {
//            new GetSuffixesStartingWith(this).execute(text);
//        } else {
//            new GetWordsStartingWith(this).execute(text);
//        }
//    }
//
//    @Override
//    public void onWordFinished(String word, String previousWord) {
//        new AddOrUpdateDictionaryWordsTask(this).execute(word, previousWord);
//    }
//
//    @Override
//    public void onCandidateClick(int position, String word, String previousWordInEditor) {
//        addSpace();
//        new RespondToCandidateClick(this).execute(word, previousWordInEditor);
//    }
//
//    private void addSpace() {
//        InputConnection ic = imeContainer.getInputConnection();
//        if (ic == null) return;
//        ic.commitText(" ", 1);
//    }
//
//    @Override
//    public void onCandidateLongClick(int position, String word, String previousWordInEditor) {
//        new DeleteWord(this, position).execute(word, previousWordInEditor);
//    }

    // ImeContainer.OnNonSystemImeListener methods

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
            case R.id.main_action_share:
                shareActionBarItemClick();
                return true;
            case R.id.main_action_photo:
                //photoActionBarItemClick();
                return true;
            case R.id.main_action_favorite:
                favoriteActionBarItemClick();
                return true;
            case R.id.main_action_overflow:
                overflowMenuItemClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareActionBarItemClick() {

        // check WeChat and Bainu
        PackageManager pm = getApplicationContext().getPackageManager();
        boolean shouldShowWeChat = isPackageInstalled(WECHAT_PACKAGE_NAME);
        boolean shouldShowBainu = isPackageInstalled(BAINU_PACKAGE_NAME)
                || shouldShowBainuIcon();
        if (!shouldShowWeChat && !shouldShowBainu) {
            shareTo(ShareType.Other);
            return;
        }

        // create menu
        MongolMenu menu = new MongolMenu(this);
        final MongolMenuItem weChat = new MongolMenuItem(getString(R.string.menu_item_share_wechat), R.drawable.ic_wechat_black_24dp);
        final MongolMenuItem bainu = new MongolMenuItem(getString(R.string.menu_item_share_bainu), R.drawable.ic_bainu_black_24dp);
        final MongolMenuItem other = new MongolMenuItem(getString(R.string.menu_item_share_other), R.drawable.ic_more_vert_black_24dp);
        if (shouldShowWeChat)
            menu.add(weChat);
        if (shouldShowBainu)
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

        // show menu
        int[] location = new int[2];
        View shareButton = findViewById(R.id.main_action_share);
        shareButton.getLocationInWindow(location);
        int gravity = Gravity.NO_GRAVITY;
        int marginPx = convertDpToPx(MENU_MARGIN_DP);
        int xOffset = location[0];
        int yOffset = location[1] + marginPx;
        menu.showAtLocation(shareButton, gravity, xOffset, yOffset);
    }

    private boolean shouldShowBainuIcon() {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        return settings.getBoolean(SettingsActivity.SHOW_BAINU_BUTTON_KEY, true);
    }

    private boolean isPackageInstalled(String packageName) {
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            return pm.getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void shareTo(ShareType shareDestination) {

        CharSequence message = inputWindow.getText();

        if (TextUtils.isEmpty(message)) {
            notifyUserOfEmptyMessage();
            return;
        }

        saveMessageToHistory(message);

        switch (shareDestination) {
            case WeChat:
                shareToWeChat();
                break;
            case Bainu:
                shareToBainu();
                break;
            case Other:
                shareToSystemApp();
                break;
        }
    }


    private void favoriteActionBarItemClick() {

        Intent intent = new Intent(this, FavoriteActivity.class);
        String text = inputWindow.getText().toString();
        intent.putExtra(FavoriteActivity.CURRENT_MESSAGE_KEY, text);
        startActivityForResult(intent, FAVORITE_MESSAGE_REQUEST);


        // catch empty string
//        String text = inputWindow.getText().toString().trim();
//        if (TextUtils.isEmpty(text)) {
//            String message = getString(R.string.dialog_message_emptyfavorite);
//            showAlertWithNoButtons(message);
//            return;
//        }
//
//        new AddMessageToFavoriteDb(this).execute(text);
    }

    private void showAlertWithNoButtons(String message) {
        MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(this);
        builder.setMessage(message);
        MongolAlertDialog dialog = builder.create();
        dialog.show();
    }

    private void overflowMenuItemClick() {
        View overflowMenuButton = findViewById(R.id.main_action_overflow);
        MongolMenu menu = new MongolMenu(this);
        final MongolMenuItem open = new MongolMenuItem(getString(R.string.menu_item_open), R.drawable.ic_folder_open_black_24dp);
        final MongolMenuItem save = new MongolMenuItem(getString(R.string.menu_item_save), R.drawable.ic_save_black_24dp);
        final MongolMenuItem settings = new MongolMenuItem(getString(R.string.menu_item_settings), R.drawable.ic_settings_black_24dp);
        if (inputWindow.hasUnsavedContent()) {
            menu.add(save);
        } else {
            menu.add(open);
        }
        menu.add(settings);
        menu.setOnMenuItemClickListener(new MongolMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MongolMenuItem item) {
                if (item == open) {
                    onOpenMenuItemClick();
                } else if (item == save) {
                    onSaveMenuItemClick();
                } else if (item == settings) {
                    onSettingsMenuItemClick();
                }
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

    private void onOpenMenuItemClick() {
        Intent intent = new Intent(this, OpenActivity.class);
        startActivityForResult(intent, OPEN_REQUEST);
    }

    private void onSaveMenuItemClick() {
        Intent intent = new Intent(this, SaveActivity.class);
        intent.putExtra(SaveActivity.TEXT_KEY, inputWindow.getText().toString());
        startActivityForResult(intent, SAVE_REQUEST);
    }

    private void onSettingsMenuItemClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_REQUEST);
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }


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

    private Intent getShareImageIntent() {
        Bitmap bitmap = getInputWindowBitmap();
        boolean successfullySaved = saveBitmapToCacheDir(bitmap);
        if (!successfullySaved) return null;
        Uri imageUri = getUriForSavedImage();
        if (imageUri == null) return null;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setDataAndType(imageUri, getContentResolver().getType(imageUri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        return shareIntent;
    }

    private Bitmap getInputWindowBitmap() {
        inputWindow.setCursorVisible(false);
        Bitmap bitmap = inputWindow.getBitmap();
        inputWindow.setCursorVisible(true);
        return bitmap;
    }

    private void shareToWeChat() {
        Intent shareIntent = getShareImageIntent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareImgUI");
        shareIntent.setComponent(comp);
        startActivityForResult(shareIntent, WECHAT_REQUEST);
    }

    private void shareToBainu() {

        boolean isBainuInstalled = isPackageInstalled(BAINU_PACKAGE_NAME);
        if (!isBainuInstalled) {
            askIfUserWantsToDownloadBainu();
            return;
        }

        String text = inputWindow.getText().toString();
        String menksoftCode = MongolCode.INSTANCE.unicodeToMenksoft(text);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, menksoftCode);
        shareIntent.setType("text/plain");
        ComponentName comp = new ComponentName("com.zuga.im",
                "com.zuga.im.bainuSdk.BNEntryActivity");
        shareIntent.setComponent(comp);
        startActivityForResult(shareIntent, BAINU_REQUEST);
    }

    private void askIfUserWantsToDownloadBainu() {
        MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(this);
        builder.setMessage(getString(R.string.download_bainu_alert_message));

        builder.setPositiveButton(getString(R.string.download_bainu_alert_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openBainuDownloadPage();
            }
        });
        builder.setNegativeButton(getString(R.string.download_bainu_alert_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveBainuChoiceToPreferences(false);
            }
        });

        MongolAlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openBainuDownloadPage() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BAINU_DOWNLOAD_SITE));
        startActivity(browserIntent);
    }

    private void saveBainuChoiceToPreferences(boolean showBainuButton) {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SettingsActivity.SHOW_BAINU_BUTTON_KEY, showBainuButton);
        editor.apply();
    }

    private void shareToSystemApp() {
        Intent shareIntent = getShareImageIntent();
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
            if (start > end) {
                int swap = start;
                start = end;
                end = swap;
            }
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
            if (start > end) {
                int swap = start;
                start = end;
                end = swap;
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SHARE_CHOOSER_REQUEST:
                onShareChooserResult(resultCode, data);
                break;
            case WECHAT_REQUEST:
                onWeChatResult(resultCode, data);
                break;
            case BAINU_REQUEST:
                onBainuResult(resultCode, data);
                break;
            case SETTINGS_REQUEST:
                onSettingsResult(resultCode, data);
                break;
            case FAVORITE_MESSAGE_REQUEST:
                onFavoriteActivityResult(resultCode, data);
                break;
            case OPEN_REQUEST:
                onOpenFileResult(resultCode, data);
                break;
            case SAVE_REQUEST:
                onSaveFileResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onShareChooserResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // TODO this never gets called. Make a custom chooser
        }
        inputWindow.getEditText().setText("");
    }

    private void onWeChatResult(int resultCode, Intent data) {
        inputWindow.getEditText().setText("");
    }

    private void onBainuResult(int resultCode, Intent data) {

    }

    private void onSettingsResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        String message = data.getStringExtra(HistoryActivity.RESULT_STRING_KEY);
        insertMessageIntoInputWindow(message);
    }

    private void insertMessageIntoInputWindow(String message) {
        if (TextUtils.isEmpty(message)) return;
        MongolEditText editText = inputWindow.getEditText();
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        editText.getText().replace(start, end, message);
    }

    private void onFavoriteActivityResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Bundle extras = data.getExtras();
        if (extras == null) return;
        String message = extras.getString(FavoriteActivity.RESULT_STRING_KEY);
        insertMessageIntoInputWindow(message);
    }

    private void onHistoryResult(int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (data.hasExtra("resultString")) {
                String result = data.getExtras().getString("resultString");

            }
        }
    }

    private void onOpenFileResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Bundle extras = data.getExtras();
        if (extras == null) return;
        String fileName = extras.getString(OpenActivity.FILE_NAME_KEY);
        String fileText = extras.getString(OpenActivity.FILE_TEXT_KEY);
        if (TextUtils.isEmpty(fileName) || fileText == null) return;
        MongolEditText editText = inputWindow.getEditText();
        editText.setText(fileText);
        editText.setSelection(0);
        inputWindow.recordSavedContent();
    }

    private void onSaveFileResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            inputWindow.recordSavedContent();
        }
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


}