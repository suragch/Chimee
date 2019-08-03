package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.studymongolian.mongollibrary.MongolTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryRvAdapter extends RecyclerView.Adapter<HistoryRvAdapter.ViewHolder> {

    private List<Message> mMessages;
    private LayoutInflater mInflater;
    private HistoryListener mListener;

    HistoryRvAdapter(Context context, List<Message> messages) {
        this.mInflater = LayoutInflater.from(context);
        this.mMessages = messages;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_history_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.mtvMessage.setText(message.getMessage());
        String date = convertDate(message.getDate());
        holder.mtvDate.setText(date);

        // check if need to load more
        if (position >= getItemCount() - 1
                && mListener != null)
            mListener.loadMore();
    }

    static String convertDate(long unixMilliseconds) {
        Date date = new Date(unixMilliseconds);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yy.M.d H:mm:ss");
        return sdf.format(date);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        MongolTextView mtvMessage;
        MongolTextView mtvDate;

        ViewHolder(View itemView) {
            super(itemView);
            mtvMessage = itemView.findViewById(R.id.mtv_history_message_item);
            mtvDate = itemView.findViewById(R.id.mtv_history_date_item);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) mListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return mListener != null
                    && mListener.onItemLongClick(view, getAdapterPosition());
        }
    }

    Message getItem(int id) {
        return mMessages.get(id);
    }

    void setClickListener(HistoryListener itemClickListener) {
        this.mListener = itemClickListener;
    }

    public interface HistoryListener {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
        void loadMore();
    }
}
