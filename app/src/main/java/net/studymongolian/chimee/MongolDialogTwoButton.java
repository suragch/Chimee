package net.studymongolian.chimee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * To use this dialog, send a TITLE (optional), MESSAGE, BUTTON_TOP_TEXT (optional), and
 * BUTTON_BOTTOM_TEXT (optional) strings in an intent.
 * 
 */
public class MongolDialogTwoButton extends Activity {

	public static final String TITLE = "title";
	public static final String MESSAGE = "message";
	public static final String BUTTON_TOP_TEXT = "button1";
	public static final String BUTTON_BOTTOM_TEXT = "button2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_two_button);

		String title;
		String message;
		String buttonTop;
		String buttonBottom;

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
		if (intent.hasExtra(BUTTON_TOP_TEXT)) {
			buttonTop = intent.getExtras().getString(BUTTON_TOP_TEXT);
		} else {
			buttonTop = getResources().getString(R.string.dialog_top_button_default);
		}
		if (intent.hasExtra(BUTTON_BOTTOM_TEXT)) {
			buttonBottom = intent.getExtras().getString(BUTTON_BOTTOM_TEXT);
		} else {
			buttonBottom = getResources().getString(R.string.dialog_bottom_button_default);
		}

		// Set typeface
		TextView tvTitle = (TextView) findViewById(R.id.tvDialogTitle);
		tvTitle.setText(title);
		TextView tvMessage = (TextView) findViewById(R.id.tvDialogMessage);
		tvMessage.setText(message);
		TextView tvButtonTop = (TextView) findViewById(R.id.tvDialogButtonTop);
		tvButtonTop.setText(buttonTop);
		TextView tvButtonBottom = (TextView) findViewById(R.id.tvDialogButtonBottom);
		tvButtonBottom.setText(buttonBottom);

		// OnClick listeners
		RelativeLayout rlButtonTop = (RelativeLayout) findViewById(R.id.rlDialogButtonTop);
		rlButtonTop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v != null) {
					// send notice to delete
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				}
			}
		});
		RelativeLayout rlButtonBottom = (RelativeLayout) findViewById(R.id.rlDialogButtonBottom);
		rlButtonBottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v != null) {
					// send notice to cancel
					Intent returnIntent = new Intent();
					setResult(RESULT_CANCELED, returnIntent);
					finish();
				}
			}
		});

	}
}