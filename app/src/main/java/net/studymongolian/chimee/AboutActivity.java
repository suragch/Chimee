package net.studymongolian.chimee;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class AboutActivity extends AppCompatActivity {

	protected static final String SITE_URL = "http://www.studymongolian.net/apps/chimee/zh/";
	protected static final String CONTACT_URL = "http://www.studymongolian.net/contact-zh/";

    String appName;
    String appVersion = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		setupToolbar();
		setAppNameAndVersion();
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

    private void setAppNameAndVersion() {
        TextView nameAndVersion = findViewById(R.id.tv_app_name_and_version);
        appName = getString(R.string.app_name);
        appVersion = "";
        try {
            appVersion = getApplicationContext().getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String formattedString = getString(R.string.about_chimee_version, appName, appVersion);
        nameAndVersion.setText(formattedString);
    }

	public void onContactClick(View view) {
        // Open contact page in a browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONTACT_URL));
        startActivity(browserIntent);
	}

    public void onUpdatesClick(View view) {
        // Open app site in a browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SITE_URL));
        startActivity(browserIntent);
    }

    public void onShareClick(View view) {
	    String shareString = appName + " " + appVersion + "\n" + SITE_URL;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareString);
        startActivity(Intent.createChooser(intent, ""));
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
