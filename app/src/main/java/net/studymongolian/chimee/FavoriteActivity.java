package net.studymongolian.chimee;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class FavoriteActivity extends AppCompatActivity implements OnItemClickListener,
		OnItemLongClickListener {

	private static final String STATE_SCROLL_POSITION = "scrollPosition";
	public static final String CONTEXT_MENU_TAG = "context_menu";

	String currentMessage;
	ListView lvFavorite;
	FrameLayout flContextMenuContainer;
	MessageFavoriteListAdapter adapter;
	int savedPosition = 0;
	ArrayList<Message> favoriteMessages = new ArrayList<Message>();
	FragmentManager fragmentManager;
	View menuHiderForOutsideClicks;
	int longClickedItem = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);

		// setup toolbar
		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);

		Intent intent = getIntent();
		currentMessage = intent.getStringExtra("message");

		// create objects
		lvFavorite = (ListView) findViewById(R.id.lvFavorite);
		flContextMenuContainer = (FrameLayout) findViewById(R.id.flContextMenuContainer);
		menuHiderForOutsideClicks = findViewById(R.id.transparent_view);

		// Set up fragments
		fragmentManager = getSupportFragmentManager();
//		if (savedInstanceState == null) {
//			contextMenu = new FavoriteActivityContextMenu();
//			fragmentManager.beginTransaction()
//					.add(R.id.flContextMenuContainer, contextMenu, CONTEXT_MENU_TAG).commit();
//			contextMenu.setRetainInstance(true);
//		} else {
//			contextMenu = (FavoriteActivityContextMenu) fragmentManager
//					.findFragmentByTag(CONTEXT_MENU_TAG);
//		}

		// Show messages
		new GetFavoriteMessages().execute();
		lvFavorite.setOnItemClickListener(this);
		lvFavorite.setOnItemLongClickListener(this);

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		// Save the user's current game state
		int currentPosition = lvFavorite.getFirstVisiblePosition();
		savedInstanceState.putInt(STATE_SCROLL_POSITION, currentPosition);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();

		// hide the menu if showing
		if (flContextMenuContainer.getVisibility() == View.VISIBLE) {
			flContextMenuContainer.setVisibility(View.GONE);
			menuHiderForOutsideClicks.setVisibility(View.GONE);
		}
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Always call the superclass so it can restore the view hierarchy
		super.onRestoreInstanceState(savedInstanceState);

		savedPosition = savedInstanceState.getInt(STATE_SCROLL_POSITION);
		lvFavorite.setSelection(savedPosition);

	}

	public void finishedClick(View v) {
		finish();
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
				dbAdapter.addFavorateMessage(currentMessage);
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

			favoriteMessages = result;
			adapter = new MessageFavoriteListAdapter(getApplicationContext(), result);
			lvFavorite.setAdapter(adapter);
			lvFavorite.setSelection(savedPosition);

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
				savedPosition = lvFavorite.getFirstVisiblePosition();
				new GetFavoriteMessages().execute();
			}
		}
	}

	public void hideMenu(View view) {

		flContextMenuContainer.setVisibility(View.GONE);
		menuHiderForOutsideClicks.setVisibility(View.GONE);
		longClickedItem = -1;
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {

		Message chosenMessage = favoriteMessages.get(position);

		// send back to activity
		Intent returnIntent = new Intent();
		returnIntent.putExtra("resultString", chosenMessage.getMessage());
		setResult(RESULT_OK, returnIntent);
		finish();

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long rowId) {

		if (view != null) {
			flContextMenuContainer.setVisibility(View.VISIBLE);
			menuHiderForOutsideClicks.setVisibility(View.VISIBLE);
			longClickedItem = position;
		} else {
			longClickedItem = -1;
		}

		return true;
	}

//	@Override
//	public void contextMenuItemClicked(int itemCode) {
//
//		if (longClickedItem >= 0) {
//
//			Message chosenMessage = favoriteMessages.get(longClickedItem);
//
//			switch (itemCode) {
//			case FavoriteActivityContextMenu.MOVE_TO_FRONT:
//
//				new UpdateMessageTime().execute(chosenMessage.getId());
//				break;
//			case FavoriteActivityContextMenu.DELETE:
//
//				new DeleteMessageByIdTask().execute(chosenMessage.getId());
//				break;
//			}
//		}
//
//		hideMenu(null);
//	}

}
