package net.studymongolian.chimee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import net.studymongolian.chimee.MongolTextView.CursorTouchLocationListener;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  implements KeyboardController.OnKeyboardControllerListener {

	//public class MainActivity extends AppCompatActivity implements MongolAeiouKeyboard.Communicator,
			//MongolQwertyKeyboard.Communicator, EnglishKeyboard.OnKeyTouchListener {

	protected static final char SPACE = ' ';
	protected static final char NEW_LINE = '\n';
	protected static final char MONGOLIAN_COMMA = '\u1802';
	protected static final char MONGOLIAN_FULL_STOP = '\u1803';
	protected static final char NULL_CHAR = '\u0000';
	protected static final char CURSOR_HOLDER = MongolUnicodeRenderer.CURSOR_HOLDER; // |
	protected static final char BACKSPACE = '\u232b';
	protected static final char SWITCH_TO_ENGLISH = 'α'; // arbitrary symbol
	protected static final char SWITCH_TO_MONGOLIAN = 'β'; // arbitrary symbol
	protected static final char ZWJ = '\u200d';// ZeroWidthJoiner
	protected static final char NNBS = '\u202f';// NarrowNonBreakingSpace
	protected static final char MONGOLIAN_FVS1 = '\u180b';// FreeVariationSelector
	protected static final char MONGOLIAN_FVS2 = '\u180c';
	protected static final char MONGOLIAN_FVS3 = '\u180d';
	protected static final char MONGOLIAN_MVS = '\u180e';// VOWEL SEPARATOR

	protected static final int SHARE_CHOOSER_REQUEST = 0;
	protected static final int WECHAT_REQUEST = 1;
	protected static final int SETTINGS_REQUEST = 2;
	protected static final int FAVORITE_MESSAGE_REQUEST = 3;
	protected static final int HISTORY_REQUEST = 4;
	protected static final int AE_REQUEST = 5;
    protected static final int PHOTO_OVERLAY_REQUEST = 6;

	// Fragment tags
	public static final String MONGOL_AEIOU_TAG = "mongol_aeiou";
	public static final String MONGOL_QWERTY_TAG = "mongol_qwerty";
	public static final String ENGLISH_FRAGMENT_TAG = "english";
	public static final String CONTEXT_MENU_TAG = "context_menu";

	private enum ShareType {
		WeChat,
		Bainu,
		Other
	}


	MongolEditText inputWindow;

	FloatingActionButton fabEditMenu;
	//MongolViewGroup rlMessage;
	//HorizontalScrollView hsvScrollView;
	MongolViewGroup mvgMessageOutline;
	CoordinatorLayout rlTop;
	Dialog overflowMenu;
	Dialog fabMenu;
	static final int INPUT_WINDOW_SIZE_INCREMENT_DP = 50;
	int inputWindowSizeIncrementPx = 0; // size in pixels
	static final int INPUT_WINDOW_MIN_HEIGHT_DP = 150;
	int inputWindowMinHeightPx = 0; // size in pixels
	//int renderedTextOldLength = 0;
	//int oldInputWindowHeight = -1;
	//StringBuilder unicodeText = new StringBuilder();
	//MongolUnicodeRenderer renderer = MongolUnicodeRenderer.INSTANCE;
	//int cursorPosition = 0;
	//Keyboard currentKeyboard;
	//Keyboard userMongolKeyboard;
	SharedPreferences settings;
	//boolean swapMongolKeyboards = false;
	//MongolAeiouKeyboard mongolAeiouKeyboard;
	//MongolQwertyKeyboard mongolQwertyKeyboard;
	//EnglishKeyboard englishKeyboard;
	//InputWindowContextMenu contextMenu;
	//FragmentManager fragmentManager;
	String lastSentMessage = ""; // don't save two same messages to history
	boolean fabIsShowing = true;



	// Keyboard Controller Listener methods

	@Override
	public void keyWasTapped(char character) {

//		if (character==MongolUnicodeRenderer.Uni.LHA) {
//
//			ViewGroup.LayoutParams layoutParams = inputWindow.getLayoutParams();
//			layoutParams.height = 1200;
//			inputWindow.setLayoutParams(layoutParams);
//			inputWindow.invalidate();
//
//			return;
//		}



		// add a space after punctuation
		if (character == MONGOLIAN_COMMA || character == MONGOLIAN_FULL_STOP || character == '?'
				|| character == '!') {
			// Place punctuation automatically if entered after a space
			if (inputWindow.unicodeCharBeforeCursor() == SPACE) {
				inputWindow.deleteBackward();
			}
			inputWindow.insertMongolText("" + character + SPACE);
		} else {
			inputWindow.insertMongolText(character);
		}

		// hide fab if necessary
		if (fabIsShowing && viewsIntersect(mvgMessageOutline, fabEditMenu)) {
			Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide);
			fabEditMenu.startAnimation(animation);
			fabIsShowing = false;
		}
	}

	@Override
	public void keyBackspace() {
		inputWindow.deleteBackward();

		// show fab if necessary
		if (!fabIsShowing && !viewsIntersect(mvgMessageOutline, fabEditMenu)) {
			Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show);
			fabEditMenu.startAnimation(animation);
			fabIsShowing = true;
		}
	}

	@Override
	public char getCharBeforeCursor() {
		return inputWindow.unicodeCharBeforeCursor();
	}

	@Override
	public String oneMongolWordBeforeCursor() {
		return inputWindow.mongolWordBeforeCursor();
	}

	@Override
	public String secondMongolWordsBeforeCursor() {
		return inputWindow.secondMongolWordBeforeCursor();
	}

	@Override
	public void replaceCurrentWordWith(String replacementWord) {
		inputWindow.replaceWordAtCursorWith(replacementWord);
	}





	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        // setup toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

		// disable rotation for smaller devices
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


		// prevent system keyboard from appearing
		inputWindow = (MongolEditText) findViewById(R.id.etInputWindow);
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			inputWindow.setRawInputType(InputType.TYPE_CLASS_TEXT);
			inputWindow.setTextIsSelectable(true);
		} else {
			inputWindow.setRawInputType(InputType.TYPE_NULL);
			inputWindow.setFocusable(true);
		}

		/////////////////// Get the settings //////////////////////////
		//initSettings();
		settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
		// TODO get the right keyboard
