package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

    FontRvAdapter(Context context, List<Font> fonts) {
        this.mContext = context;
        this.mFonts = fonts;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.font_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Font font = mFonts.get(position);
        String displayName = font.getDisplayName();
        Typeface typeface = MongolFont.get(font.getFileLocation(), mContext);
        holder.mongolTextView.setText(displayName);
        holder.mongolTextView.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return mFonts.size();
    }

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

    Font getFontAtPosition(int id) {
        return mFonts.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onFontItemClick(View view, int position);
    }
}
