package net.studymongolian.chimee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import net.studymongolian.chimee.MongolTextView.CursorTouchLocationListener;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements MongolAeiouKeyboard.Communicator,
		MongolQwertyKeyboard.Communicator, EnglishKeyboard.OnKeyTouchListener,
		InputWindowContextMenu.ContextMenuCallback {

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

	// Fragment tags
	public static final String MONGOL_AEIOU_TAG = "mongol_aeiou";
	public static final String MONGOL_QWERTY_TAG = "mongol_qwerty";
	public static final String ENGLISH_FRAGMENT_TAG = "english";
	public static final String CONTEXT_MENU_TAG = "context_menu";

	// public static final String NAVIGATION_FRAGMENT_TAG = "navigation";

	private enum Keyboard {
		MONGOLIAN_QWERTY, MONGOLIAN_AEIOU, ENGLISH
	};

	// EditText inputWindow;
	MongolTextView inputWindow;
	// TODO testing:
	// TextView testingView;

	RelativeLayout rlMessage;
	RelativeLayout rlLimit;
	RelativeLayout rlTop;
	LinearLayout llMenu;
	FrameLayout flContextMenuContainer;
	View menuHiderForOutsideClicks;
	static final int INPUT_WINDOW_SIZE_INCREMENT_DP = 50;
	int inputWindowSizeIncrementPx = 0; // size in pixels
	static final int INPUT_WINDOW_MIN_HEIGHT_DP = 150;
	int inputWindowMinHeightPx = 0; // size in pixels
	int renderedTextOldLength = 0;
	StringBuilder unicodeText = new StringBuilder();
	MongolUnicodeRenderer mongolianConverter = new MongolUnicodeRenderer();
	int cursorPosition = 0;
	Keyboard currentKeyboard;
	Keyboard userMongolKeyboard;
	SharedPreferences settings;
	boolean swapMongolKeyboards = false;
	MongolAeiouKeyboard mongolAeiouKeyboard;
	MongolQwertyKeyboard mongolQwertyKeyboard;
	EnglishKeyboard englishKeyboard;
	InputWindowContextMenu contextMenu;
	FragmentManager fragmentManager;
	String lastSentMessage = ""; // don't save two same messages to history

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get the settings
		initSettings();

		// get input window and set listeners
		initInputWindow();

		// Menu
		llMenu = (LinearLayout) findViewById(R.id.menuLayout);
		flContextMenuContainer = (FrameLayout) findViewById(R.id.flContextMenuContainer);
		menuHiderForOutsideClicks = (View) findViewById(R.id.transparent_view);

		// TODO testing:
		// testingView = (TextView) findViewById(R.id.tvTestWindow);

		// If WeChat is installed make the button visible
		String weChatMessageTool = "com.tencent.mm.ui.tools.ShareImgUI";
		Intent shareIntent = new Intent();
		shareIntent.setType("image/png");
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.name.equals(weChatMessageTool)) {

					FrameLayout weChatButton = (FrameLayout) findViewById(R.id.shareToWeChatFrame);
					weChatButton.setVisibility(View.VISIBLE);
					break;
				}
			}
		}

		// Set up fragments
		fragmentManager = getSupportFragmentManager();
		if (savedInstanceState == null) {

			if (userMongolKeyboard == Keyboard.MONGOLIAN_AEIOU) {
				mongolAeiouKeyboard = new MongolAeiouKeyboard();
				fragmentManager.beginTransaction()
						.add(R.id.keyboardContainer, mongolAeiouKeyboard, MONGOL_AEIOU_TAG)
						.commit();
				mongolAeiouKeyboard.setRetainInstance(true);
			} else {
				mongolQwertyKeyboard = new MongolQwertyKeyboard();
				fragmentManager.beginTransaction()
						.add(R.id.keyboardContainer, mongolQwertyKeyboard, MONGOL_QWERTY_TAG)
						.commit();
				mongolQwertyKeyboard.setRetainInstance(true);
			}

			contextMenu = new InputWindowContextMenu();
			fragmentManager.beginTransaction()
					.add(R.id.flContextMenuContainer, contextMenu, CONTEXT_MENU_TAG).commit();
			contextMenu.setRetainInstance(true);
		} else {

			if (userMongolKeyboard == Keyboard.MONGOLIAN_AEIOU) {
				mongolAeiouKeyboard = (MongolAeiouKeyboard) fragmentManager
						.findFragmentByTag(MONGOL_AEIOU_TAG);
			} else if (userMongolKeyboard == Keyboard.MONGOLIAN_QWERTY) {
				mongolQwertyKeyboard = (MongolQwertyKeyboard) fragmentManager
						.findFragmentByTag(MONGOL_QWERTY_TAG);
			}else { // English keyboard
                englishKeyboard = (EnglishKeyboard) fragmentManager
                        .findFragmentByTag(ENGLISH_FRAGMENT_TAG);
            }

			contextMenu = (InputWindowContextMenu) fragmentManager
					.findFragmentByTag(CONTEXT_MENU_TAG);

		}

		rlMessage = (RelativeLayout) findViewById(R.id.rlMessageOutline);
		rlMessage.setOnLongClickListener(longClickHandler);
		rlLimit = (RelativeLayout) findViewById(R.id.rlMessageLimit);
		rlTop = (RelativeLayout) findViewById(R.id.rlTop);

		// set colors
		rlMessage.setBackgroundColor(settings.getInt(SettingsActivity.BGCOLOR_KEY,
				SettingsActivity.BGCOLOR_DEFAULT));
		int textColor = settings.getInt(SettingsActivity.TEXTCOLOR_KEY,
				SettingsActivity.TEXTCOLOR_DEFAULT);
		inputWindow.setTextColor(textColor);
		inputWindow.setCursorColor(textColor);

		// set font
		String font = settings.getString(SettingsActivity.FONT_KEY, SettingsActivity.FONT_DEFAULT);
		Typeface tf = FontCache.get(font, getApplicationContext());
		if (tf != null) {
			inputWindow.setTypeface(tf);
		}

		// Set up density independent pixel constants after layout done
		rlMessage.post(new Runnable() {
			public void run() {
				inputWindowSizeIncrementPx = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, INPUT_WINDOW_SIZE_INCREMENT_DP, getResources()
								.getDisplayMetrics());
				inputWindowMinHeightPx = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, INPUT_WINDOW_MIN_HEIGHT_DP, getResources()
								.getDisplayMetrics());
			}
		});

		/*if (savedInstanceState != null) {
			unicodeText.append(savedInstanceState.getString("unicodeSave"));
			cursorPosition = savedInstanceState.getInt("positionSave");
		}*/

		// Make the cursor show
		updateDisplay();
	}

	private void initInputWindow() {

		inputWindow = (MongolTextView) findViewById(R.id.tvInputWindow);
		inputWindow.setOnTouchListener(inputWindow.new InputWindowTouchListener());
		inputWindow.setCursorTouchLocationListener(new CursorTouchLocationListener() {
			@Override
			public void onCursorTouchLocationChanged(int glyphIndex) {

				// convert glyphIndex to cursorPosition
				// TODO rather than calculating it here could do it when ready to input text
				// but that would leave room for errors
				cursorPosition = mongolianConverter.getUnicodeIndex(unicodeText.toString(),
						glyphIndex);

			}
		});
	}

	private void initSettings() {

		settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);

        // get the right keyboard
		String userKeyboard = settings.getString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
				SettingsActivity.MONGOLIAN_KEYBOARD_DEFAULT);
		if (userKeyboard.equals(SettingsActivity.MONGOLIAN_AEIOU_KEYBOARD)) {
			userMongolKeyboard = Keyboard.MONGOLIAN_AEIOU;
		} else {
			userMongolKeyboard = Keyboard.MONGOLIAN_QWERTY;
		}
		currentKeyboard = userMongolKeyboard;

        // get a saved draft
        if (unicodeText.length() == 0){
            unicodeText.append(settings.getString(SettingsActivity.DRAFT_KEY, SettingsActivity.DRAFT_DEFAULT));
            cursorPosition = settings.getInt(SettingsActivity.CURSOR_POSITION_KEY, SettingsActivity.CURSOR_POSITION_DEFAULT);
        }

	}

	@Override
	public void onPause() {
		super.onPause();

		// hide the menu if showing
		if (llMenu.getVisibility() == View.VISIBLE) {
			llMenu.setVisibility(View.GONE);
			flContextMenuContainer.setVisibility(View.GONE);
			menuHiderForOutsideClicks.setVisibility(View.GONE);
		}

	}

    @Override
    public void onStop() {
        super.onStop();

        // save draft unicode text that is in the input window in case user accidentally closes app
        settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SettingsActivity.DRAFT_KEY, unicodeText.toString());
        editor.putInt(SettingsActivity.CURSOR_POSITION_KEY, cursorPosition);
        editor.commit();
    }

	@Override
	protected void onPostResume() {
		super.onPostResume();
		if (swapMongolKeyboards) {
			// Committing transactions in onPostResume to avoid state loss exception
			Keyboard savedKeyboard;
			String savedKeyboardString = settings.getString(
					SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
					SettingsActivity.MONGOLIAN_KEYBOARD_DEFAULT);
			if (savedKeyboardString.equals(SettingsActivity.MONGOLIAN_AEIOU_KEYBOARD)) {
				savedKeyboard = Keyboard.MONGOLIAN_AEIOU;
			} else {
				savedKeyboard = Keyboard.MONGOLIAN_QWERTY;
			}
			if (!(savedKeyboard == userMongolKeyboard)) {
				if (savedKeyboard == Keyboard.MONGOLIAN_AEIOU) {

					mongolAeiouKeyboard = (MongolAeiouKeyboard) fragmentManager
							.findFragmentByTag(MONGOL_AEIOU_TAG);
					if (mongolAeiouKeyboard == null) {
						mongolAeiouKeyboard = new MongolAeiouKeyboard();
						fragmentManager
								.beginTransaction()
								.replace(R.id.keyboardContainer, mongolAeiouKeyboard,
										MONGOL_AEIOU_TAG).commitAllowingStateLoss();
					}
					userMongolKeyboard = Keyboard.MONGOLIAN_AEIOU;
				} else {

					mongolQwertyKeyboard = (MongolQwertyKeyboard) fragmentManager
							.findFragmentByTag(MONGOL_QWERTY_TAG);
					if (mongolQwertyKeyboard == null) {
						mongolQwertyKeyboard = new MongolQwertyKeyboard();
						fragmentManager
								.beginTransaction()
								.replace(R.id.keyboardContainer, mongolQwertyKeyboard,
										MONGOL_QWERTY_TAG).commitAllowingStateLoss();
					}
					userMongolKeyboard = Keyboard.MONGOLIAN_QWERTY;
				}
				currentKeyboard = userMongolKeyboard;
			}
		}
		// Reset the boolean flag back to false for next time.
		swapMongolKeyboards = false;
	}


	private OnLongClickListener longClickHandler = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View view) {
			// Log.i("", "Long press!");
			if (view.getId() == R.id.rlMessageOutline) {
				flContextMenuContainer.setVisibility(View.VISIBLE);
				menuHiderForOutsideClicks.setVisibility(View.VISIBLE);
				// flContextMenuContainer.requestFocus();
			}

			return false;
		}

	};

	public void hideMenu(View view) {

		flContextMenuContainer.setVisibility(View.GONE);
		llMenu.setVisibility(View.GONE);
		menuHiderForOutsideClicks.setVisibility(View.GONE);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			// open/close the menu from the phones physical menu button
			if (llMenu.getVisibility() == View.GONE) {
				llMenu.setVisibility(View.VISIBLE);
				menuHiderForOutsideClicks.setVisibility(View.VISIBLE);
				flContextMenuContainer.setVisibility(View.GONE);
			} else {
				hideMenu(null);
			}

			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void contextMenuItemClicked(int itemCode) {
		// Gets info from fragment

		int sdk = android.os.Build.VERSION.SDK_INT;

		switch (itemCode) {
		case InputWindowContextMenu.COPY:

			if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
				android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(unicodeText);
			} else {
				android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				android.content.ClipData clip = android.content.ClipData.newPlainText(
						"ChimeeUnicodeText", unicodeText);
				clipboard.setPrimaryClip(clip);
			}

			break;
		case InputWindowContextMenu.PASTE:

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
				unicodeText.insert(cursorPosition, pasteData);
				cursorPosition += pasteData.length();
			}

			updateDisplay();

			break;
		case InputWindowContextMenu.FAVORITES:

			// catch empty string
			if (unicodeText.toString().trim().length() == 0) {
				Intent intent = new Intent(this, MongolDialogOneButton.class);
				intent.putExtra(MongolDialogOneButton.MESSAGE,
						getResources().getString(R.string.dialog_message_emptyfavorite));
				startActivity(intent);
				return;
			}

			// add string to db
			new AddMessageToFavoriteDb().execute();

			break;
		case InputWindowContextMenu.CLEAR:

			// TODO add a warning if longer than a certain length
			unicodeText.setLength(0);
			cursorPosition = 0;
			updateDisplay();

			break;
		}

		hideMenu(null);
	}

	/*public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save text in case of screen rotation
		savedInstanceState.putString("unicodeSave", unicodeText.toString());
		savedInstanceState.putInt("positionSave", cursorPosition);
		super.onSaveInstanceState(savedInstanceState);
	}*/

	@Override
	public void onKeyTouched(char keyChar) {

		// error checking
		if (keyChar == NULL_CHAR) {
			return;
		}

		if (keyChar == BACKSPACE) {

			backspace();

		} else if (keyChar == NNBS) {

			// replace a space if there is one
			char lastChar = getCharBeforeCursor();
			if (lastChar == SPACE) {
				unicodeText.insert(cursorPosition, keyChar);
				unicodeText.delete(cursorPosition - 1, cursorPosition);
			} else if (lastChar != NNBS) {
				unicodeText.insert(cursorPosition, keyChar);
				cursorPosition++;
			}

		} else if (keyChar == MONGOLIAN_MVS) {

			final char MONGOLIAN_A = MongolUnicodeRenderer.UNI_A;
			final char MONGOLIAN_E = MongolUnicodeRenderer.UNI_E;

			// add the correct A/E vowel depending on the word
			String stringToAdd;
			String thisWord = getWordBeforeCursor();
			if (mongolianConverter.isMasculineWord(thisWord)) {

				stringToAdd = Character.toString(keyChar) + MONGOLIAN_A;
				unicodeText.insert(cursorPosition, stringToAdd);
				cursorPosition += 2;

			} else if (mongolianConverter.isFeminineWord(thisWord)) {

				stringToAdd = Character.toString(keyChar) + MONGOLIAN_E;
				unicodeText.insert(cursorPosition, stringToAdd);
				cursorPosition += 2;

			} else {

				// Unknown gender. Three choices:

				// 1. Let the user choose.
				// 2. Assume feminine
				// 3. Assume masculine. If assuming this might be better than
				// assuming feminine so that g/x are separate.

				if (currentKeyboard == Keyboard.MONGOLIAN_QWERTY) {
					// Let the user choose.
					unicodeText.insert(cursorPosition, keyChar);
					cursorPosition++;
					// Bring up a dialog box with FVS choices
					Intent intent = new Intent(getApplicationContext(), AeChooserDialog.class);
					this.startActivityForResult(intent, AE_REQUEST);

				} else {
					// Assume feminine
					stringToAdd = Character.toString(keyChar) + MONGOLIAN_E;
					unicodeText.insert(cursorPosition, stringToAdd);
					cursorPosition += 2;
				}

			}

		} else if (keyChar == MONGOLIAN_COMMA || keyChar == MONGOLIAN_FULL_STOP || keyChar == '?'
				|| keyChar == '!') {

			// Place punctuation automatically if entered after a space
			if (getCharBeforeCursor() == SPACE) {
				unicodeText.insert(cursorPosition - 1, keyChar);
				cursorPosition++;
			} else {
				String stringToAdd = Character.toString(keyChar) + SPACE;
				unicodeText.insert(cursorPosition, stringToAdd);
				cursorPosition += 2;
			}

		} else if (keyChar == SWITCH_TO_ENGLISH) {
			// load the English keyboard fragment

            //englishKeyboard = (EnglishKeyboard) fragmentManager
             //       .findFragmentByTag(ENGLISH_FRAGMENT_TAG);
			if (englishKeyboard == null) {

				englishKeyboard = new EnglishKeyboard();
				fragmentManager.beginTransaction()
						.replace(R.id.keyboardContainer, englishKeyboard, ENGLISH_FRAGMENT_TAG)
						.commit();
                englishKeyboard.setRetainInstance(true);
			}else{
                fragmentManager.beginTransaction()
                        .replace(R.id.keyboardContainer, englishKeyboard, ENGLISH_FRAGMENT_TAG)
                        .commit();
            }

			currentKeyboard = Keyboard.ENGLISH;
		} else if (keyChar == SWITCH_TO_MONGOLIAN) {
			// load the Mongolian keyboard fragment

			if (userMongolKeyboard == Keyboard.MONGOLIAN_AEIOU) {
                //mongolAeiouKeyboard = (MongolAeiouKeyboard) fragmentManager
                //       .findFragmentByTag(MONGOL_AEIOU_TAG);
                // TODO for some reason findFragmentByTag is always null
                if (mongolAeiouKeyboard == null) {

                    mongolAeiouKeyboard = new MongolAeiouKeyboard();
                    fragmentManager.beginTransaction()
                            .replace(R.id.keyboardContainer, mongolAeiouKeyboard, MONGOL_AEIOU_TAG)
                            .commit();
                    mongolAeiouKeyboard.setRetainInstance(true);
                }else{
                    fragmentManager.beginTransaction()
                            .replace(R.id.keyboardContainer, mongolAeiouKeyboard, MONGOL_AEIOU_TAG)
                            .commit();
                }


				currentKeyboard = Keyboard.MONGOLIAN_AEIOU;
			} else {
                //mongolQwertyKeyboard = (MongolQwertyKeyboard) fragmentManager
                //        .findFragmentByTag(MONGOL_QWERTY_TAG);
                // TODO for some reason findFragmentByTag is always null
                if (mongolQwertyKeyboard == null) {

                    mongolQwertyKeyboard = new MongolQwertyKeyboard();
                    fragmentManager.beginTransaction()
                            .replace(R.id.keyboardContainer, mongolQwertyKeyboard, MONGOL_QWERTY_TAG)
                            .commit();
                    mongolQwertyKeyboard.setRetainInstance(true);
                }else{
                    fragmentManager.beginTransaction()
                            .replace(R.id.keyboardContainer, mongolQwertyKeyboard, MONGOL_QWERTY_TAG)
                            .commit();
                }
				currentKeyboard = Keyboard.MONGOLIAN_QWERTY;
			}

		} else if (MongolUnicodeRenderer.isVowel(keyChar)) {

			// These rules are for convenience because unicode rules are unnatural
			// Only apply them for non initial Y and W
			if (MongolUnicodeRenderer.isMongolian(getSecondCharBeforeCursor())) {

				if (getCharBeforeCursor() == MongolUnicodeRenderer.UNI_YA) {

					// Automatically use the hooked Y when writing YI
					if (keyChar == MongolUnicodeRenderer.UNI_I) {
						unicodeText.insert(cursorPosition, MongolUnicodeRenderer.FVS1);
						cursorPosition++;
					}

				} else if (getCharBeforeCursor() == MongolUnicodeRenderer.UNI_WA) {

					// Automatically use the hooked W when followed with a vowel
					unicodeText.insert(cursorPosition, MongolUnicodeRenderer.FVS1);
					cursorPosition++;
				}
			}
			unicodeText.insert(cursorPosition, keyChar);
			cursorPosition++;

		} else {

			unicodeText.insert(cursorPosition, keyChar);
			cursorPosition++;
		}

		updateDisplay();

	}

	@Override
	public String getPreviousWord() {

		// If there is a space before the current word
		// then get word before that
		StringBuilder word = new StringBuilder();

		// Allow for certain single characters after the end word
		char s;
		int startPosition = cursorPosition - 1;
		if (startPosition >= 0) {
			s = unicodeText.charAt(startPosition);
			if (s == SPACE) {
				startPosition--;
				if (startPosition > 0) {
					s = unicodeText.charAt(startPosition);
					if (s == '?' || s == '!' || s == MONGOLIAN_COMMA || s == MONGOLIAN_FULL_STOP) {

						startPosition--;
					}
				}

			} else if (s == '?' || s == '!' || s == NEW_LINE || s == MONGOLIAN_COMMA
					|| s == MONGOLIAN_FULL_STOP || s == NNBS) {
				startPosition--;
			}
		} else {
			return "";
		}

		// Back up to the space before current word if exists
		int spacePosition = 0;
		for (int i = startPosition; i >= 0; i--) {
			if (!MongolUnicodeRenderer.isMongolian(unicodeText.charAt(i))) {
				if (i < startPosition
						&& (unicodeText.charAt(i) == ' ' || unicodeText.charAt(i) == NNBS)) {
					spacePosition = i;
				}
				break;
			}
		}
		// Get the word before that space if exists
		if (spacePosition > 1) {
			for (int i = spacePosition - 1; i >= 0; i--) {
				if (unicodeText.charAt(i) == NNBS) {
					// Stop at NNBS.
					// Consider it part of the suffix
					// But consider anything before as a separate word
					word.insert(0, unicodeText.charAt(i));
					break;
				} else if (MongolUnicodeRenderer.isMongolian(unicodeText.charAt(i))) {
					word.insert(0, unicodeText.charAt(i));
				} else {
					break;
				}
			}
		}

		return word.toString();
	}

	@Override
	public String getWordBeforeCursor() {

		StringBuilder word = new StringBuilder();

		// Allow for certain single characters after the word (or two if it is a space)
		char s;
		int startPosition = cursorPosition - 1;
		if (startPosition >= 0) {
			s = unicodeText.charAt(startPosition);
			if (s == SPACE) {
				startPosition--;
				if (startPosition > 0) {
					s = unicodeText.charAt(startPosition);
					if (s == '?' || s == '!' || s == MONGOLIAN_COMMA || s == MONGOLIAN_FULL_STOP) {

						startPosition--;
					}
				}

			} else if (s == '?' || s == '!' || s == NEW_LINE || s == MONGOLIAN_COMMA
					|| s == MONGOLIAN_FULL_STOP || s == NNBS) {
				startPosition--;
			}
		} else {
			return "";
		}

		// Get the word
		for (int i = startPosition; i >= 0; i--) {

			if (unicodeText.charAt(i) == NNBS) {
				// Stop at NNBS.
				// Consider it part of the suffix
				// But consider anything before as a separate word
				word.insert(0, unicodeText.charAt(i));
				break;
			} else if (MongolUnicodeRenderer.isMongolian(unicodeText.charAt(i))) {
				word.insert(0, unicodeText.charAt(i));
			} else {
				break;
			}
		}

		return word.toString();
	}

	@Override
	public char getCharBeforeCursor() {

		if (unicodeText.length() > 0 && cursorPosition > 0) {
			return unicodeText.charAt(cursorPosition - 1);
		} else {
			return NULL_CHAR;
		}

	}

	private char getSecondCharBeforeCursor() {

		if (unicodeText.length() > 1 && cursorPosition > 1) {
			return unicodeText.charAt(cursorPosition - 2);
		} else {
			return NULL_CHAR;
		}

	}

	@Override
	public MongolUnicodeRenderer.Location getLocationOfCharInMongolWord(int cursorOffset) {

		int index = cursorPosition + cursorOffset;

		if (index < 0 || index >= unicodeText.length()) {
			return MongolUnicodeRenderer.Location.NOT_MONGOLIAN;
		}

		if (index < unicodeText.length() - 1
				&& MongolUnicodeRenderer.isMongolian(unicodeText.charAt(index + 1))) {
			// next char is mongolian
			if (index > 0 && MongolUnicodeRenderer.isMongolian(unicodeText.charAt(index - 1))) {
				// previous char is mongolian
				return MongolUnicodeRenderer.Location.MEDIAL;
			} else {// previous char isn't mongolian
				return MongolUnicodeRenderer.Location.INITIAL;
			}
		} else {// next char isn't mongolian
			if (index > 0 && MongolUnicodeRenderer.isMongolian(unicodeText.charAt(index - 1))) {
				// previous char is mongolian
				return MongolUnicodeRenderer.Location.FINAL;
			} else {// previous char isn't mongolian
				return MongolUnicodeRenderer.Location.ISOLATE;
			}
		}

	}

	@Override
	public void replaceFromWordStartToCursor(String replacementString) {

		// Delete from cursor to beginning of word
		int index = -1;
		for (int i = cursorPosition - 1; i >= 0; i--) {
			if (!MongolUnicodeRenderer.isMongolian(unicodeText.charAt(i))) {
				index = i;
				break;
			}
		}
		index++;
		if (index < cursorPosition) {
			unicodeText.delete(index, cursorPosition);
			cursorPosition = index;
		}

		// If this is an NNBS suffix then also delete the space
		if (cursorPosition > 0
				&& replacementString.charAt(0) == NNBS
				&& (unicodeText.charAt(cursorPosition - 1) == SPACE || unicodeText
						.charAt(cursorPosition - 1) == NNBS)) {
			unicodeText.delete(cursorPosition - 1, cursorPosition);
			cursorPosition--;
		}

		// Insert new word and a space
		unicodeText.insert(cursorPosition, replacementString + " ");
		cursorPosition = cursorPosition + replacementString.length() + 1;

		// Update display
		updateDisplay();

	}


	private void updateDisplay() {

		// Log.i("Chimee", Integer.toString(inputWindow.getLineCount()));

		// Set text
		StringBuilder tempText = new StringBuilder();
		StringBuilder renderedText = new StringBuilder();
		tempText.append(unicodeText.toString());
		tempText.insert(cursorPosition, CURSOR_HOLDER);
		renderedText.append(mongolianConverter.unicodeToGlyphs(tempText.toString()));
		final int glyphCursorPosition = renderedText.indexOf(String.valueOf(CURSOR_HOLDER));
		if (glyphCursorPosition >= 0 && glyphCursorPosition < renderedText.length()) {
			renderedText = renderedText.deleteCharAt(glyphCursorPosition);
		}
		inputWindow.setText(renderedText);
		inputWindow.post(new Runnable() {

			@Override
			public void run() {
				inputWindow.setCursorLocation(glyphCursorPosition);
			}

		});

		// Check if the view size needs changed
		// TODO this is clunky
		// TODO don't let users write off the screen OR make the screen scrollable
		// http://stackoverflow.com/questions/27985992/how-to-know-what-size-a-textview-would-be-with-certain-string
		if (renderedText.length() > renderedTextOldLength) {
			if (rlMessage.getWidth() > rlMessage.getHeight()) {

				if (rlMessage.getHeight() < rlTop.getHeight()) {

					if (rlMessage.getHeight() + inputWindowSizeIncrementPx < rlTop.getHeight()) {
						// increase by increment unit
						LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
						params.height = rlMessage.getHeight() + inputWindowSizeIncrementPx;
						rlLimit.setLayoutParams(params);

					} else {
						// set input window height to max height
						LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
						params.height = rlTop.getHeight();
						rlLimit.setLayoutParams(params);
					}

				}
			} else if (2 * rlMessage.getWidth() < rlMessage.getHeight()
					&& rlMessage.getWidth() < rlTop.getWidth()) {
				// got too high, make it shorter
				if (inputWindowMinHeightPx < rlMessage.getHeight() - inputWindowSizeIncrementPx) {
					LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
					params.height = rlMessage.getHeight() - inputWindowSizeIncrementPx;
					rlLimit.setLayoutParams(params);
				} else {
					// Bump down to min height
					LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
					params.height = inputWindowMinHeightPx;
					rlLimit.setLayoutParams(params);
				}

			}
		} else { // length <= old length

			if (2 * rlMessage.getWidth() < rlMessage.getHeight()) {
				if (inputWindowMinHeightPx < rlMessage.getHeight() - inputWindowSizeIncrementPx) {
					LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
					params.height = rlMessage.getHeight() - inputWindowSizeIncrementPx;
					rlLimit.setLayoutParams(params);
				} else {
					// Bump down to min height
					LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
					params.height = inputWindowMinHeightPx;
					rlLimit.setLayoutParams(params);
				}
			} else if (rlMessage.getWidth() >= rlTop.getWidth()
					&& rlMessage.getHeight() < rlTop.getHeight()) {
				if (unicodeText.length() == 0) {
					// set input window height to min height in the case that clear was pressed
					LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
					params.height = inputWindowMinHeightPx;
					rlLimit.setLayoutParams(params);
				} else {
					// set input window height to max height in the case that a long string was
					// pasted
					LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
					params.height = rlTop.getHeight();
					rlLimit.setLayoutParams(params);
				}
			}

			if (unicodeText.length() == 0 && rlMessage.getHeight() > inputWindowMinHeightPx) {
				// set input window height to min height in the case that clear was pressed
				LayoutParams params = (LayoutParams) rlLimit.getLayoutParams();
				params.height = inputWindowMinHeightPx;
				rlLimit.setLayoutParams(params);
			}

		}
		renderedTextOldLength = renderedText.length();

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

	public void shareToWeChat(View v) {

		// catch empty string
		if (unicodeText.toString().trim().length() == 0) {
			showToast(getApplicationContext(),
					getResources().getString(R.string.toast_message_empty), Toast.LENGTH_LONG);
			return;
		}

		// Check to see if SD card is available
		if (!externalStarageAvailable()) {
			showToast(getApplicationContext(),
					getResources().getString(R.string.toast_no_sdcard_cant_send), Toast.LENGTH_LONG);
			return;
		}

		// Save to history if different than last message (this session)
		if (!lastSentMessage.equals(unicodeText.toString())) {
			new SaveMessageToHistory().execute(unicodeText.toString());
			lastSentMessage = unicodeText.toString();
		}

		// Remove cursor from display
		inputWindow.showCursor(false);

		// Put this in a runnable to allow UI to update itself first
		inputWindow.post(new Runnable() {
			@Override
			public void run() {
				// TODO already have a reference to messageOutline
				RelativeLayout messageOutline = (RelativeLayout) findViewById(R.id.rlMessageOutline);
				messageOutline.setDrawingCacheEnabled(true);
				Bitmap bitmap = messageOutline.getDrawingCache(true);

				try {
					File cachePath = getApplicationContext().getExternalCacheDir();
					FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
					bitmap.compress(CompressFormat.PNG, 100, stream);
					stream.close();

					// Also save as text
					FileOutputStream streamUnicode = new FileOutputStream(cachePath
							+ "/unicode.txt");
					OutputStreamWriter myOutWriter = new OutputStreamWriter(streamUnicode);
					myOutWriter.append(unicodeText.toString());
					myOutWriter.close();
					streamUnicode.close();
					// And the rendered text too
					FileOutputStream streamRendered = new FileOutputStream(cachePath
							+ "/rendered.txt");
					OutputStreamWriter myOutWriter2 = new OutputStreamWriter(streamRendered);
					myOutWriter2.append(mongolianConverter.unicodeToGlyphs(unicodeText.toString()));
					myOutWriter2.close();
					streamRendered.close();

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				messageOutline.setDrawingCacheEnabled(false);

				File imagePath = new File(getApplicationContext().getExternalCacheDir(), "/");
				File newFile = new File(imagePath, "image.png");
				Uri contentUri = Uri.fromFile(newFile);

				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setDataAndType(contentUri, "image/png");
				shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


				// String weChatMessageTool =
				// "com.tencent.mm.ui.tools.shareimgui";
				ComponentName comp = new ComponentName("com.tencent.mm",
						"com.tencent.mm.ui.tools.ShareImgUI");
				shareIntent.setComponent(comp);
				startActivityForResult(shareIntent, WECHAT_REQUEST);

				// Show cursor again
				inputWindow.showCursor(true);

			}
		});

	}

	public void shareToSystemApps(View v) {

		// catch empty string
		if (unicodeText.toString().trim().length() == 0) {
			showToast(getApplicationContext(),
					getResources().getString(R.string.toast_message_empty), Toast.LENGTH_LONG);
			return;
		}

		// Check to see if SD card is available
		if (!externalStarageAvailable()) {
			showToast(getApplicationContext(),
					getResources().getString(R.string.toast_no_sdcard_cant_send), Toast.LENGTH_LONG);
			return;
		}

		// Save to history if different than last message (this session)
		if (!lastSentMessage.equals(unicodeText.toString())) {
			new SaveMessageToHistory().execute(unicodeText.toString());
			lastSentMessage = unicodeText.toString();
		}

		// Remove cursor from display
		inputWindow.showCursor(false);

		// Put this in a runnable to allow UI to update itself first
		inputWindow.post(new Runnable() {
			@Override
			public void run() {
				// TODO already have a reference to messageOutline
				RelativeLayout messageOutline = (RelativeLayout) findViewById(R.id.rlMessageOutline);
				messageOutline.setDrawingCacheEnabled(true);
				Bitmap bitmap = messageOutline.getDrawingCache(true);

				try {
					FileOutputStream stream = new FileOutputStream(getApplicationContext()
							.getExternalCacheDir() + "/image.png");
					bitmap.compress(CompressFormat.PNG, 80, stream);
					stream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				messageOutline.setDrawingCacheEnabled(false);

				File imagePath = new File(getApplicationContext().getExternalCacheDir(), "/");
				File newFile = new File(imagePath, "image.png");
				Uri contentUri = Uri.fromFile(newFile);

				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setDataAndType(contentUri, "image/png");
				shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
				shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivityForResult(Intent.createChooser(shareIntent, ""), SHARE_CHOOSER_REQUEST);

				// Show cursor again
				inputWindow.showCursor(true);
			}
		});
	}

	public void favoriteActionBarClick(View v) {

		// Start About activity
		Intent intent = new Intent(this, FavoriteActivity.class);
		intent.putExtra("message", unicodeText.toString());
		startActivityForResult(intent, FAVORITE_MESSAGE_REQUEST);
	}

	public void overflowActionBarClick(View v) {

		if (llMenu.getVisibility() == View.GONE) {
			llMenu.setVisibility(View.VISIBLE);
			menuHiderForOutsideClicks.setVisibility(View.VISIBLE);
		} else {
			llMenu.setVisibility(View.GONE);
			menuHiderForOutsideClicks.setVisibility(View.GONE);
		}
	}

	public void menuHistoryClick(View v) {
		llMenu.setVisibility(View.GONE);
		menuHiderForOutsideClicks.setVisibility(View.GONE);

		// Start settings activity
		Intent customIntent = new Intent(this, HistoryActivity.class);
		startActivityForResult(customIntent, HISTORY_REQUEST);
	}

	public void menuSettingsClick(View v) {
		llMenu.setVisibility(View.GONE);
		menuHiderForOutsideClicks.setVisibility(View.GONE);

		// Start settings activity
		Intent customIntent = new Intent(this, SettingsActivity.class);
		startActivityForResult(customIntent, SETTINGS_REQUEST);
	}

	public void menuAboutClick(View v) {
		llMenu.setVisibility(View.GONE);
		menuHiderForOutsideClicks.setVisibility(View.GONE);

		// Start About activity
		Intent customIntent = new Intent(this, AboutActivity.class);
		startActivity(customIntent);
	}

	public void menuHelpClick(View v) {
		llMenu.setVisibility(View.GONE);
		menuHiderForOutsideClicks.setVisibility(View.GONE);

		Intent customIntent = new Intent(this, HelpActivity.class);
		startActivity(customIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SHARE_CHOOSER_REQUEST) {

			if (resultCode == RESULT_OK) {
				// TODO this never gets called. Make a custom chooser
			}

			// clear the input window after sending
			unicodeText.setLength(0);
			cursorPosition = 0;

			// Update the display with the cursor added back
			updateDisplay();

		} else if (requestCode == WECHAT_REQUEST) {

			// clear the input window after sending
			unicodeText.setLength(0);
			cursorPosition = 0;

			// Update the display with the cursor added back
			updateDisplay();

		} else if (requestCode == HISTORY_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (data.hasExtra("resultString")) {
					String result = data.getExtras().getString("resultString");
					unicodeText.insert(cursorPosition, result);
					cursorPosition += result.length();
					updateDisplay();
				}
			}

		} else if (requestCode == SETTINGS_REQUEST) {
			if (resultCode == RESULT_OK) {

				// Get preferences and update settings display
				settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);

				if (data.hasExtra("colorResult")) {

					rlMessage.setBackgroundColor(settings.getInt(SettingsActivity.BGCOLOR_KEY,
							SettingsActivity.BGCOLOR_DEFAULT));
					int textColor = settings.getInt(SettingsActivity.TEXTCOLOR_KEY,
							SettingsActivity.TEXTCOLOR_DEFAULT);
					inputWindow.setTextColor(textColor);
					inputWindow.setCursorColor(textColor);

				}
				if (data.hasExtra("fontResult")) {

					String font = settings.getString(SettingsActivity.FONT_KEY,
							SettingsActivity.FONT_DEFAULT);
					Typeface tf = FontCache.get(font, getApplicationContext());
					if (tf != null) {
						inputWindow.setTypeface(tf);
					}

					// update cursor position
					updateDisplay();
				}

				// change the keyboard if needed
				// This will be done in onPostResume()
				if (data.hasExtra("keyboardResult")) {
					swapMongolKeyboards = data.getBooleanExtra("keyboardResult", false);
				}

			}
		} else if (requestCode == FAVORITE_MESSAGE_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (data.hasExtra("resultString")) {
					String result = data.getExtras().getString("resultString");
					unicodeText.insert(cursorPosition, result);
					cursorPosition += result.length();
					updateDisplay();
				}

			}
		} else if (requestCode == AE_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (data.hasExtra("result")) {
					char aeValue = data.getCharExtra("result", MongolUnicodeRenderer.UNI_E);
					unicodeText.insert(cursorPosition, String.valueOf(aeValue));
					cursorPosition++;
					updateDisplay();
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


	// Deletes the character before the cursor
	private void backspace() {
		// if there are invisible formatting characters after the visible char
		// then these should be deleted first.
		char c;
		if (cursorPosition <= 0) {
			return;
		}

		do {
			c = unicodeText.charAt(cursorPosition - 1);
			unicodeText.deleteCharAt(cursorPosition - 1);
			cursorPosition--;

			// if it was an invisible formatting char then backspace over the
			// next one too
		} while (cursorPosition > 0
				&& (c == ZWJ || c == MONGOLIAN_FVS1 || c == MONGOLIAN_FVS2 || c == MONGOLIAN_FVS3 || c == MONGOLIAN_MVS));

	}

	// call: new AddMessageToFavoriteDb().execute();
	private class AddMessageToFavoriteDb extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			// android.os.Debug.waitForDebugger();

			try {

				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(
						getApplicationContext());
				dbAdapter.addFavorateMessage(unicodeText.toString());

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
			// android.os.Debug.waitForDebugger();

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