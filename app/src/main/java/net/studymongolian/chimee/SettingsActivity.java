package net.studymongolian.chimee;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import net.studymongolian.mongollibrary.MongolFont;
import net.studymongolian.mongollibrary.MongolToast;

public class SettingsActivity extends AppCompatActivity {

	static final String PREFS_NAME = "MyPrefsFile";
	static final String FONT_KEY = "font";
	public static final String FONT_QAGAN = MongolFont.QAGAN;                    // Normal
	public static final String FONT_GARQAG = "fonts/MenksoftGarqag.ttf";         // Title
	public static final String FONT_HARA = "fonts/MenksoftHara.ttf";             // Bold
	public static final String FONT_SCNIN = "fonts/MenksoftScnin.ttf";           // News
	public static final String FONT_HAWANG = "fonts/MenksoftHawang.ttf";         // Handwriting
	public static final String FONT_QIMED = "fonts/MenksoftQimed.ttf";           // Handwriting pen
	public static final String FONT_NARIN = "fonts/MenksoftNarin.ttf";           // Thin
	public static final String FONT_MCDVNBAR = "fonts/MenksoftMcdvnbar.ttf";     // Wood carving
	public static final String FONT_AMGLANG = "fonts/MAM8102.ttf";               // Brush
	public static final String FONT_SIDAM = "fonts/MBN8102.ttf";                 // Fat round
	public static final String FONT_QINGMING = "fonts/MQI8102.ttf";              // Qing Ming style
	public static final String FONT_ONQA_HARA = "fonts/MTH8102.ttf";             // Thick stem
	public static final String FONT_SVGVNAG = "fonts/MSO8102.ttf";               // Thick stem thin lines
	public static final String FONT_SVLBIYA = "fonts/MBJ8102.ttf";               // Double stem
	public static final String FONT_JCLGQ = "fonts/MenksoftJclgq.ttf";           // Computer
	protected static final String FONT_DEFAULT = FONT_QAGAN;

	protected static final String MONGOLIAN_KEYBOARD_KEY = "mongolKeyboard";
	protected static final String MONGOLIAN_AEIOU_KEYBOARD = "aeiouKeyboard";
	protected static final String MONGOLIAN_QWERTY_KEYBOARD = "qwertyKeyboard";
	protected static final String MONGOLIAN_KEYBOARD_DEFAULT = MONGOLIAN_AEIOU_KEYBOARD;
	protected static final String BGCOLOR_KEY = "bgColor";
	protected static final int BGCOLOR_DEFAULT = Color.WHITE;
	protected static final String TEXTCOLOR_KEY = "textColor";
	protected static final int TEXTCOLOR_DEFAULT = Color.BLACK;

	protected static final String DRAFT_KEY = "draft"; // Unicode text in input window when closed
	protected static final String DRAFT_DEFAULT = "";
	protected static final String CURSOR_POSITION_KEY = "cursorPosition";
	protected static final int CURSOR_POSITION_DEFAULT = 0;
	public static final String SHOW_BAINU_BUTTON_KEY = "show_bainu";


    private static final int HISTORY_REQUEST = 0;

	static final String SETTINGS_RETURN_ACTION_KEY = "return_key";
	//static final String SETTINGS_ACTION_EDIT_HISTORY_MESSAGE_KEY = "edit_history";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

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

    public void onHistoryClick(View view) {
	    Intent intent = new Intent(this, HistoryActivity.class);
	    startActivityForResult(intent, HISTORY_REQUEST);
    }

    public void onKeyboardWordsClick(View view) {
        MongolToast.makeText(this, R.string.settings_keyboard_words, MongolToast.LENGTH_SHORT).show();

    }

    public void onCodeConverterClick(View view) {
        MongolToast.makeText(this, R.string.settings_code_converter, MongolToast.LENGTH_SHORT).show();

    }

    public void onHelpClick(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void onAboutClick(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case HISTORY_REQUEST:
                onHistoryResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onHistoryResult(int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	    String message = data.getStringExtra(HistoryActivity.RESULT_STRING_KEY);
	    if (TextUtils.isEmpty(message)) return;
        Intent returnIntent = new Intent();
        returnIntent.putExtra(HistoryActivity.RESULT_STRING_KEY, message);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}