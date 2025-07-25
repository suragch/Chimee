package net.studymongolian.chimee;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

public class RecommendedAppsActivity extends BaseActivity {

    protected static final String BAINU_URL = "http://bainu.com/";
    protected static final String DELEHI_URL = "http://www.delehi.com/cn/";
    protected static final String MENKSOFT_URL = "http://www.menksoft.com/site/alias__menkcms/2828/Default.aspx";

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
