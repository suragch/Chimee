package net.studymongolian.chimee;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

	protected static final String PREFS_NAME = "MyPrefsFile";
	protected static final String FONT_KEY = "font";
	protected static final String FONT_WHITE = "fontWhite";
	protected static final String FONT_WRITING = "fontWriting";
	protected static final String FONT_ART = "fontArt";
	protected static final String FONT_TITLE = "fontTitle";
	protected static final String FONT_DEFAULT = FONT_WHITE;

	protected static final String MONGOLIAN_KEYBOARD_KEY = "mongolKeyboard";
	protected static final String MONGOLIAN_AEIOU_KEYBOARD = "aeiouKeyboard";
	protected static final String MONGOLIAN_QWERTY_KEYBOARD = "qwertyKeyboard";
	protected static final String MONGOLIAN_KEYBOARD_DEFAULT = MONGOLIAN_AEIOU_KEYBOARD;
	protected static final String BGCOLOR_KEY = "bgColor";
	protected static final int BGCOLOR_DEFAULT = Color.WHITE;
	protected static final String TEXTCOLOR_KEY = "textColor";
	protected static final int TEXTCOLOR_DEFAULT = Color.BLACK;

    protected static final String DRAFT_KEY = "draft"; // Unicode text in input window when closed
    protected static final String DRAFT_DEFAULT = "";
    protected static final String CURSOR_POSITION_KEY = "cursorPosition";
    protected static final int CURSOR_POSITION_DEFAULT = 0;


	private static final int KEYBOARD_REQUEST = 1;
	private static final int COLOR_REQUEST = 2;
	private static final int FONT_REQUEST = 3;

	TextView tvColorText;
	TextView tvFontSetting;
	FrameLayout flColorBox;
	TextView tvKeyboard;
	SharedPreferences settings;
	Intent returnInfoIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		// setup toolbar
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		tvKeyboard = (TextView) findViewById(R.id.tvSettingsKeyboard);

		// Get preferences and update settings display
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		// Color
		int background = settings.getInt(BGCOLOR_KEY, BGCOLOR_DEFAULT);
		int text = settings.getInt(TEXTCOLOR_KEY, TEXTCOLOR_DEFAULT);
		flColorBox = (FrameLayout) findViewById(R.id.flSettingsColorSampleBox);
		flColorBox.setBackgroundColor(background);
		tvColorText = (TextView) findViewById(R.id.tvSettingsColorSampleU);
		tvColorText.setTextColor(text);
		
		// Font
		String font = settings.getString(FONT_KEY, FONT_DEFAULT);
		tvFontSetting = (TextView) findViewById(R.id.tvSettingsFontSelected);
		tvFontSetting.setText(getResources().getString(R.string.settings_font_detail));
//		Typeface tf = FontCache.get(font, getApplicationContext());
//        if(tf != null) {
//        	tvFontSetting.setTypeface(tf);
//        }
		
		// Keyboard type
		String userKeyboard = settings
				.getString(MONGOLIAN_KEYBOARD_KEY, MONGOLIAN_KEYBOARD_DEFAULT);
		if (userKeyboard.equals(MONGOLIAN_AEIOU_KEYBOARD)) {
			tvKeyboard.setText(getResources().getString(R.string.keyboard_aeiou_short));
		} else {
			tvKeyboard.setText(getResources().getString(R.string.keyboard_qwerty_short));
		}

		// initialize things that need to be updated on result
		returnInfoIntent = new Intent();
		returnInfoIntent.putExtra("settingsHaveChanged", false);

	}

	public void finishedClick(View v) {
		finish();
	}

	public void settingsColorClick(View v) {
		// Choose the color for the message text and background
		Intent intent = new Intent(v.getContext(), ColorChooserActivity.class);
		startActivityForResult(intent, COLOR_REQUEST);
	}

	public void settingsFontClick(View v) {
//		// Bring up a dialog box with font size choices
//		Intent intent = new Intent(v.getContext(), FontChooserDialog.class);
//		startActivityForResult(intent, FONT_REQUEST);
//		setResult(RESULT_OK, returnInfoIntent);
	}

	public void settingsKeyboardClick(View v) {
//		// Bring up a dialog box with font size choices
//		Intent intent = new Intent(v.getContext(), SettingsKeyboardChooserDialog.class);
//		startActivityForResult(intent, KEYBOARD_REQUEST);
//		setResult(RESULT_OK, returnInfoIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == KEYBOARD_REQUEST) {
			if (resultCode == RESULT_OK) {

				boolean keyboardChanged = true;
				returnInfoIntent.putExtra("keyboardResult", keyboardChanged);

				// get choice
				String chosenKeyboard = data.getStringExtra("result");

				// save settings
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(MONGOLIAN_KEYBOARD_KEY, chosenKeyboard);
				editor.commit();

				// update display
				if (chosenKeyboard.equals(MONGOLIAN_AEIOU_KEYBOARD)) {
					tvKeyboard.setText(getResources().getString(
							R.string.keyboard_aeiou_short));
				} else {
					tvKeyboard.setText(getResources().getString(
							R.string.keyboard_qwerty_short));
				}

				// change keyboard on return
				setResult(RESULT_OK, returnInfoIntent);
			}
		} else if (requestCode == COLOR_REQUEST) {
			if (resultCode == RESULT_OK) {

				// get colors
				int backgroundColor = data.getExtras().getInt("resultBackground");
				int textColor = data.getExtras().getInt("resultText");

				// Save to settings
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt(BGCOLOR_KEY, backgroundColor);
				editor.putInt(TEXTCOLOR_KEY, textColor);
				editor.commit();

				// Update settings view
				flColorBox.setBackgroundColor(backgroundColor);
				tvColorText.setTextColor(textColor);

				// send message to main view to update that
				boolean colorChanged = true;
				returnInfoIntent.putExtra("colorResult", colorChanged);
				setResult(RESULT_OK, returnInfoIntent);

			}
		} else if (requestCode == FONT_REQUEST) {
			if (resultCode == RESULT_OK) {

				// get font
				String font = data.getStringExtra("resultFont");

				// Save to settings
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(FONT_KEY, font);
				editor.commit();

				// Update settings view
//				Typeface tf = FontCache.get(font, getApplicationContext());
//		        if(tf != null) {
//		        	tvFontSetting.setTypeface(tf);
//		        }

				// send message to main view to update that
				boolean fontChanged = true;
				returnInfoIntent.putExtra("fontResult", fontChanged);
				setResult(RESULT_OK, returnInfoIntent);

			}
		}
	}

}