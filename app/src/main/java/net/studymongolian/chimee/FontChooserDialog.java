package net.studymongolian.chimee;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FontChooserDialog extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_font_chooser);
		
//		// Set the fonts
//		TextView tvWhite = (TextView) findViewById(R.id.tvSettingsDialogFontWhite);
//		TextView tvWriting = (TextView) findViewById(R.id.tvSettingsDialogFontWriting);
//		TextView tvArt = (TextView) findViewById(R.id.tvSettingsDialogFontArt);
//		TextView tvTitle = (TextView) findViewById(R.id.tvSettingsDialogFontTitle);
//		Typeface tf = FontCache.get(SettingsActivity.FONT_WHITE, getApplicationContext());
//        if(tf != null) {
//        	tvWhite.setTypeface(tf);
//        }
//        tf = FontCache.get(SettingsActivity.FONT_WRITING, getApplicationContext());
//        if(tf != null) {
//        	tvWriting.setTypeface(tf);
//        }
//        tf = FontCache.get(SettingsActivity.FONT_ART, getApplicationContext());
//        if(tf != null) {
//        	tvArt.setTypeface(tf);
//        }
//        tf = FontCache.get(SettingsActivity.FONT_TITLE, getApplicationContext());
//        if(tf != null) {
//        	tvTitle.setTypeface(tf);
//        }

		// Get radio buttons
		final RadioButton rbWhite = (RadioButton) findViewById(R.id.rbSettingsDialogFontWhite);
		final RadioButton rbWriting = (RadioButton) findViewById(R.id.rbSettingsDialogFontWriting);
		final RadioButton rbArt = (RadioButton) findViewById(R.id.rbSettingsDialogFontArt);
		final RadioButton rbTitle = (RadioButton) findViewById(R.id.rbSettingsDialogFontTitle);

		// Show the current radio button settings
		SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
		String savedFont = settings.getString(SettingsActivity.FONT_KEY,
				SettingsActivity.FONT_DEFAULT);
		if (savedFont.equals(SettingsActivity.FONT_WHITE)) {
			rbWhite.setChecked(true);
			rbWriting.setChecked(false);
			rbArt.setChecked(false);
			rbTitle.setChecked(false);
		} else if (savedFont.equals(SettingsActivity.FONT_WRITING)) {
			rbWhite.setChecked(false);
			rbWriting.setChecked(true);
			rbArt.setChecked(false);
			rbTitle.setChecked(false);
		} else if (savedFont.equals(SettingsActivity.FONT_ART)) {
			rbWhite.setChecked(false);
			rbWriting.setChecked(false);
			rbArt.setChecked(true);
			rbTitle.setChecked(false);
		} else {// FONT_TITLE
			rbWhite.setChecked(false);
			rbWriting.setChecked(false);
			rbArt.setChecked(false);
			rbTitle.setChecked(true);
		}

		// set the click listeners
		RelativeLayout rlWhite = (RelativeLayout) findViewById(R.id.rlSettingsDialogFontWhite);
		rlWhite.setOnClickListener(this);
		RelativeLayout rlWriting = (RelativeLayout) findViewById(R.id.rlSettingsDialogFontWriting);
		rlWriting.setOnClickListener(this);
		RelativeLayout rlArt = (RelativeLayout) findViewById(R.id.rlSettingsDialogFontArt);
		rlArt.setOnClickListener(this);
		RelativeLayout rlTitle = (RelativeLayout) findViewById(R.id.rlSettingsDialogFontTitle);
		rlTitle.setOnClickListener(this);

	}


	@Override
	public void onClick(View view) {
		
		Intent returnIntent = new Intent();
		switch (view.getId()) {
		case R.id.rlSettingsDialogFontWhite:

			returnIntent.putExtra("resultFont", SettingsActivity.FONT_WHITE);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;
		case R.id.rlSettingsDialogFontWriting:

			returnIntent.putExtra("resultFont", SettingsActivity.FONT_WRITING);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;
		case R.id.rlSettingsDialogFontArt:

			returnIntent.putExtra("resultFont", SettingsActivity.FONT_ART);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;
		case R.id.rlSettingsDialogFontTitle:

			returnIntent.putExtra("resultFont", SettingsActivity.FONT_TITLE);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;
		default:

			break;
		}
		
	}

}
