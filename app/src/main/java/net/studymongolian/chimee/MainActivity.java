package net.studymongolian.chimee;

import java.util.List;


import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolInputMethodManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

public class MainActivity extends AppCompatActivity {

    protected static final int SHARE_CHOOSER_REQUEST = 0;
    protected static final int WECHAT_REQUEST = 1;
    protected static final int SETTINGS_REQUEST = 2;
    protected static final int FAVORITE_MESSAGE_REQUEST = 3;
    protected static final int HISTORY_REQUEST = 4;
    protected static final int PHOTO_OVERLAY_REQUEST = 6;

    private enum ShareType {
        WeChat,
        Bainu,
        Other
    }

    InputWindow inputWindow;
    HorizontalScrollView hsvScrollView;
    FrameLayout rlTop;
    Dialog overflowMenu;
    Dialog contextMenu;
    static final int INPUT_WINDOW_SIZE_INCREMENT_DP = 50;
    int inputWindowSizeIncrementPx = 0;
    static final int INPUT_WINDOW_MIN_HEIGHT_DP = 150;
    int inputWindowMinHeightPx = 0;
    SharedPreferences settings;
    String lastSentMessage = ""; // don't save two same messages to history
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
        ImeContainer imeContainer = findViewById(R.id.imeContainer);
        inputWindow = findViewById(R.id.resizingScrollView);
        MongolEditText editText = inputWindow.getEditText();
        MongolInputMethodManager mimm = new MongolInputMethodManager();
        mimm.addEditor(editText);
        mimm.setIme(imeContainer);

        settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        // TODO get the right keyboard
        // TODO get a saved draft


        // TODO get input window and set listeners
        //hsvScrollView = findViewById(R.id.horizontalScrollView);
        //initInputWindow();

        showWeChatButton();


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

    private void showWeChatButton() {
        // If WeChat is installed make the button visible
        String weChatMessageTool = "com.tencent.mm.ui.tools.ShareImgUI";
        Intent shareIntent = new Intent();
        shareIntent.setType("image/png");
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info.activityInfo.name.equals(weChatMessageTool)) {
                    // FIXME
                    //FrameLayout weChatButton = (FrameLayout) findViewById(R.id.shareToWeChatFrame);
                    //weChatButton.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // If WeChat is installed make the menu item visible
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FrameLayout topFrame = findViewById(R.id.flTop);
        InputWindow inputWindow = findViewById(R.id.resizingScrollView);

        Log.i("TAG", "onOptionsItemSelected: "
                + topFrame.getHeight() + " "
                + inputWindow.getHeight());

        inputWindow.getEditText().setText(";lkja sd;lkfj a;lskdjf ;alsjdkf akjsdghfjgasdlfiuya sdkhf lakjsdhflkjashd flkha sdlkfjh alksdjfh laksdjhfl kajbsdlkfjbhalskdhfaiouwedhf lbsdfl kabjsdf lkjbhasdljkfhalksjdhf lkah");
        return super.onOptionsItemSelected(item);
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        int initialSize;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            inputWindow.setIsManualScaling(true);
            initialSize = inputWindow.getHeight();
            mScaleFactor = 1.0f;
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
            resizeInputWindow(initialSize);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            inputWindow.setIsManualScaling(false);
            super.onScaleEnd(detector);
        }
    }

    private void resizeInputWindow(int initialSize) {
        int newHeight = (int) (initialSize * mScaleFactor);
        inputWindow.setDesiredHeight(newHeight);
    }

}