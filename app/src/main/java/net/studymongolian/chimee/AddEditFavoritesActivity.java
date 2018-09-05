package net.studymongolian.chimee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.studymongolian.mongollibrary.MongolEditText;
import net.studymongolian.mongollibrary.MongolInputMethodManager;


public class AddEditFavoritesActivity extends AppCompatActivity
        implements ImeDataSourceHelper.DataSourceHelperListener {

    static final String MESSAGE_ID_KEY = "message_id";
    static final String MESSAGE_TEXT_KEY = "message_text";
    //static final String EDIT_MODE_KEY = "edit_mode";
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
        //imeContainer.showSystemKeyboardsOption(getString(R.string.keyboard_show_system_keyboards));
        //imeContainer.setOnNonSystemImeListener(this);
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

    private void handleUserFinishing () {
        Intent intent = new Intent();
        intent.putExtra(MESSAGE_ADDED_KEY, messageWasAdded);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void saveMessage() {
        String messageText = metMessage.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, R.string.toast_message_empty, Toast.LENGTH_LONG).show();
            return;
        }



        if (isNewVocab()) {
            new AddMessage().execute(messageText);
        } else {
            Message message = new Message(mMessageId, System.currentTimeMillis(), messageText);
            new UpdateMessage().execute(message);
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

    private class AddMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String item = params[0];


            try {
                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(getApplicationContext());
                dbAdapter.addFavoriteMessage(item);
            } catch (Exception e) {
                Log.i("app", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            messageWasAdded = true;
            Intent intent = new Intent();
            //intent.putExtra(EDIT_MODE_KEY, true);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    private class UpdateMessage extends AsyncTask<Message, Void, Void> {

        @Override
        protected Void doInBackground(Message... params) {

            Message item = params[0];
            try {

                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(getApplicationContext());
                dbAdapter.updateFavoriteMessage(item);
            } catch (Exception e) {
                Log.i("app", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void results) {
            Intent intent = new Intent();
            //intent.putExtra(EDIT_MODE_KEY, true);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

//    private class LoadEditingMessage extends AsyncTask<Long, Void, Message> {
//
//        @Override
//        protected Message doInBackground(Long... params) {
//
//            long messageId = params[0];
//
//            Message messageItem = null;
//
//            try {
//
//                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(getApplicationContext());
//                messageItem = dbAdapter.getFavoriteMessage(messageId);
//            } catch (Exception e) {
//                Log.i("app", e.toString());
//            }
//
//            return messageItem;
//
//        }
//
//        @Override
//        protected void onPostExecute(Message messageItem) {
//            metMessage.setText(messageItem.getMessage());
//        }
//
//    }
}