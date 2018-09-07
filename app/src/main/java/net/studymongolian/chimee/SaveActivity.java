package net.studymongolian.chimee;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

import net.studymongolian.mongollibrary.MongolAlertDialog;
import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolInputMethodManager;
import net.studymongolian.mongollibrary.MongolToast;

import java.lang.ref.WeakReference;

public class SaveActivity extends AppCompatActivity
        implements ImeDataSourceHelper.DataSourceHelperListener {

    private MenuItem saveButton;
    static final String TEXT_KEY = "text";
    public static final int REQUEST_WRITE_STORAGE = 112;
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
        requestWritePermission();
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

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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

    private void requestWritePermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_WRITE_STORAGE);
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

    private void handleUserFinishing() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void onSaveFileClick() {
        String filename = metFileName.getText().toString();
        if (filenameExists(filename)) {
            askUserWhetherToOverwrite(filename);
        } else {
            new SaveFile(this).execute(filename, mText);
        }
    }

    private boolean filenameExists(String filename) {
        return FileUtils.textFileExists(filename);
    }

    private void askUserWhetherToOverwrite(final String filename) {
        MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_confirm_overwrite_file));
        builder.setPositiveButton(getString(R.string.dialog_button_overwrite), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new SaveFile(SaveActivity.this).execute(filename, mText);
            }
        });
        MongolAlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                notifyUserThatTheyCantSaveFileWithoutWritePermission();
                break;
            }
        }
    }

    private void notifyUserThatTheyCantSaveFileWithoutWritePermission() {
        MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(this);
        builder.setMessage(getString(R.string.no_write_file_permission));
        builder.setPositiveButton(getString(R.string.dialog_got_it), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        MongolAlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public CustomImeContainer getImeContainer() {
        return imeContainer;
    }

    @Override
    public Context getContext() {
        return this;
    }

    private static class SaveFile extends AsyncTask<String, Void, Boolean> {

        private WeakReference<SaveActivity> activityReference;

        SaveFile(SaveActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean result;
            Context appContext = activityReference.get();
            if (appContext == null) return false;

            String filename = params[0];
            String text = params[1];

            try {
                result = FileUtils.saveTextFile(appContext, filename, text);
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean exportSuccessful) {

            SaveActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            Intent intent = new Intent();
            if (exportSuccessful) {
                activity.setResult(RESULT_OK, intent);

            } else {
                MongolToast.makeText(activity, activity.getString(R.string.file_not_saved), MongolToast.LENGTH_SHORT).show();
            }

            activity.finish();
        }

    }
}


