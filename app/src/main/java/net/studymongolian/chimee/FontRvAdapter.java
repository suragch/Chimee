package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.studymongolian.mongollibrary.MongolFont;
import net.studymongolian.mongollibrary.MongolTextView;

import java.util.List;

public class FontRvAdapter
        extends RecyclerView.Adapter<FontRvAdapter.ViewHolder> {

    private List<Font> mFonts;
    private ItemClickListener mClickListener;
    private Context mContext;

    // data is passed into the constructor
    FontRvAdapter(Context context, List<Font> fonts) {
        this.mContext = context;
        this.mFonts = fonts;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.font_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Font font = mFonts.get(position);
        String displayName = font.getDisplayName();
        Typeface typeface = MongolFont.get(font.getFileLocation(), mContext);
        holder.mongolTextView.setText(displayName);
        holder.mongolTextView.setTypeface(typeface);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mFonts.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MongolTextView mongolTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mongolTextView = itemView.findViewById(R.id.mtv_font_preview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onFontItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Font getFontAtPosition(int id) {
        return mFonts.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onFontItemClick(View view, int position);
    }
}
