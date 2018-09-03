package net.studymongolian.chimee;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

public class FavoriteActivity extends AppCompatActivity
		implements FavoritesRvAdapter.ItemClickListener {

	private static final String STATE_SCROLL_POSITION = "scrollPosition";
	public static final String CONTEXT_MENU_TAG = "context_menu";
	public static final String CURRENT_MESSAGE_KEY = "message";

	String currentMessage;
	//ListView lvFavorite;
	//FrameLayout flContextMenuContainer;
	//MessageFavoriteListAdapter adapter;
	int savedPosition = 0;
	ArrayList<Message> favoriteMessages = new ArrayList<Message>();
	FragmentManager fragmentManager;
	View menuHiderForOutsideClicks;
	int longClickedItem = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);

		setupToolbar();
		currentMessage = getIntent().getStringExtra(CURRENT_MESSAGE_KEY);
		new GetFavoriteMessages().execute();
	}

	private void setupToolbar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
	}

	@Override
	public void onItemClick(View view, int position) {
		Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onItemLongClick(View view, int position) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent();
				//intent.putExtra(CHANGES_MADE_KEY, changesWereMade);
				setResult(RESULT_OK, intent);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	public void addFavoriteClick(View v) {

		// catch empty string
		if (currentMessage.trim().length() == 0) {
			showNoContentDialog();
			return;
		}

		// add string to db
		new AddMessageToDb().execute();

	}

	private void showNoContentDialog() {

//		Intent intent = new Intent(this, MongolDialogOneButton.class);
//		intent.putExtra(MongolDialogOneButton.MESSAGE, getResources().getString(R.string.dialog_message_emptyfavorite));
//		startActivity(intent);

	}

	// call: new AddMessageToDb().execute();
	private class AddMessageToDb extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			// android.os.Debug.waitForDebugger();

			// Log.i("app", "Current message: " + currentMessage);
			try {

				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(
						getApplicationContext());
				dbAdapter.addFavoriteMessage(currentMessage);
			} catch (Exception e) {
				//Log.e("app", e.toString());
			}
			return null;

		}

		@Override
		protected void onPostExecute(Void v) {

			// update display
			new GetFavoriteMessages().execute();

		}

	}

	private class GetFavoriteMessages extends AsyncTask<Void, Void, ArrayList<Message>> {

		@Override
		protected ArrayList<Message> doInBackground(Void... params) {

			// android.os.Debug.waitForDebugger();

			ArrayList<Message> result = new ArrayList<Message>();

			try {

				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(
						getApplicationContext());
				result = dbAdapter.getAllFavoriteMessages();
			} catch (Exception e) {
				// Log.i("app", e.toString());
			}

			return result;

		}

		@Override
		protected void onPostExecute(ArrayList<Message> result) {

//			favoriteMessages = result;
//			adapter = new MessageFavoriteListAdapter(getApplicationContext(), result);
//			lvFavorite.setAdapter(adapter);
//			lvFavorite.setSelection(savedPosition);

		}
	}

	// call with: new UpdateMessageTime().execute(messageId);
	private class UpdateMessageTime extends AsyncTask<Long, Void, Integer> {

		private Context context = getApplicationContext();

		@Override
		protected Integer doInBackground(Long... params) {

			// android.os.Debug.waitForDebugger();

			// get the message
			long messageId = params[0];

			int count = 0;

			try {
				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(context);
				count = dbAdapter.updateFavorateMessageTime(messageId);
			} catch (Exception e) {
				// Log.e("app", e.toString());
			}

			return count;
		}

		@Override
		protected void onPostExecute(Integer count) {

			// This is the result from doInBackground

			if (count > 0) {
				// Notify the user that the message was deleted
				//showToast(context, getResources().getString(R.string.message_deleted),
				//		Toast.LENGTH_SHORT);
				// update display
				new GetFavoriteMessages().execute();
			}
		}
	}

	// call with: new DeleteMessageByIdTask().execute(messageId);
	private class DeleteMessageByIdTask extends AsyncTask<Long, Void, Integer> {

		private Context context = getApplicationContext();

		@Override
		protected Integer doInBackground(Long... params) {

			// android.os.Debug.waitForDebugger();

			// get the message
			long messageId = params[0];

			// Delete word
			int count = 0;

			try {
				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(context);
				count = dbAdapter.deleteFavoriteMessage(messageId);
			} catch (Exception e) {
				//Log.e("app", e.toString());
			}

			return count;
		}

		@Override
		protected void onPostExecute(Integer count) {

			// This is the result from doInBackground

			if (count > 0) {
				// Notify the user that the message was deleted
//				showToast(context, getResources().getString(R.string.toast_message_deleted),
//						Toast.LENGTH_SHORT);
				// update display
				//savedPosition = lvFavorite.getFirstVisiblePosition();
				new GetFavoriteMessages().execute();
			}
		}
	}


}
