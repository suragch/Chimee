package net.studymongolian.chimee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import net.studymongolian.mongollibrary.MongolAlertDialog;
import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolInputMethodManager;
import net.studymongolian.mongollibrary.MongolTextView;
import net.studymongolian.mongollibrary.MongolToast;

import java.lang.ref.WeakReference;
import java.util.List;

public class SaveActivity extends AppCompatActivity
        implements ImeDataSourceHelper.DataSourceHelperListener,
        SimpleListRvAdapter.ItemClickListener {

    static final String TEXT_KEY = "text";

    private MenuItem saveButton;
    private MongolTextView hintText;
    CustomImeContainer imeContainer;
    MongolEditText metFileName;
    String mText;
    SimpleListRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        setupToolbar();
        setupKeyboard();
        setupMongolEditText();
        loadInfoFromIntent();
        showExistingFilenameList();
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
        hintText = findViewById(R.id.mtv_file_name_hint);
        editText.requestFocus();
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean buttonIsVisible = saveButton.isVisible();
                boolean fileNameIsEmpty = TextUtils.isEmpty(s);
                if (buttonIsVisible && fileNameIsEmpty) {
                    saveButton.setVisible(false);
                    hintText.setVisibility(View.VISIBLE);
                }
                else if (!buttonIsVisible && !fileNameIsEmpty) {
                    saveButton.setVisible(true);
                    hintText.setVisibility(View.INVISIBLE);
                }
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

    private void showExistingFilenameList() {
        List<String> files = FileUtils.getTextFileNamesWithoutExtension(this);
        if (files.size() > 0) {
            setupRecyclerView(files);
        } else {
            hideRecyclerView();
        }
    }

    private void setupRecyclerView(List<String> files) {
        RecyclerView recyclerView = findViewById(R.id.rv_document_list);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),
                        horizontalLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new SimpleListRvAdapter(this, files);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void hideRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_document_list);
        recyclerView.setVisibility(View.INVISIBLE);
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
        return FileUtils.textFileExists(this, filename);
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
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);
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

    @Override
    public void onItemClick(View view, int position) {
        String filename = adapter.getItem(position);
        metFileName.setText(filename);
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        return false;
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
                MongolToast.makeText(activity, activity.getString(R.string.saved), MongolToast.LENGTH_SHORT).show();
                activity.setResult(RESULT_OK, intent);
            } else {
                MongolToast.makeText(activity, activity.getString(R.string.couldnt_be_saved), MongolToast.LENGTH_SHORT).show();
            }

            activity.finish();
        }

    }
}


