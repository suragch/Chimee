package net.studymongolian.chimee;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends AppCompatActivity implements OnItemClickListener,
		OnItemLongClickListener {

	public static final int NUMBER_OF_MESSAGES_TO_LOAD = 100;
	private static final String STATE_SCROLL_POSITION = "scrollPosition";
	//public static final String CONTEXT_MENU_TAG = "context_menu";
	public static final int DELETE_REQUEST = 0;

	ListView lvHistory;
	View footerView;
	ProgressBar progressSpinner;
	TextView tvListviewFooter;
	LinearLayout menuLayout;
	MessageHistoryListAdapter adapter;
	int savedPosition = 0;
	ArrayList<Message> historyMessages = new ArrayList<Message>();
	View menuHiderForOutsideClicks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

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

	// TODO save scroll state on rotation


	private class GetRecentHistoryMessages extends AsyncTask<Void, Void, ArrayList<Message>> {

		@Override
		protected ArrayList<Message> doInBackground(Void... params) {

			// android.os.Debug.waitForDebugger();

			ArrayList<Message> result = new ArrayList<Message>();

			try {

				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(
						getApplicationContext());
				result = dbAdapter.getRecentHistoryMessages(NUMBER_OF_MESSAGES_TO_LOAD);
			} catch (Exception e) {
				// Log.i("app", e.toString());
			}

			return result;

		}

		@Override
		protected void onPostExecute(ArrayList<Message> result) {

			historyMessages = result;
			adapter = new MessageHistoryListAdapter(getApplicationContext(), result);
			lvHistory.setAdapter(adapter);
			lvHistory.setSelection(savedPosition);
			if (adapter.getCount() < NUMBER_OF_MESSAGES_TO_LOAD) {
				// TODO this will give one false show when total messages=NUMBER
				lvHistory.removeFooterView(footerView);
			}
			/*
			 * if (historyMessages.size() < NUMBER_OF_MESSAGES_TO_LOAD) {
			 * lvHistory.removeFooterView(footerView); }
			 */
			// TODO add lvHistory.setOnScrollListener and populate data without having to press a
			// button
			// https://chrisarriola.wordpress.com/2012/06/15/dynamic-data-with-listview-loading-footer/

		}
	}

	private class GetAllHistoryMessages extends AsyncTask<Void, Void, ArrayList<Message>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressSpinner.setVisibility(View.VISIBLE);
			tvListviewFooter.setVisibility(View.GONE);
		}

		@Override
		protected ArrayList<Message> doInBackground(Void... params) {

			// android.os.Debug.waitForDebugger();

			ArrayList<Message> result = new ArrayList<Message>();

			try {

				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(
						getApplicationContext());
				result = dbAdapter.getAllHistoryMessages();
			} catch (Exception e) {
				// Log.i("app", e.toString());
			}

			return result;

		}

		@Override
		protected void onPostExecute(ArrayList<Message> result) {

			historyMessages = result;
			adapter = new MessageHistoryListAdapter(getApplicationContext(), result);
			lvHistory.setAdapter(adapter);
			lvHistory.removeFooterView(footerView);
			progressSpinner.setVisibility(View.GONE);
			tvListviewFooter.setVisibility(View.VISIBLE);
			lvHistory.setSelection(savedPosition);
			// TODO use the same adapter rather than making a new one
		}
	}

	// call with: new DeleteMessageByIdTask().execute(int lvPosition, long messageId);
	private class DeleteMessageByIdTask extends AsyncTask<Object, Void, Integer> {

		private Context context = getApplicationContext();
		private int lvPosition = -1;

		@Override
		protected Integer doInBackground(Object... params) {

			// android.os.Debug.waitForDebugger();

			// get the message
			lvPosition = (Integer) params[0];
			long messageId = (Long) params[1];

			int count = 0;

			try {
				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(context);
				count = dbAdapter.deleteHistoryMessage(messageId);
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
				if (lvPosition >= 0) {
					historyMessages.remove(lvPosition);
					adapter.notifyDataSetChanged();
					savedPosition = lvHistory.getFirstVisiblePosition();
				}

				// new GetHistoryMessages().execute();
				// TODO just update adapter rather than requerying
			}
		}
	}

	// call with: new DeleteAllMessages().execute();
	private class DeleteAllMessages extends AsyncTask<Void, Void, Integer> {

		private Context context = getApplicationContext();

		@Override
		protected Integer doInBackground(Void... params) {

			// android.os.Debug.waitForDebugger();

			int count = 0;

			try {
				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(context);
				count = dbAdapter.deleteHistoryAllMessages();
			} catch (Exception e) {
				//Log.e("app", e.toString());
			}

			return count;
		}

		@Override
		protected void onPostExecute(Integer count) {

			// This is the result from doInBackground

			if (count > 0) {
				// Notify the user that the messages were deleted
				/*showToast(getApplicationContext(),
						getResources().getString(R.string.toast_history_deleted),
						Toast.LENGTH_SHORT);*/
				// update display
				new GetRecentHistoryMessages().execute();
				// TODO just update adapter rather than requerying
			}
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {

		if (position == adapter.getCount()) { // footer
			if (progressSpinner.getVisibility()!=View.VISIBLE){ // don't allow second click 
				// load everything
				savedPosition = lvHistory.getFirstVisiblePosition();
				new GetAllHistoryMessages().execute();
			}
			
		} else {
			// send back to activity to insert in input window
			Intent returnIntent = new Intent();
			returnIntent.putExtra("resultString", historyMessages.get(position).getMessage());
			setResult(RESULT_OK, returnIntent);
			finish();
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long rowId) {

		if (position != adapter.getCount()) { // not the footer
			// delete message
			new DeleteMessageByIdTask().execute(position, historyMessages.get(position).getId());
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DELETE_REQUEST) {
			if (resultCode == RESULT_OK) {
				// Delete all message history
				new DeleteAllMessages().execute();
			}
		}
	}

}
