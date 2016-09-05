package net.studymongolian.chimee;

import android.app.Activity;
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

        // setup toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);



        TextView nameAndVersion = (TextView) findViewById(R.id.aboutAppTitle);
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

	public void finishedClick(View v) {
		finish();
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
