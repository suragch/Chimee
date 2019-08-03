package net.studymongolian.chimee;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ColorsRvAdapter extends RecyclerView.Adapter<ColorsRvAdapter.ViewHolder> {

    private int[] mViewColors;
    private int mItemLayoutResId;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    ColorsRvAdapter(Context context, int item_layout_res_id, int[] colors) {
        this.mInflater = LayoutInflater.from(context);
        this.mItemLayoutResId = item_layout_res_id;
        this.mViewColors = colors;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mItemLayoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color = mViewColors[position];
        if (holder.myView instanceof CircleView) {
            ((CircleView) holder.myView).setColor(color);
        } else {
            holder.myView.setBackgroundColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return mViewColors.length;
    }

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

    int getColorAtPosition(int position) {
        return mViewColors[position];
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onColorItemClick(View view, int position);
    }
}