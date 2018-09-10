package net.studymongolian.chimee;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.studymongolian.mongollibrary.MongolMenu;
import net.studymongolian.mongollibrary.MongolMenuItem;

public class HistoryActivity extends AppCompatActivity
        implements HistoryRvAdapter.HistoryListener {

    private static final int MENU_MARGIN_DP = 8;
	public static final int NUMBER_OF_MESSAGES_TO_LOAD = 100;
	private static final String STATE_SCROLL_POSITION = "scrollPosition";
	public static final int DELETE_REQUEST = 0;
    public static final String RESULT_STRING_KEY = "history_result";
    int longClickedItem = -1;
    int mDataPageCounter = 0;

	HistoryRvAdapter adapter;
    List<Message> mMessages = new ArrayList<>();
    private boolean isFinishedLoadingData = false;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		setupToolbar();
		setupRecyclerView();
		new AppendHistoryMessageRange(this, NUMBER_OF_MESSAGES_TO_LOAD, mDataPageCounter).execute();
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

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_message_history);
        final LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),
                        horizontalLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new HistoryRvAdapter(this, mMessages);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
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
        View menuButton = findViewById(R.id.action_overflow);
        int[] location = new int[2];
        menuButton.getLocationInWindow(location);
        int gravity = Gravity.TOP | Gravity.RIGHT;
        int marginPx = convertDpToPx(MENU_MARGIN_DP);
        int xOffset = menuButton.getWidth();
        int yOffset = location[1] + marginPx;
        MongolMenu menu = getContextMenu();
        menu.showAtLocation(menuButton, gravity, xOffset, yOffset);
        longClickedItem = position;
        return true;
    }

    @Override
    public void loadMore() {
        if (isFinishedLoadingData) return;
	    int offset = mDataPageCounter * NUMBER_OF_MESSAGES_TO_LOAD;
        new AppendHistoryMessageRange(this, NUMBER_OF_MESSAGES_TO_LOAD, offset).execute();
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private MongolMenu getContextMenu() {
        MongolMenu menu = new MongolMenu(this);
        final MongolMenuItem edit = new MongolMenuItem(
                getString(R.string.history_menu_edit_item), R.drawable.ic_mode_edit_black_24dp);
        final MongolMenuItem delete = new MongolMenuItem(
                getString(R.string.history_menu_delete_item), R.drawable.ic_clear_black_24dp);
        menu.add(edit);
        menu.add(delete);
        menu.setOnMenuItemClickListener(new MongolMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MongolMenuItem item) {
                if (item == edit) {
                    editItem();
                } else if (item == delete) {
                    deleteItem();
                }
                return true;
            }
        });
        return menu;
    }

    private void editItem() {

    }

    private void deleteItem() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_overflow:
                overflowMenuItemClick();
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

    private void overflowMenuItemClick() {
        MongolMenu menu = new MongolMenu(this);
        final MongolMenuItem export = new MongolMenuItem(
                getString(R.string.history_menu_export), R.drawable.ic_save_black_24dp);
        final MongolMenuItem delete = new MongolMenuItem(
                getString(R.string.history_menu_delete_all), R.drawable.ic_clear_black_24dp);
        menu.add(export);
        menu.add(delete);
        menu.setOnMenuItemClickListener(new MongolMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MongolMenuItem item) {
                if (item == export) {
                    exportHistory();
                } else if (item == delete) {
                    deleteAll();
                }
                return true;
            }
        });

        int[] location = new int[2];
        View overflowMenuButton = findViewById(R.id.action_overflow);
        overflowMenuButton.getLocationInWindow(location);
        int gravity = Gravity.TOP | Gravity.RIGHT;
        int marginPx = convertDpToPx(MENU_MARGIN_DP);
        int yOffset = location[1] + marginPx;

        menu.showAtLocation(overflowMenuButton, gravity, marginPx, yOffset);
    }



    private void exportHistory() {

    }

    private void deleteAll() {

    }

	private static class AppendHistoryMessageRange extends AsyncTask<Void, Void, ArrayList<Message>> {

        private WeakReference<HistoryActivity> activityReference;
        int limit;
        int offset;

        AppendHistoryMessageRange(HistoryActivity context, int limit, int offset) {
            activityReference = new WeakReference<>(context);
            this.limit = limit;
            this.offset = offset;
        }

		@Override
		protected ArrayList<Message> doInBackground(Void... params) {

			ArrayList<Message> result = new ArrayList<>();
            HistoryActivity activity = activityReference.get();
			try {
				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
				result = dbAdapter.getHistoryMessages(limit, offset);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<Message> range) {

            HistoryActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            //activity.mMessages.clear();
            activity.mMessages.addAll(range);
            int lastIndex = activity.adapter.getItemCount();
            activity.adapter.notifyItemRangeInserted(lastIndex, range.size());
            activity.mDataPageCounter++;
            if (range.size() < activity.NUMBER_OF_MESSAGES_TO_LOAD) {
                activity.isFinishedLoadingData = true;
            }
		}
	}

	private static class GetAllHistoryMessages extends AsyncTask<Void, Void, ArrayList<Message>> {

        private WeakReference<HistoryActivity> activityReference;

        GetAllHistoryMessages(HistoryActivity context) {
            activityReference = new WeakReference<>(context);
        }

		@Override
		protected ArrayList<Message> doInBackground(Void... params) {
			ArrayList<Message> result = new ArrayList<>();
            HistoryActivity activity = activityReference.get();
			try {
				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
				result = dbAdapter.getAllHistoryMessages();
			} catch (Exception e) {
                e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(ArrayList<Message> results) {

            HistoryActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.mMessages.clear();
            activity.mMessages.addAll(results);
            activity.adapter.notifyDataSetChanged();
		}
	}

    private static class DeleteMessageByIdTask extends AsyncTask<Long, Void, Integer> {

        private WeakReference<HistoryActivity> activityReference;

        DeleteMessageByIdTask(HistoryActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(Long... params) {
            long messageId = params[0];
            int count = 0;
            HistoryActivity activity = activityReference.get();
            try {
                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
                count = dbAdapter.deleteHistoryMessage(messageId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return count;
        }

        @Override
        protected void onPostExecute(Integer count) {
            HistoryActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (count <= 0) return;
            activity.mMessages.remove(activity.longClickedItem);
            activity.adapter.notifyItemRemoved(activity.longClickedItem);
        }
    }

    private static class DeleteAllMessages extends AsyncTask<Void, Void, Integer> {

        private WeakReference<HistoryActivity> activityReference;

        DeleteAllMessages(HistoryActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int count = 0;
            HistoryActivity activity = activityReference.get();
            try {
                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
                count = dbAdapter.deleteHistoryAllMessages();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return count;
        }

        @Override
        protected void onPostExecute(Integer count) {
            HistoryActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (count <= 0) return;
            activity.mMessages.clear();
            activity.adapter.notifyDataSetChanged();
        }
    }

}
