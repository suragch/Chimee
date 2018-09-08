package net.studymongolian.chimee;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import net.studymongolian.mongollibrary.MongolTextView;
import net.studymongolian.mongollibrary.MongolToast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class ReaderActivity extends AppCompatActivity {

    MongolTextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        setupToolbar();
        initTextView();
        handleIntent();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initTextView() {
        textView = findViewById(R.id.mtv_content);
    }

    private void handleIntent() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            tellUserThatCouldntOpenFile();
            return;
        }
        String text = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            text = FileUtils.convertStreamToString(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (text == null) {
            tellUserThatCouldntOpenFile();
            return;
        }

        textView.setText(text);
    }

    private void tellUserThatCouldntOpenFile() {
        MongolToast.makeText(this,
                getString(R.string.could_not_open_file),
                MongolToast.LENGTH_SHORT)
                .show();
    }

//    public static String getStringFromInputStream(InputStream stream) throws IOException {
//        int n;
//        char[] buffer = new char[1024 * 4];
//        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
//        StringWriter writer = new StringWriter();
//        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
//        return writer.toString();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miCopy:
                onCopyClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onCopyClick() {
        String text = textView.getText().toString();
        if (TextUtils.isEmpty(text)) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null, text);
        if (clipboard == null) return;
        clipboard.setPrimaryClip(clip);
        MongolToast.makeText(this, getString(R.string.text_copied), MongolToast.LENGTH_SHORT).show();
    }
}
