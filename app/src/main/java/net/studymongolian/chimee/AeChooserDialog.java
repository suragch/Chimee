package net.studymongolian.chimee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

// This dialog is for choosing the A or E vowel in ambiguous words (like qim-a or xin-e)
// Just using for the Qwerty keyboard for now. Aeiou keyboard assumes E.
public class AeChooserDialog extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_ae_chooser);

		// Initialize views
		RelativeLayout rlA = (RelativeLayout) findViewById(R.id.rlDialogAeChooserA);
		RelativeLayout rlE = (RelativeLayout) findViewById(R.id.rlDialogAeChooserE);

		// set button listeners
		rlA.setOnClickListener(this);
		rlE.setOnClickListener(this);


	}



	@Override
	public void onClick(View v) {
		Intent returnIntent = new Intent();

		switch (v.getId()) {
		case R.id.rlDialogAeChooserA:

			returnIntent.putExtra("result", MongolUnicodeRenderer.UNI_A);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;
		case R.id.rlDialogAeChooserE:

			returnIntent.putExtra("result", MongolUnicodeRenderer.UNI_E);
			setResult(RESULT_OK, returnIntent);
			finish();

			break;
		default:

			break;
		}

	}

}
