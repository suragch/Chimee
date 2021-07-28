package net.studymongolian.chimee;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import net.studymongolian.mongollibrary.MongolToast;

import java.io.InputStream;
import java.util.ArrayList;

public class ReaderActivity extends AppCompatActivity {

    private ReaderRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        setupToolbar();
        ArrayList<CharSequence> paragraphLines = getTextFromIntent();
        setupRecyclerView(paragraphLines);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private ArrayList<CharSequence> getTextFromIntent() {
        Uri uri = getIntent().getData();
        ArrayList<CharSequence> text = new ArrayList<>();
        if (uri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                text = FileUtils.convertStreamToStringArray(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
                tellUserThatCouldntOpenFile();
            }
        } else {
            tellUserThatCouldntOpenFile();
        }
        return text;
    }

    private void tellUserThatCouldntOpenFile() {
        MongolToast.makeText(this,
                getString(R.string.could_not_open_file),
                MongolToast.LENGTH_SHORT)
                .show();
    }

    private void setupRecyclerView(ArrayList<CharSequence> paragraphLines) {
        RecyclerView recyclerView = findViewById(R.id.rv_reader);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new ReaderRvAdapter(this, paragraphLines);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.miCopy) {
            onCopyClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCopyClick() {
        String text = adapter.extractFullText();
        if (TextUtils.isEmpty(text)) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null, text);
        if (clipboard == null) return;
        clipboard.setPrimaryClip(clip);
        MongolToast.makeText(this, getString(R.string.text_copied), MongolToast.LENGTH_SHORT).show();
    }
}
