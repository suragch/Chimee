package net.studymongolian.chimee;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import net.studymongolian.mongollibrary.MongolCode;
import net.studymongolian.mongollibrary.MongolToast;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CodeConverterActivity extends AppCompatActivity {

    private static final int OPEN_FILE_REQUEST = 0;
    private static final int SAVE_REQUEST = 1;

    FrameLayout convertButton;
    FrameLayout copyButton;
    FrameLayout detailsButton;
    private int mMenksoftColor;
    MenuItem mSaveMenuItem;
    private ReaderRvAdapter adapter;
    private ArrayList<CharSequence> mParagraphs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_converter);

        setupToolbar();
        initViews();
        hideExtraButtons();
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

    private void initViews() {
        //contentWindow = findViewById(R.id.mtv_convert_content);
        convertButton = findViewById(R.id.flConvert);
        copyButton = findViewById(R.id.flCopy);
        detailsButton = findViewById(R.id.flDetails);
        mMenksoftColor = getResources().getColor(R.color.converter_menksoft);
        mParagraphs = new ArrayList<>();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_content);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new ReaderRvAdapter(this, mParagraphs);
        adapter.setTextColor(getResources().getColor(R.color.converter_unicode));
        recyclerView.setAdapter(adapter);
    }

    private void hideExtraButtons() {
        convertButton.setVisibility(View.INVISIBLE);
        copyButton.setVisibility(View.INVISIBLE);
        detailsButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.converter_menu, menu);
        mSaveMenuItem = menu.findItem(R.id.action_save);
        mSaveMenuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_open:
                openFile();
                return true;
            case R.id.action_save:
                saveToFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openFile() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("files/*");
        intent = Intent.createChooser(chooseFile, ""); // TODO add mongolian?
        startActivityForResult(intent, OPEN_FILE_REQUEST);
    }

    private void saveToFile() {
        String text = getText();
        Intent intent = new Intent(this, SaveActivity.class);
        intent.putExtra(SaveActivity.TEXT_KEY, text);
        startActivityForResult(intent, SAVE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case OPEN_FILE_REQUEST:
                onOpenFileResult(resultCode, data);
                break;
            case SAVE_REQUEST:
                onSaveFileResult(resultCode);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onOpenFileResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Uri uri = data.getData();
        new OpenFile(this, mMenksoftColor).execute(uri);
    }

    private void onSaveFileResult(int resultCode) {
        if (resultCode != RESULT_OK) return;
        mSaveMenuItem.setVisible(false);
    }

    public void onPasteClick(View view) {
        String clipboardText = getClipboardText();
        if (TextUtils.isEmpty(clipboardText)) {
            notifyUserThatClipboardIsEmpty();
            return;
        }
        //SpannableStringBuilder formattedText = colorTextAccordingToCoding(clipboardText);
        ArrayList<CharSequence> paragraphs = convertStringToArray(clipboardText, mMenksoftColor);
        setText(paragraphs);

        showConvertAndDetailButtons();
    }

    private void showConvertAndDetailButtons() {
        convertButton.setVisibility(View.VISIBLE);
        detailsButton.setVisibility(View.VISIBLE);
    }

    public void onConvertClick(View view) {
        new ConvertText(this, mParagraphs, mMenksoftColor).execute();
    }

    public void onCopyClick(View view) {
        String text = getText();
        if (TextUtils.isEmpty(text)) return;
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(null, text);
        if (clipboard == null) return;
        clipboard.setPrimaryClip(clip);
        notifyUserThatTextCopied();
    }

    private void notifyUserThatTextCopied() {
        MongolToast.makeText(this,
                getString(R.string.text_copied),
                MongolToast.LENGTH_SHORT)
                .show();
    }

    private void notifyUserThatClipboardIsEmpty() {
        MongolToast.makeText(this,
                getString(R.string.converter_clipboard_empty_notice),
                MongolToast.LENGTH_LONG)
                .show();
    }

    private String getClipboardText() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return "";
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null) return "";
        ClipData.Item item = clip.getItemAt(0);
        if (item == null) return "";
        return item.getText().toString();
    }

    private static SpannableStringBuilder colorTextAccordingToCoding(String text, int menksoftColor) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);

        int start = 0;
        int end;
        boolean inMenksoftSpan = false;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char character = text.charAt(i);
            if (character == ' ') continue;
            if (MongolCode.isMenksoft(character)) {
                if (!inMenksoftSpan) {
                    start = i;
                    inMenksoftSpan = true;
                }
            } else {
                if (inMenksoftSpan) {
                    end = i;
                    addMenksoftColorSpan(spannable, start, end, menksoftColor);
                    inMenksoftSpan = false;
                }
            }
        }

        if (inMenksoftSpan) {
            end = length;
            addMenksoftColorSpan(spannable, start, end, menksoftColor);
        }

        return spannable;
    }

    private static void addMenksoftColorSpan(SpannableStringBuilder text, int start, int end, int color) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        text.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    public void onDetailsClick(View view) {
        Intent intent = new Intent(this, CodeConverterDetailsActivity.class);
        intent.putCharSequenceArrayListExtra(CodeConverterDetailsActivity.DETAILS_TEXT_KEY, mParagraphs);
        startActivity(intent);
    }

    public void setText(ArrayList<CharSequence> paragraphs) {
        mParagraphs.clear();
        if (paragraphs == null || paragraphs.size() == 0) return;
        mParagraphs.addAll(paragraphs);
        adapter.notifyDataSetChanged();
    }

    public static ArrayList<CharSequence> convertStringToArray(String text, int menksoftColor) {
        ArrayList<CharSequence> array = new ArrayList<>();
        String myArray[] = TextUtils.split(text,"\n");
        for (String paragraph : myArray) {
            SpannableStringBuilder formatted = colorTextAccordingToCoding(paragraph, menksoftColor);
            array.add(formatted);
        }
        return array;
    }

    String getText() {
        StringBuilder builder = new StringBuilder();
        for (CharSequence paragraph : mParagraphs) {
            builder.append(paragraph).append("\n");
        }
        return builder.toString();
    }

    private static class OpenFile extends AsyncTask<Uri, Void, ArrayList<CharSequence>> {

        private WeakReference<CodeConverterActivity> activityReference;
        private int mMenksoftColor;

        OpenFile(CodeConverterActivity activityContext, int menksoftColor) {
            activityReference = new WeakReference<>(activityContext);
            mMenksoftColor = menksoftColor;
        }

        @Override
        protected ArrayList<CharSequence> doInBackground(Uri... params) {


            CodeConverterActivity activity = activityReference.get();
            if (activity == null) return null;
            Uri importFile = params[0];
            if (importFile == null) return null;

            String fileText;
            try {
                InputStream inputStream = activity.getContentResolver().openInputStream(importFile);
                fileText = FileUtils.convertStreamToString(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return convertStringToArray(fileText, mMenksoftColor);
        }

        @Override
        protected void onPostExecute(ArrayList<CharSequence> paragraphs) {
            CodeConverterActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (paragraphs == null) return;
            activity.setText(paragraphs);
            activity.showConvertAndDetailButtons();
        }

    }

    private static class ConvertText extends AsyncTask<Void, Void, ArrayList<CharSequence>> {

        private WeakReference<CodeConverterActivity> activityReference;
        private ArrayList<CharSequence> mParagraphs;
        private int mMenksoftColor;

        ConvertText(CodeConverterActivity activityContext, ArrayList<CharSequence> paragraphs, int menksoftColor) {
            activityReference = new WeakReference<>(activityContext);
            mParagraphs = paragraphs;
            mMenksoftColor = menksoftColor;
        }

        @Override
        protected ArrayList<CharSequence> doInBackground(Void... params) {


            CodeConverterActivity activity = activityReference.get();
            if (activity == null) return null;
            ArrayList<CharSequence> convertedParagraphs = new ArrayList<>();
            for (CharSequence paragraph : mParagraphs) {
                String converted = convertText(paragraph.toString());
                SpannableStringBuilder formatted = colorTextAccordingToCoding(converted, mMenksoftColor);
                convertedParagraphs.add(formatted);
            }
            return convertedParagraphs;
        }

        private String convertText(String text) {
            if (isMenksoft(text)) {
                return MongolCode.INSTANCE.menksoftToUnicode(text);
            } else {
                return MongolCode.INSTANCE.unicodeToMenksoft(text);
            }
        }

        private boolean isMenksoft(String text) {
            for (char character : text.toCharArray()) {
                if (MongolCode.isMenksoft(character))
                    return true;
                if (MongolCode.isMongolian(character))
                    return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(ArrayList<CharSequence> paragraphs) {
            CodeConverterActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (paragraphs == null) return;
            activity.setText(paragraphs);
            activity.copyButton.setVisibility(View.VISIBLE);
            activity.mSaveMenuItem.setVisible(true);
        }

    }
}
