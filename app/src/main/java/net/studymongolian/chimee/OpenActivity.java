package net.studymongolian.chimee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.studymongolian.mongollibrary.MongolTextView;

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

    private void showContent() {
        List<String> files = FileUtils.getTextFileNames();
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
    public void onItemClick(View view, int position) {
        String fileName = adapter.getItem(position);
        String fileText = FileUtils.openFile(fileName);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FILE_NAME_KEY, fileName);
        returnIntent.putExtra(FILE_TEXT_KEY, fileText);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        return false;
    }
}
