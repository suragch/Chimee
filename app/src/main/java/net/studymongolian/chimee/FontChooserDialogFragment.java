package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FontChooserDialogFragment extends DialogFragment
        implements FontRvAdapter.ItemClickListener {



    FontRvAdapter adapter;
    FontDialogListener mListener;
    List<Font> mFonts;

    public interface FontDialogListener {
        void onFontDialogPositiveClick(Font chosenFont);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_font_chooser, null);
        setupRecyclerView(customView);
        builder.setView(customView);
        return builder.create();
    }

    private void setupRecyclerView(View customView) {
        RecyclerView recyclerView = customView.findViewById(R.id.rv_font_list);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        mFonts = getFonts();
        adapter = new FontRvAdapter(getContext(), mFonts);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private List<Font> getFonts() {
        if (getContext() == null) return new ArrayList<>();
        return Font.getAvailableFonts(getContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FontDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FontDialogListener");
        }
    }

    @Override
    public void onFontItemClick(View view, int position) {
        if (mListener != null) {
            mListener.onFontDialogPositiveClick(mFonts.get(position));
        }
        dismiss();
    }
}
