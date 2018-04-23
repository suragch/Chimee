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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

	ResizingScrollView inputWindow;
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// setup toolbar
		Toolbar myToolbar = findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		// disable rotation for smaller devices
		if(getResources().getBoolean(R.bool.portrait_only)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

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
}