//		String userKeyboard = settings.getString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
//				SettingsActivity.MONGOLIAN_KEYBOARD_DEFAULT);
//		if (userKeyboard.equals(SettingsActivity.MONGOLIAN_AEIOU_KEYBOARD)) {
//			userMongolKeyboard = Keyboard.MONGOLIAN_AEIOU;
//		} else {
//			userMongolKeyboard = Keyboard.MONGOLIAN_QWERTY;
//		}
//		currentKeyboard = userMongolKeyboard;
		// get a saved draft
//		if (unicodeText.length() == 0) {
//			unicodeText.append(settings.getString(SettingsActivity.DRAFT_KEY, SettingsActivity.DRAFT_DEFAULT));
//			// TODO cursorPosition = settings.getInt(SettingsActivity.CURSOR_POSITION_KEY, SettingsActivity.CURSOR_POSITION_DEFAULT);
//		}


		// TODO get input window and set listeners
		initInputWindow();


//		// If WeChat is installed make the button visible
//		String weChatMessageTool = "com.tencent.mm.ui.tools.ShareImgUI";
//		Intent shareIntent = new Intent();
//		shareIntent.setType("image/png");
//		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
//		if (!resInfo.isEmpty()) {
//			for (ResolveInfo info : resInfo) {
//				if (info.activityInfo.name.equals(weChatMessageTool)) {
//
//					FrameLayout weChatButton = (FrameLayout) findViewById(R.id.shareToWeChatFrame);
//					weChatButton.setVisibility(View.VISIBLE);
//					break;
//				}
//			}
//		}

		///////////// Set up fragments ///////////////////

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.keyboardContainer, new KeyboardController());
		ft.commit();



		mvgMessageOutline = (MongolViewGroup) findViewById(R.id.mvgMessageOutline);
		//rlMessage.setOnLongClickListener(longClickHandler);
		//hsvScrollView = (HorizontalScrollView) findViewById(R.id.hsvMessageLimit);
		//rlLimit = (RelativeLayout) findViewById(R.id.rlMessageLimit);
		rlTop = (CoordinatorLayout) findViewById(R.id.rlTop);

		// set colors
		//rlMessage.setBackgroundColor(settings.getInt(SettingsActivity.BGCOLOR_KEY,
		//		SettingsActivity.BGCOLOR_DEFAULT));
		//int textColor = settings.getInt(SettingsActivity.TEXTCOLOR_KEY,
		//		SettingsActivity.TEXTCOLOR_DEFAULT);
		//inputWindow.setTextColor(textColor);
		//inputWindow.setCursorColor(textColor);

		// set font
		String font = settings.getString(SettingsActivity.FONT_KEY, SettingsActivity.FONT_DEFAULT);
		Typeface tf = FontCache.get(font, getApplicationContext());
		if (tf != null) {
			inputWindow.setTypeface(tf);
		}

		// floating action button
		fabEditMenu = (FloatingActionButton) findViewById(R.id.fabEditInputWindow);
		fabEditMenu.setOnClickListener(onFabClick);

		// Set up density independent pixel constants
		final float scale = getResources().getDisplayMetrics().density;
		inputWindowSizeIncrementPx = (int) (INPUT_WINDOW_SIZE_INCREMENT_DP * scale + 0.5f);
		inputWindowMinHeightPx = (int) (INPUT_WINDOW_MIN_HEIGHT_DP * scale + 0.5f);


		/*rlMessage.post(new Runnable() {
			public void run() {
				inputWindowSizeIncrementPx = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, INPUT_WINDOW_SIZE_INCREMENT_DP, getResources()
								.getDisplayMetrics());
				inputWindowMinHeightPx = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, INPUT_WINDOW_MIN_HEIGHT_DP, getResources()
								.getDisplayMetrics());
			}
		});*/

		/*if (savedInstanceState != null) {
			unicodeText.append(savedInstanceState.getString("unicodeSave"));
			cursorPosition = savedInstanceState.getInt("positionSave");
		}*/

		// Make the cursor show and resize input window
		//rlMessage.post(new Runnable() {
		//	public void run() {
		//		updateDisplay();
		//	}
		//});

	}

	private boolean viewsIntersect(View view1, View view2) {

		Rect rect1 = new Rect(view1.getLeft(), view1.getTop(), view1.getRight(), view1.getBottom());
		Rect rect2 = new Rect(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom());

		return Rect.intersects(rect1, rect2);

//		int[] location1 = new int[2];
//		int[] location2 = new int[2];
//
//		view1.getLocationOnScreen(location1);
//		view2.getLocationOnScreen(location2);
//
//		Rect rect1 = new Rect(location1[0], location1[1],
//				location1[0] + view1.getMeasuredWidth(),
//				location1[1] + view1.getMeasuredHeight());
//		Rect rect2 = new Rect(location2[0], location2[1],
//				location2[0] + view2.getMeasuredWidth(),
//				location2[1] + view2.getMeasuredHeight());
//
//		return rect1.intersect(rect2);
	}

	private void initInputWindow() {

		inputWindow.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count)  {
				CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mvgMessageOutline.getLayoutParams();
				Rect size = getBestSizeForInputWindow();
				params.height = size.height();
				if (size.width() < rlTop.getWidth()) {
					params.width = size.width();
				} else {
					params.width = rlTop.getWidth();
				}
				mvgMessageOutline.setLayoutParams(params);
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});
	}

	private Rect getBestSizeForInputWindow() {

		// Since the input window EditText is rotated, the height and width need to be swapped

		int currentHeight = mvgMessageOutline.getHeight();
		if (currentHeight < inputWindowMinHeightPx) {
			currentHeight = inputWindowMinHeightPx;
		}

		inputWindow.measure(View.MeasureSpec.makeMeasureSpec(currentHeight, View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		int h = currentHeight;
		int w = inputWindow.getMeasuredHeight();

		// TODO don't do this if there is no change

		int maxH = rlTop.getHeight();
		final int minH = inputWindowMinHeightPx;

		if (w < minH / 2) {
			w = minH / 2;
		}
		if (maxH < minH) {
			maxH = minH;
		}


		if (h < w && h < maxH) { // need to increase h

			while (h < maxH) {
				h += inputWindowSizeIncrementPx;
				if (h >= maxH) {
					h = maxH;
					inputWindow.measure(View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY),
							View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
					w = inputWindow.getMeasuredHeight();

					break;
				}
				inputWindow.measure(View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY),
						View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
				w = inputWindow.getMeasuredHeight();
				if (h >= w) {
					break;
				}
			}

		} else if (h > 2 * w && h > minH) { // need to decrease h

			while (h > minH) {
				h -= inputWindowSizeIncrementPx;

				if (h <= minH) {
					h = minH;
					inputWindow.measure(View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY),
							View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
					w = inputWindow.getMeasuredHeight();
					break;
				}

				inputWindow.measure(View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY),
						View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
				w = inputWindow.getMeasuredHeight();
				if (h <= 2 * w) {
					break;
				}
			}
		}

		if (w < minH / 2) {
			w = minH / 2;
		}
		return new Rect(0, 0, w, h);
	}


//	private void initSettings() {
//
//		settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
//
//		// get the right keyboard
//		String userKeyboard = settings.getString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
//				SettingsActivity.MONGOLIAN_KEYBOARD_DEFAULT);
//		if (userKeyboard.equals(SettingsActivity.MONGOLIAN_AEIOU_KEYBOARD)) {
//			userMongolKeyboard = Keyboard.MONGOLIAN_AEIOU;
//		} else {
//			userMongolKeyboard = Keyboard.MONGOLIAN_QWERTY;
//		}
//		currentKeyboard = userMongolKeyboard;
//
//		// get a saved draft
//		if (unicodeText.length() == 0) {
//			unicodeText.append(settings.getString(SettingsActivity.DRAFT_KEY, SettingsActivity.DRAFT_DEFAULT));
//			cursorPosition = settings.getInt(SettingsActivity.CURSOR_POSITION_KEY, SettingsActivity.CURSOR_POSITION_DEFAULT);
//		}
//
//	}

	@Override
	public void onPause() {
		super.onPause();

		// hide the menus if showing
		hideMenu();

	}

	@Override
	public void onStop() {
		super.onStop();

		// TODO save draft unicode text that is in the input window in case user accidentally closes app
//		settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
//		SharedPreferences.Editor editor = settings.edit();
//		editor.putString(SettingsActivity.DRAFT_KEY, unicodeText.toString());
//		// TODO editor.putInt(SettingsActivity.CURSOR_POSITION_KEY, cursorPosition);
//		editor.commit();
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
        switch (item.getItemId()) {
            case R.id.main_action_wechat:
				//shareToWeChat();
				shareTo(ShareType.WeChat);
                return true;
			case R.id.main_action_photo:
				photoActionBarClick();
				return true;
            case R.id.main_action_favorite:
				favoriteActionBarClick();
                return true;
            case R.id.main_action_overflow:
				overflowActionBarClick();
				return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	public View.OnClickListener onOverflowMenuItemSelected = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.mainMenuItemShareFrame:
					shareTo(ShareType.Other);
					break;
				case R.id.mainMenuItemOpenSaveFrame:
					if (TextUtils.isEmpty(inputWindow.getText())) {
						menuOpenClick();
					} else {
						menuSaveClick();
					}
					break;
				case R.id.mainMenuItemSettingsFrame:
					menuSettingsClick();
					break;
				default:
					break;
			}
		}
	};


	public View.OnClickListener onFabMenuItemSelected = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.fabMenuItemCopyFrame:
					fabMenuCopy();
					break;
				case R.id.fabMenuItemPasteFrame:
					fabMenuPaste();
					break;
				case R.id.fabMenuItemClearFrame:
					fabMenuClear();
					break;
				case R.id.fabMenuItemColorFrame:
					break;
				case R.id.fabMenuItemFontFrame:
					break;
				default:
					break;
			}
		}
	};

	public View.OnClickListener onFabClick = new View.OnClickListener() {
		@Override
		public void onClick(View view) {

			// *** set up menu ***
			fabMenu = new Dialog(MainActivity.this);
			fabMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
			fabMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			fabMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			WindowManager.LayoutParams params = fabMenu.getWindow().getAttributes();
			params.gravity = Gravity.CENTER;
			fabMenu.setCancelable(true);
			fabMenu.setContentView(R.layout.dialog_fab_menu);

			// *** Menu Items ***

			// Copy
			MongolTextView tvCopy = (MongolTextView) fabMenu.findViewById(R.id.tvFabMenuItemCopy);
			tvCopy.setTextWithRenderedUnicode(tvCopy.getText());
			fabMenu.findViewById(R.id.fabMenuItemCopyFrame).setOnClickListener(onFabMenuItemSelected);

			// Paste
			MongolTextView tvPaste = (MongolTextView) fabMenu.findViewById(R.id.tvFabMenuItemPaste);
			tvPaste.setTextWithRenderedUnicode(tvPaste.getText());
			fabMenu.findViewById(R.id.fabMenuItemPasteFrame).setOnClickListener(onFabMenuItemSelected);

			// Clear
			MongolTextView tvClear = (MongolTextView) fabMenu.findViewById(R.id.tvFabMenuItemClear);
			tvClear.setTextWithRenderedUnicode(tvClear.getText());
			fabMenu.findViewById(R.id.fabMenuItemClearFrame).setOnClickListener(onFabMenuItemSelected);

			// Color
			MongolTextView tvColor = (MongolTextView) fabMenu.findViewById(R.id.tvFabMenuItemColor);
			tvColor.setTextWithRenderedUnicode(tvColor.getText());
			fabMenu.findViewById(R.id.fabMenuItemColorFrame).setOnClickListener(onFabMenuItemSelected);

			// Font
			MongolTextView tvFont = (MongolTextView) fabMenu.findViewById(R.id.tvFabMenuItemFont);
			tvFont.setTextWithRenderedUnicode(tvFont.getText());
			fabMenu.findViewById(R.id.fabMenuItemFontFrame).setOnClickListener(onFabMenuItemSelected);


			// *** show menu ***

			fabMenu.show();
		}
	};


