package net.studymongolian.chimee;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import net.studymongolian.mongollibrary.MongolEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmojiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);

        setupToolbar();
        addCurrentEmojiList();
        showKeyboard();
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

    private void addCurrentEmojiList() {
//        MongolEditText met = findViewById(R.id.met_emoji_set);
//        met.setText(KeyboardEmoji.EMOJI_LIST);
        EditText et = findViewById(R.id.et_emoji_set);
        et.setText(KeyboardEmoji.EMOJI_LIST);
    }

    private List<String> getEmojis() {
        String[] splitStr = KeyboardEmoji.EMOJI_LIST.split(" ");
        return new ArrayList<>(Arrays.asList(splitStr));
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
