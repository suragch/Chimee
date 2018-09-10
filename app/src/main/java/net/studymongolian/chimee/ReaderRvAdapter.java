package net.studymongolian.chimee;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.studymongolian.mongollibrary.MongolTextView;

import java.util.List;

public class ReaderRvAdapter extends RecyclerView.Adapter<ReaderRvAdapter.ViewHolder> {

    private List<String> mParagraphs;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    ReaderRvAdapter(Context context, List<String> numbers) {
        this.mInflater = LayoutInflater.from(context);
        this.mParagraphs = numbers;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.reader_paragraph_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = mParagraphs.get(position);
        holder.mongolTextView.setText(text);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mParagraphs.size();
    }

    public String extractFullText() {
        StringBuilder sb = new StringBuilder();
        for (String paragraph : mParagraphs) {
            sb.append(paragraph).append('\n');
        }
        return sb.toString();
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        MongolTextView mongolTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mongolTextView = itemView.findViewById(R.id.mtv_reader_paragraph);
        }
    }
}
