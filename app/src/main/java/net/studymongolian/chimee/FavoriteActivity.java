package net.studymongolian.chimee;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.studymongolian.mongollibrary.MongolMenu;
import net.studymongolian.mongollibrary.MongolMenuItem;
import net.studymongolian.mongollibrary.MongolToast;

public class FavoriteActivity extends AppCompatActivity
		implements FavoritesRvAdapter.ItemClickListener {

    private static final int MARGIN_DP = 4;
	private static final String STATE_SCROLL_POSITION = "scrollPosition";
	public static final String CONTEXT_MENU_TAG = "context_menu";
	public static final String CURRENT_MESSAGE_KEY = "message";
    public static final String RESULT_STRING_KEY = "result";

    String currentMessage;
	FavoritesRvAdapter adapter;
	//ListView lvFavorite;
	//FrameLayout flContextMenuContainer;
	//MessageFavoriteListAdapter adapter;
	int savedPosition = 0;
	//ArrayList<Message> favoriteMessages = new ArrayList<>();
	FragmentManager fragmentManager;
	View menuHiderForOutsideClicks;
	int longClickedItem = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);

		setupToolbar();
		currentMessage = getIntent().getStringExtra(CURRENT_MESSAGE_KEY);
		new GetFavoriteMessages(this).execute();
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

	@Override
	public void onItemClick(View view, int position) {
        Message chosenMessage = adapter.getItem(position);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESULT_STRING_KEY, chosenMessage.getMessage());
        setResult(RESULT_OK, returnIntent);
        finish();
	}

	@Override
	public boolean onItemLongClick(View view, int position) {

        View menuButton = findViewById(R.id.action_add);
        int[] location = new int[2];
        menuButton.getLocationInWindow(location);
        int gravity = Gravity.TOP | Gravity.RIGHT;
        int marginPx = convertDpToPx(MARGIN_DP);
        int xOffset =  menuButton.getWidth();
        int yOffset = location[1] + marginPx;
        MongolMenu menu = getMenu();
        menu.showAtLocation(menuButton, gravity, xOffset, yOffset);
		return true;
	}

    private int convertDpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private MongolMenu getMenu() {
        MongolMenu menu = new MongolMenu(this);
        menu.add(new MongolMenuItem("edit", R.drawable.ic_mode_edit_black_24dp));
        menu.add(new MongolMenuItem("delete", R.drawable.ic_clear_black_24dp));
        menu.setOnMenuItemClickListener(new MongolMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MongolMenuItem item) {
                MongolToast.makeText(FavoriteActivity.this, item.getTitle(), MongolToast.LENGTH_SHORT).show();
                return true;
            }
        });
        return menu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorites_menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.action_add:
                addFavorite();
                return true;
			case android.R.id.home:
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    private void addFavorite() {
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
			new GetFavoriteMessages(FavoriteActivity.this).execute();

		}

	}

	private static class GetFavoriteMessages extends AsyncTask<Void, Void, ArrayList<Message>> {

        private WeakReference<FavoriteActivity> activityReference;

        GetFavoriteMessages(FavoriteActivity context) {
            activityReference = new WeakReference<>(context);
        }

		@Override
		protected ArrayList<Message> doInBackground(Void... params) {

			ArrayList<Message> result = new ArrayList<>();
            FavoriteActivity activity = activityReference.get();
			try {
				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
				result = dbAdapter.getAllFavoriteMessages();
			} catch (Exception e) {
				// Log.i("app", e.toString());
			}

			return result;

		}

		@Override
		protected void onPostExecute(ArrayList<Message> results) {
            FavoriteActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            RecyclerView recyclerView = activity.findViewById(R.id.rv_all_favorites);
            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            DividerItemDecoration dividerItemDecoration =
                    new DividerItemDecoration(recyclerView.getContext(),
                    horizontalLayoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            activity.adapter = new FavoritesRvAdapter(activity, results);
            activity.adapter.setClickListener(activity);
            recyclerView.setAdapter(activity.adapter);
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
				new GetFavoriteMessages(FavoriteActivity.this).execute();
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
				new GetFavoriteMessages(FavoriteActivity.this).execute();
			}
		}
	}

    private static class AddMessageToFavoriteDb extends AsyncTask<String, Void, Boolean> {

        private WeakReference<MainActivity> activityReference;

        AddMessageToFavoriteDb(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String message = params[0];
            MainActivity activity = activityReference.get();
            long rowId;
            try {
                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
                rowId = dbAdapter.addFavoriteMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return rowId >= 0;
        }

        @Override
        protected void onPostExecute(Boolean messageAdded) {
            if (!messageAdded) return;
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            MongolToast.makeText(activity, R.string.toast_favorite_added,
                    MongolToast.LENGTH_SHORT).show();
        }

    }
}
