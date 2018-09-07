package net.studymongolian.chimee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolInputMethodManager;
import net.studymongolian.mongollibrary.MongolToast;

import static net.studymongolian.chimee.AddEditFavoritesActivity.MESSAGE_ADDED_KEY;

public class SaveActivity extends AppCompatActivity
        implements ImeDataSourceHelper.DataSourceHelperListener {

    private MenuItem saveButton;
    static final String TEXT_KEY = "text";

    CustomImeContainer imeContainer;
    MongolEditText metFileName;
    String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        setupToolbar();
        setupKeyboard();
        setupMongolEditText();
        loadInfoFromIntent();
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

    private void setupMongolEditText() {
        MongolEditText editText = findViewById(R.id.met_file_name);
        editText.requestFocus();
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean buttonIsVisible = saveButton.isVisible();
                boolean fileNameIsEmpty = TextUtils.isEmpty(s);
                if (buttonIsVisible && fileNameIsEmpty)
                    saveButton.setVisible(false);
                else if (!buttonIsVisible && !fileNameIsEmpty)
                    saveButton.setVisible(true);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void setupKeyboard() {
        metFileName = findViewById(R.id.met_file_name);
        imeContainer = findViewById(R.id.keyboard_container);

        MongolInputMethodManager mimm = new MongolInputMethodManager();
        mimm.addEditor(metFileName);
        mimm.setIme(imeContainer);
        imeContainer.setDataSource(new ImeDataSourceHelper(this));
        getSavedKeyboard(imeContainer);
    }

    private void getSavedKeyboard(CustomImeContainer imeContainer) {
        // TODO if user phone is lower than ANDROID 6.0 then use Latin keyboard

        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        String userKeyboard = settings.getString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
                SettingsActivity.MONGOLIAN_KEYBOARD_DEFAULT);
        if (userKeyboard.equals(SettingsActivity.MONGOLIAN_QWERTY_KEYBOARD)) {
            imeContainer.requestNewKeyboard(CustomImeContainer.MONGOL_QWERTY_KEYBOARD_INDEX);
        }
    }

    private void loadInfoFromIntent() {
        Intent intent = getIntent();
        mText = intent.getStringExtra(TEXT_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_file_menu, menu);
        saveButton = menu.findItem(R.id.miSaveFile);
        saveButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miSaveFile:
                onSaveFileClick();
                return true;
            case android.R.id.home:
                handleUserFinishing();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        handleUserFinishing();
    }

    private void handleUserFinishing () {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void onSaveFileClick() {
//        if (TextUtils.isEmpty(metFileName.getText())) {
//            MongolToast.makeText(this,
//                    getString(R.string.filename_cant_be_empty), MongolToast.LENGTH_LONG).show();
//            return;
//        }

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public CustomImeContainer getImeContainer() {
        return imeContainer;
    }

    @Override
    public Context getContext() {
        return this;
    }
}
