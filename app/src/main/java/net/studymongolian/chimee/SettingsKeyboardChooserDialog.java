package net.studymongolian.chimee;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class SettingsKeyboardChooserDialog extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_settings_keyboard_chooser);

		// Get radio buttons
		final RadioButton rbAeiou = (RadioButton) findViewById(R.id.rbSettingsKeyboardAeiou);
		final RadioButton rbQwerty = (RadioButton) findViewById(R.id.rbSettingsKeyboardQwerty);

		// Show the current radio button settings
		SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
		String keyboard = settings.getString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
				SettingsActivity.MONGOLIAN_KEYBOARD_DEFAULT);
		if (keyboard.equals(SettingsActivity.MONGOLIAN_AEIOU_KEYBOARD)) {
			rbAeiou.setChecked(true);
			rbQwerty.setChecked(false);
		} else {
			rbAeiou.setChecked(false);
			rbQwerty.setChecked(true);
		}

		// set the click listeners
		RelativeLayout rlAeiou = (RelativeLayout) findViewById(R.id.rlSettingsDialogKeyboardAeiou);
		rlAeiou.setOnClickListener(this);
		RelativeLayout rlQwerty = (RelativeLayout) findViewById(R.id.rlSettingsDialogKeyboardQwerty);
		rlQwerty.setOnClickListener(this);

	}



	@Override
	public void onClick(View view) {
		
		Intent returnIntent = new Intent();
		switch (view.getId()) {
		case R.id.rlSettingsDialogKeyboardAeiou:

			returnIntent.putExtra("result", SettingsActivity.MONGOLIAN_AEIOU_KEYBOARD);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;
		case R.id.rlSettingsDialogKeyboardQwerty:

			returnIntent.putExtra("result", SettingsActivity.MONGOLIAN_QWERTY_KEYBOARD);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;
		default:

			break;
		}
		
	}

}
