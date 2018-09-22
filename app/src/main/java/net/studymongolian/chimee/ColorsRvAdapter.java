package net.studymongolian.chimee;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ColorsRvAdapter extends RecyclerView.Adapter<ColorsRvAdapter.ViewHolder> {

    private int[] mViewColors;
    private int mItemLayoutResId;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    ColorsRvAdapter(Context context, int item_layout_res_id, int[] colors) {
        this.mInflater = LayoutInflater.from(context);
        this.mItemLayoutResId = item_layout_res_id;
        this.mViewColors = colors;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mItemLayoutResId, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color = mViewColors[position];
        if (holder.myView instanceof CircleView) {
            ((CircleView) holder.myView).setColor(color);
        } else {
            holder.myView.setBackgroundColor(color);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mViewColors.length;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View myView;

        ViewHolder(View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.color_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onColorItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public int getColorAtPosition(int position) {
        return mViewColors[position];
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onColorItemClick(View view, int position);
    }
}