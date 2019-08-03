package net.studymongolian.chimee;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

public class RecommendedAppsActivity extends AppCompatActivity {

    protected static final String BAINU_URL = "http://bainu.com/";
    protected static final String DELEHI_URL = "http://www.delehi.com/cn/";
    protected static final String MENKSOFT_URL = "http://www.menksoft.com/site/alias__menkcms/2828/Default.aspx";
    protected static final String OLOOL_URL = "http://mgl.olool.com/";
    protected static final String GUNGUR_URL = "http://gungursoft.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_apps);
        setupToolbar();
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

    public void onBainuClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BAINU_URL));
        startActivity(browserIntent);
    }

    public void onDelehiClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(DELEHI_URL));
        startActivity(browserIntent);
    }

    public void onMenksoftClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MENKSOFT_URL));
        startActivity(browserIntent);
    }

    public void onOloolClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(OLOOL_URL));
        startActivity(browserIntent);
    }

    public void onGungurClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GUNGUR_URL));
        startActivity(browserIntent);
    }
}
