package net.studymongolian.chimee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * To use this dialog, send a "title" (optional, default=nitice), "message", and "button" (optional,
 * default=OK) strings in an intent.
 * 
 */
public class MongolDialogOneButton extends Activity {
	
	public static final String TITLE = "title";
	public static final String MESSAGE = "message";
	public static final String BUTTON_TEXT = "button";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_one_button);

		String title;
		String message;
		String button;

		// Get strings from Intent
		Intent intent = getIntent();
		if (intent.hasExtra(TITLE)) {
			title = intent.getExtras().getString(TITLE);

		} else {
			title = getResources().getString(R.string.dialog_title_default);
		}
		if (intent.hasExtra(MESSAGE)) {
			message = intent.getExtras().getString(MESSAGE);
		} else {
			message = "";
		}
		if (intent.hasExtra(BUTTON_TEXT)) {
			button = intent.getExtras().getString(BUTTON_TEXT);
		} else {
			button = getResources().getString(R.string.dialog_one_button_default);
		}

		// render strings
		//MongolUnicodeRenderer converter = new MongolUnicodeRenderer();
		//title = converter.unicodeToGlyphs(title);
		//message = converter.unicodeToGlyphs(message);
		//button = converter.unicodeToGlyphs(button);

		// Set typeface
		TextView tvTitle = (TextView) findViewById(R.id.tvDialogTitle);
		tvTitle.setText(title);
		TextView tvMessage = (TextView) findViewById(R.id.tvDialogMessage);
		tvMessage.setText(message);
		TextView tvButton = (TextView) findViewById(R.id.tvDialogButton);
		tvButton.setText(button);

		// OnClick listener
		RelativeLayout rlButton = (RelativeLayout) findViewById(R.id.rlDialogButton);
		rlButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v != null) {

					finish();
				}
			}
		});

	}
}