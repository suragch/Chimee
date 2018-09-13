package net.studymongolian.chimee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.studymongolian.mongollibrary.MongolCode;
import net.studymongolian.mongollibrary.MongolFont;

import java.text.BreakIterator;

public class CodeConverterDetailsActivity extends AppCompatActivity {

    static final String DETAILS_TEXT_KEY = "details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_converter_details);

        setupToolbar();
        setupTextView();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTextView() {
        TextView textView = findViewById(R.id.textView);
        textView.setTypeface(MongolFont.get(MongolFont.QAGAN, this));

        String text = getTextFromIntent();
        String renderedText = renderText(text);
        textView.setText(renderedText);
    }

    private String getTextFromIntent() {
        Intent intent = getIntent();
        String text = intent.getStringExtra(DETAILS_TEXT_KEY);
        return (text == null) ? "" : text;
    }

    private String renderText(String text) {
        StringBuilder builder = new StringBuilder();
        BreakIterator boundary = BreakIterator.getLineInstance();
        boundary.setText(text);
        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; end = boundary.next()) {
            String substring = text.substring(start, end).trim();

            String rendered = MongolCode.INSTANCE.unicodeToMenksoft(substring);
            builder.append(rendered).append('\n');

            String code = getCode(substring);
            builder.append(code).append('\n');
            builder.append('\n');

            start = end;
        }
        return builder.toString();
    }

    private String getCode(String substring) {
        StringBuilder builder = new StringBuilder();
        for (char character : substring.toCharArray()) {
            String hexValue = String.format("%x", (int) character);
            builder.append(hexValue).append(' ');
        }
        return builder.toString();
    }
}
