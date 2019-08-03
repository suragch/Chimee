package net.studymongolian.chimee;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class HelpActivity extends AppCompatActivity {

	protected static final String HELP_URL = "http://www.studymongolian.net/apps/chimee/zh/chimee-help/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		setupToolbar();
		loadHelpText();
	}

	private void setupToolbar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setTitle("");
		}
	}

	private void loadHelpText() {
		String helpText = readText();
		//MongolTextView textView = findViewById(R.id.tvHelpContent);
		//textView.setText(helpText);
	}

	private String readText() {

		InputStream inputStream = getResources().openRawResource(R.raw.help);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}

	public void finishedClick(View v) {
		finish();
	}

	public void onlineHelpClick(View v) {
		// Open app site in a browser
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HELP_URL));
		startActivity(browserIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
