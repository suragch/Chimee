package net.studymongolian.chimee;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import net.studymongolian.mongollibrary.MongolFont;
import net.studymongolian.mongollibrary.MongolTextView;

import java.util.ArrayList;
import java.util.List;

public class FontChooserDialogFragment extends DialogFragment
        implements FontRecyclerViewAdapter.ItemClickListener {



    FontRecyclerViewAdapter adapter;
    FontDialogListener mListener;
    List<Font> mFonts;

    public interface FontDialogListener {
        void onFontDialogPositiveClick(Font chosenFont);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFontList();
    }

    private void setupFontList() {
        mFonts = new ArrayList<>();
        mFonts.add(new Font(getString(R.string.font_name_qagan), SettingsActivity.FONT_QAGAN));
        mFonts.add(new Font(getString(R.string.font_name_garqag), SettingsActivity.FONT_GARQAG));
        mFonts.add(new Font(getString(R.string.font_name_hara), SettingsActivity.FONT_HARA));
        mFonts.add(new Font(getString(R.string.font_name_scnin), SettingsActivity.FONT_SCNIN));
        mFonts.add(new Font(getString(R.string.font_name_hawang), SettingsActivity.FONT_HAWANG));
        mFonts.add(new Font(getString(R.string.font_name_qimed), SettingsActivity.FONT_QIMED));
        mFonts.add(new Font(getString(R.string.font_name_narin), SettingsActivity.FONT_NARIN));
        mFonts.add(new Font(getString(R.string.font_name_mcdcnbar), SettingsActivity.FONT_MCDVNBAR));
        mFonts.add(new Font(getString(R.string.font_name_amglang), SettingsActivity.FONT_AMGLANG));
        mFonts.add(new Font(getString(R.string.font_name_sidam), SettingsActivity.FONT_SIDAM));
        mFonts.add(new Font(getString(R.string.font_name_qingming), SettingsActivity.FONT_QINGMING));
        mFonts.add(new Font(getString(R.string.font_name_onqa_hara), SettingsActivity.FONT_ONQA_HARA));
        mFonts.add(new Font(getString(R.string.font_name_svgvnag), SettingsActivity.FONT_SVGVNAG));
        mFonts.add(new Font(getString(R.string.font_name_svlbiya), SettingsActivity.FONT_SVLBIYA));
        mFonts.add(new Font(getString(R.string.font_name_jclgq), SettingsActivity.FONT_JCLGQ));
    }

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
        adapter = new FontRecyclerViewAdapter(getContext(), mFonts);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
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
    public void onItemClick(View view, int position) {
        //MongolTextView textView = view.findViewById(R.id.mtv_font_preview);
        if (mListener != null) {
            //Typeface typeface = textView.getTypeface();
            mListener.onFontDialogPositiveClick(mFonts.get(position));
        }
        dismiss();
    }
}
