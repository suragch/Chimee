package net.studymongolian.chimee;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import net.studymongolian.mongollibrary.MongolMenu;
import net.studymongolian.mongollibrary.MongolMenuItem;

public class FavoriteActivity extends AppCompatActivity
        implements FavoritesRvAdapter.ItemClickListener {

    private static final int MARGIN_DP = 4;
    public static final String CURRENT_MESSAGE_KEY = "message";
    public static final String RESULT_STRING_KEY = "result";

    private static final int EDIT_REQUEST_CODE = 0;
    private static final int ADD_REQUEST_CODE = 1;

    List<Message> mMessages = new ArrayList<>();
    String currentMessage;
    FavoritesRvAdapter adapter;
    int longClickedItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        setupToolbar();
        setupRecyclerView();
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

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_all_favorites);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),
                        horizontalLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new FavoritesRvAdapter(this, mMessages);
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

        View menuButton = findViewById(R.id.action_add);
        int[] location = new int[2];
        menuButton.getLocationInWindow(location);
        @SuppressLint("RtlHardcoded") int gravity = Gravity.TOP | Gravity.RIGHT;
        int marginPx = convertMarginDpToPx();
        int xOffset = menuButton.getWidth();
        int yOffset = location[1] + marginPx;
        MongolMenu menu = getMenu();
        menu.showAtLocation(menuButton, gravity, xOffset, yOffset);
        longClickedItem = position;
        return true;
    }

    private int convertMarginDpToPx() {
        return (int) (MARGIN_DP * getResources().getDisplayMetrics().density);
    }

    private MongolMenu getMenu() {
        MongolMenu menu = new MongolMenu(this);
        final MongolMenuItem edit = new MongolMenuItem(
                getString(R.string.favorites_menu_edit), R.drawable.ic_mode_edit_black_24dp);
        final MongolMenuItem delete = new MongolMenuItem(
                getString(R.string.favorites_menu_delete), R.drawable.ic_clear_black_24dp);
        menu.add(edit);
        menu.add(delete);
        menu.setOnMenuItemClickListener(item -> {
            if (item == edit) {
                editItem();
            } else if (item == delete) {
                deleteItem();
            }
            return true;
        });
        return menu;
    }

    private void editItem() {
        Intent intent = new Intent(this, AddEditFavoritesActivity.class);
        Message message = adapter.getItem(longClickedItem);
        intent.putExtra(AddEditFavoritesActivity.MESSAGE_ID_KEY, message.getId());
        intent.putExtra(AddEditFavoritesActivity.MESSAGE_TEXT_KEY, message.getMessage());
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    private void deleteItem() {
        Message message = adapter.getItem(longClickedItem);
        new DeleteMessageByIdTask(this).execute(message.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorites_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.action_add) {
            addNewFavorite();
            return true;
        } else if (itemId == android.R.id.home) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewFavorite() {
        Intent intent = new Intent(this, AddEditFavoritesActivity.class);
        intent.putExtra(AddEditFavoritesActivity.MESSAGE_TEXT_KEY, currentMessage);
        startActivityForResult(intent, ADD_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {

            case EDIT_REQUEST_CODE:

                Message message = adapter.getItem(longClickedItem);
                if (message == null) break;
                new RefreshMessageItem(this).execute(message.getId());
                break;

            case ADD_REQUEST_CODE:
                new GetFavoriteMessages(this).execute();
                break;
        }
    }

    private static class RefreshMessageItem extends AsyncTask<Long, Void, Message> {

        private final WeakReference<FavoriteActivity> activityReference;

        RefreshMessageItem(FavoriteActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Message doInBackground(Long... params) {

            long messageId = params[0];

            Message messageItem = null;
            FavoriteActivity activity = activityReference.get();
            try {

                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
                messageItem = dbAdapter.getFavoriteMessage(messageId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return messageItem;
        }

        @Override
        protected void onPostExecute(Message messageItem) {
            FavoriteActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.mMessages.set(activity.longClickedItem, messageItem);
            activity.adapter.notifyItemChanged(activity.longClickedItem);
            activity.longClickedItem = -1;
        }

    }

    private static class GetFavoriteMessages extends AsyncTask<Void, Void, ArrayList<Message>> {

        private final WeakReference<FavoriteActivity> activityReference;

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
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPostExecute(ArrayList<Message> results) {
            FavoriteActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.mMessages.clear();
            activity.mMessages.addAll(results);
            activity.adapter.notifyDataSetChanged();
        }
    }

    private static class DeleteMessageByIdTask extends AsyncTask<Long, Void, Integer> {

        private final WeakReference<FavoriteActivity> activityReference;

        DeleteMessageByIdTask(FavoriteActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected Integer doInBackground(Long... params) {
            long messageId = params[0];
            int count = 0;
            FavoriteActivity activity = activityReference.get();
            try {
                MessageDatabaseAdapter dbAdapter = new MessageDatabaseAdapter(activity);
                count = dbAdapter.deleteFavoriteMessage(messageId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return count;
        }

        @Override
        protected void onPostExecute(Integer count) {
            FavoriteActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (count <= 0) return;
            activity.mMessages.remove(activity.longClickedItem);
            activity.adapter.notifyItemRemoved(activity.longClickedItem);
        }
    }
}
