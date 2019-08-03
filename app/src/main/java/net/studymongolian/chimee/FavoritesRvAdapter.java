package net.studymongolian.chimee;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.studymongolian.mongollibrary.MongolTextView;

import java.util.List;

public class FavoritesRvAdapter extends RecyclerView.Adapter<FavoritesRvAdapter.ViewHolder> {

    private List<Message> mFavorites;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    FavoritesRvAdapter(Context context, List<Message> messages) {
        this.mInflater = LayoutInflater.from(context);
        this.mFavorites = messages;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.message_favorite_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mFavorites.get(position);
        holder.mtvMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return mFavorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        MongolTextView mtvMessage;

        ViewHolder(View itemView) {
            super(itemView);
            mtvMessage = itemView.findViewById(R.id.mtv_favorite_message_item);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return mClickListener != null
                    && mClickListener.onItemLongClick(view, getAdapterPosition());
        }
    }

    Message getItem(int id) {
        return mFavorites.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }
}
