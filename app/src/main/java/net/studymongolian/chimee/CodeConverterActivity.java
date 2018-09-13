package net.studymongolian.chimee;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import net.studymongolian.mongollibrary.MongolCode;
import net.studymongolian.mongollibrary.MongolTextView;
import net.studymongolian.mongollibrary.MongolToast;

public class CodeConverterActivity extends AppCompatActivity {

    MongolTextView contentWindow;
    FrameLayout convertButton;
    FrameLayout copyButton;
    FrameLayout detailsButton;
    private int mMenksoftColor;

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
        contentWindow = findViewById(R.id.mtv_convert_content);
        convertButton = findViewById(R.id.flConvert);
        copyButton = findViewById(R.id.flCopy);
        detailsButton = findViewById(R.id.flDetails);
        mMenksoftColor = getResources().getColor(R.color.converter_menksoft);
    }

    private void hideExtraButtons() {
        convertButton.setVisibility(View.INVISIBLE);
        copyButton.setVisibility(View.INVISIBLE);
        detailsButton.setVisibility(View.INVISIBLE);
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

    public void onPasteClick(View view) {
        String clipboardText = getClipboardText();
        if (TextUtils.isEmpty(clipboardText)) {
            notifyUserThatClipboardIsEmpty();
            return;
        }
        SpannableStringBuilder formattedText = colorTextAccordingToCoding(clipboardText);
        contentWindow.setText(formattedText);

        convertButton.setVisibility(View.VISIBLE);
        detailsButton.setVisibility(View.VISIBLE);
    }

    public void onConvertClick(View view) {
        String text = contentWindow.getText().toString();
        String convertedText = convertText(text);
        SpannableStringBuilder formattedText = colorTextAccordingToCoding(convertedText);
        contentWindow.setText(formattedText);
        copyButton.setVisibility(View.VISIBLE);
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


    public void onCopyClick(View view) {
        String text = contentWindow.getText().toString();
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

    private SpannableStringBuilder colorTextAccordingToCoding(String text) {
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
                    addMenksoftColorSpan(spannable, start, end);
                    inMenksoftSpan = false;
                }
            }
        }

        if (inMenksoftSpan) {
            end = length;
            addMenksoftColorSpan(spannable, start, end);
        }

        return spannable;
    }

    private void addMenksoftColorSpan(SpannableStringBuilder text, int start, int end) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mMenksoftColor);
        text.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    public void onDetailsClick(View view) {
        Intent intent = new Intent(this, CodeConverterDetailsActivity.class);
        String text = contentWindow.getText().toString();
        intent.putExtra(CodeConverterDetailsActivity.DETAILS_TEXT_KEY, text);
        startActivity(intent);
    }
}
