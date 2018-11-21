package net.studymongolian.chimee;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import net.studymongolian.mongollibrary.MongolAlertDialog;
import net.studymongolian.mongollibrary.MongolTextView;
import net.studymongolian.mongollibrary.MongolToast;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {


	static final String PREFS_NAME = "MyPrefsFile";
	static final String FONT_KEY = "font";

	protected static final String FONT_DEFAULT = Font.QAGAN;

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
    private static final int INSTALL_KEYBOARD_REQUEST = 1;
    private static final int IMPORT_KEYBOARD_WORDS_REQUEST = 2;

    private boolean isChimeeSystemKeyboardAvailable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		setupToolbar();
		setupSystemKeyboardItem();
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

    private void setupSystemKeyboardItem() {
        isChimeeSystemKeyboardAvailable = isKeyboardActivated();
        if (isChimeeSystemKeyboardAvailable) {
            MongolTextView mtv = findViewById(R.id.mtv_install_keyboard);
            mtv.setText(getString(R.string.settings_choose_keyboard));
        }
    }

    private boolean isKeyboardActivated() {
        String packageLocal = getPackageName();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return false;
        List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

        // check if our keyboard is enabled as input method
        for (InputMethodInfo inputMethod : list) {
            String packageName = inputMethod.getPackageName();
            if (packageName.equals(packageLocal)) {
                return true;
            }
        }

        return false;
    }

    public void onHistoryClick(View view) {
	    Intent intent = new Intent(this, HistoryActivity.class);
	    startActivityForResult(intent, HISTORY_REQUEST);
    }

    public void onInstallKeyboardClick(View view) {
	    if (isChimeeSystemKeyboardAvailable) {
	        showChooseKeyboardDialog();
        } else {
	        showInstallKeyboardDialog();
        }
    }

    private void showChooseKeyboardDialog() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im == null) return;
        im.showInputMethodPicker();
    }

    private void showInstallKeyboardDialog() {
        Intent inputSettings = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        startActivityForResult(inputSettings, INSTALL_KEYBOARD_REQUEST);
    }

    public void onExportKeyboardWordsClick(View view) {
        if (PermissionsHelper.getWriteExternalStoragePermission(this))
            new ExportKeyboardWords(this).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (PermissionsHelper.isWritePermissionRequestGranted(requestCode, grantResults)) {
            new ExportKeyboardWords(this).execute();
        } else {
            PermissionsHelper.notifyUserThatTheyCantSaveFileWithoutWritePermission(this);
        }
    }

    public void onImportKeyboardWordsClick(View view) {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("files/*");
        intent = Intent.createChooser(chooseFile, getString(R.string.import_chooser_message));
        startActivityForResult(intent, IMPORT_KEYBOARD_WORDS_REQUEST);
    }

    public void onCodeConverterClick(View view) {
        Intent intent = new Intent(this, CodeConverterActivity.class);
        startActivity(intent);
    }

    public void onHelpClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpActivity.HELP_URL));
        startActivity(browserIntent);
        //Intent intent = new Intent(this, HelpActivity.class);
        //startActivity(intent);
    }

    public void onAboutClick(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case HISTORY_REQUEST:
                onHistoryResult(resultCode, data);
                break;
            case INSTALL_KEYBOARD_REQUEST:
                setupSystemKeyboardItem();
                break;
            case IMPORT_KEYBOARD_WORDS_REQUEST:
                onImportKeyboardWordsResult(resultCode, data);
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

    private void onImportKeyboardWordsResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        Uri uri = data.getData();
        new ImportWordList(this).execute(uri);
    }

    private static class ExportKeyboardWords extends AsyncTask<Void, Void, Boolean> {



        WeakReference<SettingsActivity> activityReference;

        ExportKeyboardWords(SettingsActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            SettingsActivity activity = activityReference.get();

            String text = extractTextFromDatabase(activity);
            return !TextUtils.isEmpty(text) &&
                    FileUtils.saveExportedWordsFile(activity, text);
        }

        private String extractTextFromDatabase(SettingsActivity activity) {
            Cursor cursor = UserDictionary.Words.getAllWords(activity);
            if (cursor == null) return "";

            int indexWord = cursor.getColumnIndex(UserDictionary.Words.WORD);
            int indexFollowing = cursor.getColumnIndex(UserDictionary.Words.FOLLOWING);

            StringBuilder builder = new StringBuilder();
            while (cursor.moveToNext()) {
                builder.append(cursor.getString(indexWord)).append(UserDictionary.Words.FIELD_DELIMITER);
                builder.append(cursor.getString(indexFollowing)).append('\n');
            }
            cursor.close();

            return builder.toString();
        }

        @Override
        protected void onPostExecute(Boolean exportSuccessful) {
            SettingsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (exportSuccessful) {
                notifyUserOfExportLocation(activity);
            } else {
                MongolToast.makeText(activity,
                        activity.getString(R.string.there_was_a_problem),
                        MongolToast.LENGTH_LONG)
                        .show();
            }

        }

        private void notifyUserOfExportLocation(Context context) {
            MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(context);
            String file = FileUtils.getExportedWordsFileDisplayPath();
            builder.setMessage(context.getString(R.string.alert_where_to_find_words_export, file));
            builder.setPositiveButton(context.getString(R.string.dialog_got_it), null);
            MongolAlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private static class ImportWordList extends AsyncTask<Uri, Void, Integer> {

        private WeakReference<SettingsActivity> activityReference;

        ImportWordList(SettingsActivity activityContext) {
            activityReference = new WeakReference<>(activityContext);
        }

        @Override
        protected Integer doInBackground(Uri... params) {


            SettingsActivity activity = activityReference.get();
            if (activity == null) return 0;
            Uri importFile = params[0];
            if (importFile == null) return 0;

            int numberImported = 0;
            try {
                InputStream inputStream = activity.getContentResolver().openInputStream(importFile);
                ArrayList<CharSequence> textLines = FileUtils.convertStreamToStringArray(inputStream);

                numberImported = UserDictionary.Words.importWordAndFollowingList(activity, textLines);

            } catch (Exception e) {
                e.printStackTrace();
                return numberImported;
            }

            return numberImported;
        }

        @Override
        protected void onPostExecute(Integer numberImported) {
            SettingsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            String message = activity.getString(R.string.number_words_imported, numberImported);
            MongolToast.makeText(activity, message, MongolToast.LENGTH_LONG).show();
        }

    }
}