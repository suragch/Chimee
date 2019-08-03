package net.studymongolian.chimee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolInputMethodManager;

import java.lang.ref.WeakReference;


public class AddEditFavoritesActivity extends AppCompatActivity
        implements ImeDataSourceHelper.DataSourceHelperListener {

    static final String MESSAGE_ID_KEY = "message_id";
    static final String MESSAGE_TEXT_KEY = "message_text";
    static final String MESSAGE_ADDED_KEY = "message_added";
    private boolean messageWasAdded = false;

    private static final int NEW_MESSAGE = -1;

    CustomImeContainer imeContainer;
    MongolEditText metMessage;
    private long mMessageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_message);

        initToolbar();
        initKeyboard();
        loadInfoFromIntent();
        setFocusOnInputWindow();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initKeyboard() {
        metMessage = findViewById(R.id.metFavoriteMessage);
        imeContainer = findViewById(R.id.keyboard_container);

        MongolInputMethodManager mimm = new MongolInputMethodManager();
        mimm.addEditor(metMessage);
        mimm.setIme(imeContainer);
        imeContainer.setDataSource(new ImeDataSourceHelper(this));
        getSavedKeyboard(imeContainer);
    }

    private void getSavedKeyboard(CustomImeContainer imeContainer) {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        String userKeyboard = settings.getString(SettingsActivity.MONGOLIAN_KEYBOARD_KEY,
                SettingsActivity.MONGOLIAN_KEYBOARD_DEFAULT);
        if (userKeyboard.equals(SettingsActivity.MONGOLIAN_QWERTY_KEYBOARD)) {
            imeContainer.requestNewKeyboard(CustomImeContainer.MONGOL_QWERTY_KEYBOARD_INDEX);
        }
    }

    private void loadInfoFromIntent() {
        Intent intent = getIntent();
        mMessageId = intent.getLongExtra(MESSAGE_ID_KEY, NEW_MESSAGE);
        String messageText = intent.getStringExtra(MESSAGE_TEXT_KEY);

        if (messageText != null)
            metMessage.setText(messageText);
    }

    private void setFocusOnInputWindow() {
        MongolEditText editText = findViewById(R.id.metFavoriteMessage);
        editText.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_favorites_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miSaveWord:
                saveMessage();
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
        intent.putExtra(MESSAGE_ADDED_KEY, messageWasAdded);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void saveMessage() {
        String messageText = metMessage.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, R.string.input_window_empty, Toast.LENGTH_LONG).show();
            return;
        }

        if (isNewVocab()) {
            new AddMessage(this).execute(messageText);
        } else {
            Message message = new Message(mMessageId, System.currentTimeMillis(), messageText);
            new UpdateMessage(this).execute(message);
        }
    }

    private boolean isNewVocab() {
        return mMessageId == NEW_MESSAGE;
    }

    @Override
    public CustomImeContainer getImeContainer() {
        return imeContainer;
    }

    @Override
    public Context getContext() {
        return this;
    }

    private static class AddMessage extends AsyncTask<String, Void, Boolean> {

        private WeakReference<AddEditFavoritesActivity> activityReference;

        AddMessage(AddEditFavoritesActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String item = params[0];

            AddEditFavoritesActivity activity = activityReference.get();
            long result = -1;
            try {
                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
                result = dbAdapter.addFavoriteMessage(item);
            } catch (Exception e) {
                Log.i("app", e.toString());
            }
            return result >= 0;
        }

        @Override
        protected void onPostExecute(Boolean messageWasAdded) {
            AddEditFavoritesActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.messageWasAdded = messageWasAdded;
            Intent intent = new Intent();
            activity.setResult(RESULT_OK, intent);
            activity.finish();
        }
    }

    private static class UpdateMessage extends AsyncTask<Message, Void, Boolean> {

        private WeakReference<AddEditFavoritesActivity> activityReference;

        UpdateMessage(AddEditFavoritesActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(Message... params) {

            Message item = params[0];
            AddEditFavoritesActivity activity = activityReference.get();
            int numRowsAffected = 0;
            try {
                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
                numRowsAffected = dbAdapter.updateFavoriteMessage(item);
            } catch (Exception e) {
                Log.i("app", e.toString());
            }

            return numRowsAffected > 0;
        }

        @Override
        protected void onPostExecute(Boolean messageWasUpdated) {
            AddEditFavoritesActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            Intent intent = new Intent();
            if (messageWasUpdated)
                activity.setResult(RESULT_OK, intent);
            activity.finish();
        }
    }
}