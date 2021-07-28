package net.studymongolian.chimee;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.studymongolian.mongollibrary.MongolCode;

import java.lang.ref.WeakReference;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

public class CodeConverterDetailsActivity extends AppCompatActivity {

    static final String DETAILS_TEXT_KEY = "details";

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_converter_details);

        setupToolbar();
        setupRecyclerView();
        new LoadTextFromIntent(this).execute();
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

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.rv_reader);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
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

    private static ArrayList<CharSequence> getTextFromIntent(Intent intent) {
        ArrayList<CharSequence> renderedParagraphs = new ArrayList<>();
        ArrayList<CharSequence> paragraphs = intent.getCharSequenceArrayListExtra(DETAILS_TEXT_KEY);
        if (paragraphs == null || paragraphs.size() == 0)
            return renderedParagraphs;
        for (CharSequence paragraph : paragraphs) {
            List<String> renderedText = renderText(paragraph.toString());
            renderedParagraphs.addAll(renderedText);
        }
        return renderedParagraphs;
    }

    private static List<String> renderText(String text) {
        List<String> parts = new ArrayList<>();
        BreakIterator boundary = BreakIterator.getLineInstance();
        boundary.setText(text);
        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; end = boundary.next()) {
            String substring = text.substring(start, end).trim();

            String rendered = MongolCode.INSTANCE.unicodeToMenksoft(substring);

            String code = getCode(substring);
            String item = rendered + "\n" + code + "\n";
            parts.add(item);

            start = end;
        }
        return parts;
    }

    private static String getCode(String substring) {
        StringBuilder builder = new StringBuilder();
        for (char character : substring.toCharArray()) {
            String hexValue = String.format("%x", (int) character);
            builder.append(hexValue).append(' ');
        }
        return builder.toString();
    }

    private static class LoadTextFromIntent extends AsyncTask<Uri, Void, ArrayList<CharSequence>> {

        private final WeakReference<CodeConverterDetailsActivity> activityReference;

        LoadTextFromIntent(CodeConverterDetailsActivity activityContext) {
            activityReference = new WeakReference<>(activityContext);
        }

        @Override
        protected ArrayList<CharSequence> doInBackground(Uri... params) {
            CodeConverterDetailsActivity activity = activityReference.get();
            if (activity == null) return null;
            return getTextFromIntent(activity.getIntent());
        }

        @Override
        protected void onPostExecute(ArrayList<CharSequence> paragraphs) {
            CodeConverterDetailsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (paragraphs == null) return;
            ReaderRvAdapter adapter = new ReaderRvAdapter(activity, paragraphs);
            activity.recyclerView.setAdapter(adapter);
        }

    }
}
