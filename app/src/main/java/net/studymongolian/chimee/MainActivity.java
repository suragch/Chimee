package net.studymongolian.chimee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolInputMethodManager;
import net.studymongolian.mongollibrary.MongolMenu;
import net.studymongolian.mongollibrary.MongolMenuItem;
import net.studymongolian.mongollibrary.MongolToast;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

public class MainActivity extends AppCompatActivity implements ImeContainer.DataSource {

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

    InputWindow inputWindow;
    ImeContainer imeContainer;
    HorizontalScrollView hsvScrollView;
    FrameLayout rlTop;
    Dialog overflowMenu;
    Dialog contextMenu;
    static final int INPUT_WINDOW_SIZE_INCREMENT_DP = 50;
    int inputWindowSizeIncrementPx = 0;
    static final int INPUT_WINDOW_MIN_HEIGHT_DP = 150;
    int inputWindowMinHeightPx = 0;
    SharedPreferences settings;
    String lastSentMessage = "";
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setup toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // disable rotation for smaller devices
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // add gesture detector to top layout
        addGestureDetectorToTopLayout();

        // set up the keyboard
        imeContainer = findViewById(R.id.imeContainer);
        inputWindow = findViewById(R.id.resizingScrollView);
        MongolEditText editText = inputWindow.getEditText();
        MongolInputMethodManager mimm = new MongolInputMethodManager();
        mimm.addEditor(editText);
        mimm.setIme(imeContainer);
        imeContainer.setDataSource(this);

        settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        // TODO get the right keyboard
        // TODO get a saved draft


        // TODO get input window and set listeners
        //hsvScrollView = findViewById(R.id.horizontalScrollView);
        //initInputWindow();


        // Set up density independent pixel constants
        final float scale = getResources().getDisplayMetrics().density;
        inputWindowSizeIncrementPx = (int) (INPUT_WINDOW_SIZE_INCREMENT_DP * scale + 0.5f);
        inputWindowMinHeightPx = (int) (INPUT_WINDOW_MIN_HEIGHT_DP * scale + 0.5f);


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
                //MongolToast.makeText(getApplicationContext(), item.getTitle(), MongolToast.LENGTH_SHORT).show();
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


        if (shareDestination == ShareType.WeChat) {
            // String weChatMessageTool =
            // "com.tencent.mm.ui.tools.shareimgui";
            ComponentName comp = new ComponentName("com.tencent.mm",
                    "com.tencent.mm.ui.tools.ShareImgUI");
            shareIntent.setComponent(comp);
            startActivityForResult(shareIntent, WECHAT_REQUEST);
        } else {
            //startActivity(Intent.createChooser(shareIntent, "Choose an app")); // FIXME: don't use English here
            startActivity(shareIntent);
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

    private Bitmap getBitmapFromInputWindow() {
        MongolEditText editText = inputWindow.getEditText();
        FrameLayout wrapper = findViewById(R.id.inputWindowWrapper);
        int editTextWidth = editText.getWidth();
        int inputWidth = wrapper.getWidth();
        int height = editText.getHeight();
        Bitmap bitmap;
        if (editTextWidth < inputWidth) {
            bitmap = Bitmap.createBitmap(inputWidth, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            wrapper.draw(canvas);
        } else {
            bitmap = Bitmap.createBitmap(editTextWidth, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            editText.draw(canvas);
        }
        return bitmap;
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
        Log.i("TAG", "resizeInputWindow: " + mScaleFactor);
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
            if (activity == null) return;

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
            if (activity == null) return;
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
            if (activity == null) return;
            activity.imeContainer.removeCandidate(index);
        }
    }

}