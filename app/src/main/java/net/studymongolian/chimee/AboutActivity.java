package net.studymongolian.chimee;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

	protected static final String SITE_URL = "http://www.studymongolian.net/apps/chimee/zh/";
	protected static final String CONTACT_URL = "http://www.studymongolian.net/contact-zh/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		setupToolbar();

        TextView nameAndVersion = findViewById(R.id.aboutAppTitle);
		String appName = getResources().getString(R.string.app_name);
		String appVersion = "";
		try {
			appVersion = getApplicationContext().getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		nameAndVersion.setText(appName + " " + appVersion);
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

	public void siteClick(View v) {
		// Open app site in a browser
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_URL));
		startActivity(browserIntent);
	}

	public void contactClick(View v) {
		// Open contact page in a browser
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONTACT_URL));
		startActivity(browserIntent);
	}

}
