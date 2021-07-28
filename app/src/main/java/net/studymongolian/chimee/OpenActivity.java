package net.studymongolian.chimee;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.studymongolian.mongollibrary.MongolTextView;
import net.studymongolian.mongollibrary.MongolToast;

import java.util.List;

public class OpenActivity extends AppCompatActivity
        implements SimpleListRvAdapter.ItemClickListener {

    static final String FILE_NAME_KEY = "file_name";
    static final String FILE_TEXT_KEY = "file_text";

    SimpleListRvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        setupToolbar();
        showDocDirectoryName();
        showContent();
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

    private void showDocDirectoryName() {
        TextView textView = findViewById(R.id.tv_documents_directory);
        String path = FileUtils.getAppDocumentFolder(textView.getContext());
        textView.setText(path);
    }

    private void showContent() {
        List<String> files = FileUtils.getTextFileNamesWithoutExtension(this);
        if (files.size() > 0) {
            setupRecyclerView(files);
        } else {
            showEmptyContentMessage();
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

    private void showEmptyContentMessage() {
        MongolTextView textView = findViewById(R.id.mtv_doc_dir_empty_notice);
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        String fileName = adapter.getItem(position);
        String fileText = null;
        try {
            fileText = FileUtils.openFile(view.getContext(), fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fileText != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(FILE_NAME_KEY, fileName);
            returnIntent.putExtra(FILE_TEXT_KEY, fileText);
            setResult(RESULT_OK, returnIntent);
            finish();
        } else {
            MongolToast.makeText(this,
                    getString(R.string.could_not_open_file), MongolToast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        return false;
    }
}
