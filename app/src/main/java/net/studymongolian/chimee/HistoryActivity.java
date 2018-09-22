package net.studymongolian.chimee;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import net.studymongolian.mongollibrary.MongolAlertDialog;
import net.studymongolian.mongollibrary.MongolMenu;
import net.studymongolian.mongollibrary.MongolMenuItem;
import net.studymongolian.mongollibrary.MongolToast;

public class HistoryActivity extends AppCompatActivity
        implements HistoryRvAdapter.HistoryListener {

    public static final String RESULT_STRING_KEY = "history_result";
    private static final int MENU_MARGIN_DP = 8;
    private static final int NUMBER_OF_MESSAGES_TO_LOAD = 100;
    private int longClickedItem = -1;
    private int mDataPageCounter = 0;
    private MenuItem overflowMenuItem;

	HistoryRvAdapter adapter;
    List<Message> mMessages = new ArrayList<>();
    private boolean isFinishedLoadingData = false;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		setupToolbar();
		setupRecyclerView();
		getHistoryMessages();
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

    private void getHistoryMessages() {
        new AppendHistoryMessageRange(this,
                NUMBER_OF_MESSAGES_TO_LOAD, mDataPageCounter).execute();
    }

    @Override
    public void onItemClick(View view, int position) {
        insertHistoryMessageIntoInputWindow(position);
    }

    private void insertHistoryMessageIntoInputWindow(int adapterPosition) {
        Message message = adapter.getItem(adapterPosition);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESULT_STRING_KEY, message.getMessage());
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
        insertHistoryMessageIntoInputWindow(longClickedItem);
    }

    private void deleteItem() {
        Message message = adapter.getItem(longClickedItem);
        new DeleteMessageByIdTask(this).execute(message.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        overflowMenuItem = menu.findItem(R.id.action_overflow);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_overflow:
                overflowMenuItemClick();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void overflowMenuItemClick() {
        MongolMenu menu = new MongolMenu(this);
        final MongolMenuItem export = new MongolMenuItem(
                getString(R.string.history_menu_save), R.drawable.ic_save_black_24dp);
        final MongolMenuItem delete = new MongolMenuItem(
                getString(R.string.history_menu_delete_all), R.drawable.ic_clear_black_24dp);
        menu.add(export);
        menu.add(delete);
        menu.setOnMenuItemClickListener(new MongolMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MongolMenuItem item) {
                if (item == export) {
                    new ExportHistory(HistoryActivity.this).execute();
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

    private void deleteAll() {
        MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_delete_all_history_messages));
        builder.setPositiveButton(getString(R.string.dialog_delete_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteAllMessages(HistoryActivity.this).execute();
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);
        MongolAlertDialog dialog = builder.create();
        dialog.show();
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

            activity.mMessages.addAll(range);
            int lastIndex = activity.adapter.getItemCount();
            activity.adapter.notifyItemRangeInserted(lastIndex, range.size());
            activity.mDataPageCounter++;
            if (range.size() < HistoryActivity.NUMBER_OF_MESSAGES_TO_LOAD) {
                activity.isFinishedLoadingData = true;
            }
            if (activity.adapter.getItemCount() == 0 && activity.overflowMenuItem != null) {
                activity.overflowMenuItem.setVisible(false);
            }
		}
	}

	private static class ExportHistory extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<HistoryActivity> activityReference;

        ExportHistory(HistoryActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            turnOnSpinner();
        }

        @Override
		protected Boolean doInBackground(Void... params) {
            HistoryActivity activity = activityReference.get();
            boolean result = false;
			try {
				MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
                ArrayList<Message> messages = dbAdapter.getAllHistoryMessages();
                String text = createFileTextFromMessages(messages);
                result = FileUtils.saveHistoryMessageFile(activity, text);
			} catch (Exception e) {
                e.printStackTrace();
			}
			return result;
		}

        private String createFileTextFromMessages(ArrayList<Message> messages) {
            StringBuilder text = new StringBuilder();
            for (Message message : messages) {
                String messageText = message.getMessage();
                String date = HistoryRvAdapter.convertDate(message.getDate());
                text.append(date).append('\n');
                text.append(messageText).append('\n');
                text.append("---").append('\n');
            }
            return text.toString();
        }

        @Override
		protected void onPostExecute(Boolean wasSuccessfullyExported) {

            HistoryActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            turnOffSpinner();

            if (wasSuccessfullyExported) {
                tellUserWhereToFindFile(activity);
            } else {
                MongolToast.makeText(activity,
                        activity.getString(R.string.couldnt_be_saved),
                        MongolToast.LENGTH_SHORT).show();
            }
		}

        private void turnOnSpinner() {
            HistoryActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            ProgressBar spinner = new ProgressBar(activity);
            spinner.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            activity.overflowMenuItem.setActionView(spinner);
        }

        private void turnOffSpinner() {
            HistoryActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.overflowMenuItem.setActionView(null);
        }

        private void tellUserWhereToFindFile(final Activity activity) {
            MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(activity);
            String location = FileUtils.getExportedHistoryFileDisplayPath();
            builder.setMessage(activity.getString(R.string.alert_where_to_find_history_export, location));
            builder.setPositiveButton(activity.getString(R.string.dialog_got_it), null);
            MongolAlertDialog dialog = builder.create();
            dialog.show();
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
            if (activity.adapter.getItemCount() == 0 && activity.overflowMenuItem != null) {
                activity.overflowMenuItem.setVisible(false);
            }
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