//    private OnLongClickListener longClickHandler = new OnLongClickListener() {
//
//		@Override
//		public boolean onLongClick(View view) {
//			// Log.i("", "Long press!");
////			if (view.getId() == R.id.rlMessageOutline) {
////				//flContextMenuContainer.setVisibility(View.VISIBLE);
////				//menuHiderForOutsideClicks.setVisibility(View.VISIBLE);
////				// flContextMenuContainer.requestFocus();
////				showContextMenu();
////			}
//
//			return false;
//		}
//
//	};

	public void hideMenu() {

		if (overflowMenu != null) {
			overflowMenu.dismiss();
		}

		if (fabMenu != null) {
			fabMenu.dismiss();
		}

	}

//	private void showContextMenu() {
//
//		contextMenu = new Dialog(MainActivity.this);
//		// Making sure there's no title.
//		contextMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		// Making dialog content transparent.
//		contextMenu.getWindow().setBackgroundDrawable(
//				new ColorDrawable(Color.TRANSPARENT));
//		// Removing window dim normally visible when dialog are shown.
//		contextMenu.getWindow().clearFlags(
//				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		// Setting position of content, relative to window.
//		WindowManager.LayoutParams params = contextMenu.getWindow().getAttributes();
//		params.gravity = Gravity.CENTER;
//		//params.x = 16;
//		//params.y = 16;
//		// If user taps anywhere on the screen, dialog will be cancelled.
//		contextMenu.setCancelable(true);
//		// Setting the content using prepared XML layout file.
//		contextMenu.setContentView(R.layout.contextmenu_inputwindow);
//
//		// set onClick listeners for the menu items
//		contextMenu.findViewById(R.id.rlContextMenuCopy).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				contextMenuCopy(v);
//			}
//		});
//		contextMenu.findViewById(R.id.rlContextMenuPaste).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				contextMenuPaste(v);
//			}
//		});
//		contextMenu.findViewById(R.id.rlContextMenuFavorites).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				contextMenuFavorites(v);
//			}
//		});
//		contextMenu.findViewById(R.id.rlContextMenuClear).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				contextMenuClear(v);
//			}
//		});
//
//		contextMenu.show();
//	}


	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void fabMenuCopy() {

		hideMenu();

		// get selected text (or all text if no selection)
		CharSequence text = inputWindow.getSelectedText();
		if (inputWindow.getSelectedText().length() == 0) {
			text = inputWindow.getTextUnicode();
		}

		// error checking
		if (TextUtils.isEmpty(text)) {
			return;
		}

		// copy to clipboard
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("Mongol text", text);
			clipboard.setPrimaryClip(clip);
		}
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void fabMenuPaste() {
		hideMenu();

		int sdk = android.os.Build.VERSION.SDK_INT;
		String pasteData = "";
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

			try {
				pasteData = clipboard.getText().toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

			if (clipboard.getPrimaryClip() != null) {
				android.content.ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
				pasteData = item.getText().toString();
			}

		}

		if (pasteData != null) {
			inputWindow.insertMongolText(pasteData);
		}

		//updateDisplay();
	}

	public void contextMenuFavorites(View v) {
		hideMenu();

		// catch empty string
//		if (unicodeText.toString().trim().length() == 0) {
//			Intent intent = new Intent(this, MongolDialogOneButton.class);
//			intent.putExtra(MongolDialogOneButton.MESSAGE,
//					getResources().getString(R.string.dialog_message_emptyfavorite));
//			startActivity(intent);
//			return;
//		}
//
//		// add string to db
//		new AddMessageToFavoriteDb().execute();
	}

	public void fabMenuClear() {
		hideMenu();

//		// TODO add a warning if longer than a certain length
//		unicodeText.setLength(0);
//		cursorPosition = 0;
//		updateDisplay();
	}








	private void showToast(Context context, String text, int toastLength) {

		// TextView
		final float scale = getResources().getDisplayMetrics().density;
		int padding_8dp = (int) (8 * scale + 0.5f);
		MongolTextView tvMongolToastMessage = new MongolTextView(context);
		tvMongolToastMessage.setText(text);
		tvMongolToastMessage.setPadding(padding_8dp, padding_8dp, padding_8dp, padding_8dp);
		tvMongolToastMessage.setTextColor(getResources().getColor(R.color.white));

		// Layout
		LinearLayout toastLayout = new LinearLayout(context);
		toastLayout.setBackgroundResource(R.color.black_c);
		toastLayout.addView(tvMongolToastMessage);

		// Toast
		Toast mongolToast = new Toast(context);
		mongolToast.setView(toastLayout);
		mongolToast.setDuration(toastLength);
		mongolToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		mongolToast.show();

	}


	public void shareTo(ShareType shareDestination) {

		// catch empty string
		if (inputWindow.getText().length() == 0) {
			showToast(getApplicationContext(),
					getResources().getString(R.string.toast_message_empty), Toast.LENGTH_LONG);
			return;
		}

		// Check to see if SD card is available
		// TODO don't use external storage at all to save temp copy
		if (!externalStarageAvailable()) {
			showToast(getApplicationContext(),
					getResources().getString(R.string.toast_no_sdcard_cant_send), Toast.LENGTH_LONG);
			return;
		}

		// Save to history if different than last message (this session)
		String currentText = inputWindow.getText().toString();
		if (!lastSentMessage.equals(currentText)) {
			new SaveMessageToHistory().execute(currentText);
			lastSentMessage = currentText;
		}

		// Remove cursor from display
		inputWindow.setCursorVisible(false);

		createBitmap();

		File imagePath = new File(getApplicationContext().getExternalCacheDir(), "/");
		File newFile = new File(imagePath, "image.png");
		Uri contentUri = Uri.fromFile(newFile);

		if (contentUri == null) {
			return;
		}

		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.setDataAndType(contentUri, "image/png");
		shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		//File imagePath = new File(getApplicationContext().getCacheDir(), "images");
		//File newFile = new File(imagePath, "image.png");
		//Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "net.studymongolian.chimee.fileprovider", newFile);




//			Intent shareIntent = new Intent();
//			shareIntent.setAction(Intent.ACTION_SEND);
//			//shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
//			//shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
//			shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			//startActivity(Intent.createChooser(shareIntent, "Choose an app"));




//		File imagePath = new File(getApplicationContext().getExternalCacheDir(), "/");
//		File newFile = new File(imagePath, "image.png");
//		Uri contentUri = Uri.fromFile(newFile);
//
//		Intent shareIntent = new Intent();
//		shareIntent.setAction(Intent.ACTION_SEND);
//		shareIntent.setDataAndType(contentUri, "image/png");
//		shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//

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

	private void createBitmap() {

		RelativeLayout parentLayout = (RelativeLayout) findViewById(R.id.rlTop);
		LayoutInflater inflater = getLayoutInflater();
		View inputWindowCopy = inflater.inflate(R.layout.input_window, parentLayout, false);

		// set up the edit text copy
		MongolEditText met = (MongolEditText) inputWindowCopy.findViewById(R.id.etInputWindow);
		met.setBackgroundColor(inputWindow.getSolidColor()); // FIXME: Is this right?
		met.setText(inputWindow.getText());
		met.setTypeface(inputWindow.getTypeface());

		// measure and layout input window copy
		int h = mvgMessageOutline.getHeight();
		inputWindow.measure(View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		int w = inputWindow.getMeasuredHeight(); // because it's rotated
		if (w < mvgMessageOutline.getWidth()) {
			w = mvgMessageOutline.getWidth();
		}
		inputWindowCopy.layout(0, 0, w, h);

		// create bitmap
		Bitmap bitmap = Bitmap.createBitmap(inputWindowCopy.getWidth(), inputWindowCopy.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		inputWindowCopy.draw(canvas);

		// save bitmap to cache directory
		try {

//			File cachePath = new File(getApplicationContext().getCacheDir(), "images");
//			cachePath.mkdirs(); // don't forget to make the directory
//			FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
//			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//			stream.close();


			//TODO testing delete me
			File cachePath = getApplicationContext().getExternalCacheDir();
			FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
			bitmap.compress(CompressFormat.PNG, 100, stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void shareToSystemApps() {

//		// catch empty string
//		if (unicodeText.toString().trim().length() == 0) {
//			showToast(getApplicationContext(),
//					getResources().getString(R.string.toast_message_empty), Toast.LENGTH_LONG);
//			return;
//		}
//
//		// Check to see if SD card is available
//		if (!externalStarageAvailable()) {
//			showToast(getApplicationContext(),
//					getResources().getString(R.string.toast_no_sdcard_cant_send), Toast.LENGTH_LONG);
//			return;
//		}
//
//		// Save to history if different than last message (this session)
//		if (!lastSentMessage.equals(unicodeText.toString())) {
//			new SaveMessageToHistory().execute(unicodeText.toString());
//			lastSentMessage = unicodeText.toString();
//		}
//
//		// Remove cursor from display
//		//inputWindow.showCursor(false);
//
//		// Put this in a runnable to allow UI to update itself first
//		inputWindow.post(new Runnable() {
//			@Override
//			public void run() {
//
//				createBitmap();
//
//				File imagePath = new File(getApplicationContext().getExternalCacheDir(), "/");
//				File newFile = new File(imagePath, "image.png");
//				Uri contentUri = Uri.fromFile(newFile);
//
//				Intent shareIntent = new Intent();
//				shareIntent.setAction(Intent.ACTION_SEND);
//				shareIntent.setDataAndType(contentUri, "image/png");
//				shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivityForResult(Intent.createChooser(shareIntent, ""), SHARE_CHOOSER_REQUEST);
//
//				// Show cursor again
//				//inputWindow.showCursor(true);
//			}
//		});
	}


    public void photoActionBarClick() {

        // Start photo editing activity
        Intent intent = new Intent(this, PhotoOverlayActivity.class);
        // TODO intent.putExtra("message", unicodeText.toString());
        startActivityForResult(intent, PHOTO_OVERLAY_REQUEST);
    }

	public void favoriteActionBarClick() {

		// Start About activity
		Intent intent = new Intent(this, FavoriteActivity.class);
		// TODO intent.putExtra("message", unicodeText.toString());
		startActivityForResult(intent, FAVORITE_MESSAGE_REQUEST);
	}

	public void overflowActionBarClick() {

		// *** set up menu ***

		overflowMenu = new Dialog(MainActivity.this);
		overflowMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
		overflowMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		overflowMenu.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		WindowManager.LayoutParams params = overflowMenu.getWindow().getAttributes();
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		overflowMenu.setCancelable(true);
		overflowMenu.setContentView(R.layout.dialog_main_overflow_menu);

		// *** Menu Items ***

		// Share
		MongolTextView tvShare = (MongolTextView) overflowMenu.findViewById(R.id.tvMainMenuItemShare);
		tvShare.setTextWithRenderedUnicode(tvShare.getText());
		overflowMenu.findViewById(R.id.mainMenuItemShareFrame).setOnClickListener(onOverflowMenuItemSelected);

		// Open / Save
		MongolTextView tvOpenSave = (MongolTextView) overflowMenu.findViewById(R.id.tvMainMenuItemOpenSave);
		if (TextUtils.isEmpty(inputWindow.getText())) {
			String openString = getResources().getString(R.string.menu_item_open);
			tvOpenSave.setTextWithRenderedUnicode(openString);
		} else {
			String saveString = getResources().getString(R.string.menu_item_save);
			tvOpenSave.setTextWithRenderedUnicode(saveString);
		}
		overflowMenu.findViewById(R.id.mainMenuItemOpenSaveFrame).setOnClickListener(onOverflowMenuItemSelected);

		// Settings
		MongolTextView tvSettings = (MongolTextView) overflowMenu.findViewById(R.id.tvMainMenuItemSettings);
		tvSettings.setTextWithRenderedUnicode(tvSettings.getText());
		overflowMenu.findViewById(R.id.mainMenuItemSettingsFrame).setOnClickListener(onOverflowMenuItemSelected);


		// *** show menu ***

		overflowMenu.show();
	}

	public void menuHistoryClick(View v) {
		hideMenu();
		Intent customIntent = new Intent(this, HistoryActivity.class);
		startActivityForResult(customIntent, HISTORY_REQUEST);
	}

	public void menuSettingsClick() {
		hideMenu();
		Intent customIntent = new Intent(this, SettingsActivity.class);
		startActivityForResult(customIntent, SETTINGS_REQUEST);
	}

	public void menuOpenClick() {
		hideMenu();
		Intent customIntent = new Intent(this, OpenActivity.class);
		startActivity(customIntent);
	}

	public void menuSaveClick() {
		hideMenu();
//		Intent customIntent = new Intent(this, SaveActivity.class);
//		customIntent.putExtra("message", unicodeText.toString());
//		startActivity(customIntent);
	}

	public void menuAboutClick(View v) {
		hideMenu();
		Intent customIntent = new Intent(this, AboutActivity.class);
		startActivity(customIntent);
	}

	public void menuHelpClick(View v) {
		hideMenu();
		Intent customIntent = new Intent(this, HelpActivity.class);
		startActivity(customIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SHARE_CHOOSER_REQUEST) {

			if (resultCode == RESULT_OK) {
				// TODO this never gets called. Make a custom chooser
			}

			// TODO clear the input window after sending
			inputWindow.setText("");
//			unicodeText.setLength(0);
//			cursorPosition = 0;

			// Update the display with the cursor added back
			//updateDisplay();

		} else if (requestCode == WECHAT_REQUEST) {

			// TODO clear the input window after sending
			inputWindow.setText("");
//			unicodeText.setLength(0);
//			cursorPosition = 0;

			// Update the display with the cursor added back

			//updateDisplay();

		} else if (requestCode == HISTORY_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (data.hasExtra("resultString")) {
					String result = data.getExtras().getString("resultString");
//					unicodeText.insert(cursorPosition, result);
//					cursorPosition += result.length();
					//updateDisplay();
				}
			}

		} else if (requestCode == SETTINGS_REQUEST) {
			if (resultCode == RESULT_OK) {

				// Get preferences and update settings display
				settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);

				if (data.hasExtra("colorResult")) {

//					rlMessage.setBackgroundColor(settings.getInt(SettingsActivity.BGCOLOR_KEY,
//							SettingsActivity.BGCOLOR_DEFAULT));
//					int textColor = settings.getInt(SettingsActivity.TEXTCOLOR_KEY,
//							SettingsActivity.TEXTCOLOR_DEFAULT);
//					inputWindow.setTextColor(textColor);
//					//inputWindow.setCursorColor(textColor);

				}
				if (data.hasExtra("fontResult")) {

					String font = settings.getString(SettingsActivity.FONT_KEY,
							SettingsActivity.FONT_DEFAULT);
					Typeface tf = FontCache.get(font, getApplicationContext());
					if (tf != null) {
						inputWindow.setTypeface(tf);
					}

					// update cursor position
					//updateDisplay();
				}



			}
		} else if (requestCode == FAVORITE_MESSAGE_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (data.hasExtra("resultString")) {
					String result = data.getExtras().getString("resultString");
//					unicodeText.insert(cursorPosition, result);
//					cursorPosition += result.length();
					//updateDisplay();
				}

			}

		} else {

			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	protected boolean externalStarageAvailable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;

		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return (mExternalStorageAvailable && mExternalStorageWriteable);
	}



	// call: new AddMessageToFavoriteDb().execute();
	private class AddMessageToFavoriteDb extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			// android.os.Debug.waitForDebugger();

			try {

				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(
						getApplicationContext());
				// FIXME dbAdapter.addFavorateMessage(unicodeText.toString());

			} catch (Exception e) {
				//Log.e("app", e.toString());

			}
			return null;

		}

		@Override
		protected void onPostExecute(Void v) {

			// Notify the user that the message was deleted
			showToast(getApplicationContext(),
					getResources().getString(R.string.toast_favorite_added), Toast.LENGTH_SHORT);

		}

	}

	// call: new AddMessageToFavoriteDb().execute();
	private class SaveMessageToHistory extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			String messageText = params[0];

			try {

				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(
						getApplicationContext());
				dbAdapter.addHistoryMessage(messageText);

			} catch (Exception e) {
				//Log.e("app", e.toString());

			}
			return null;

		}
	}

}