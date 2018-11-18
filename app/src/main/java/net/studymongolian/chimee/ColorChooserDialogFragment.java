package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import net.studymongolian.mongollibrary.MongolLabel;

public class ColorChooserDialogFragment extends DialogFragment
        implements ColorsRvAdapter.ItemClickListener {

    private int mBgColor = SettingsActivity.BGCOLOR_DEFAULT;
    private int mTextColor = SettingsActivity.TEXTCOLOR_DEFAULT;

    ColorsRvAdapter adapter;
    ColorDialogListener mListener;
    MongolLabel mColorPreview;
    RadioButton rbBackground;
    RadioButton rbTextColor;
    int[] mColorChoices;

    public interface ColorDialogListener {
        void onColorDialogPositiveClick(int chosenBackgroundColor, int chosenForegroundColor);
    }

    public static ColorChooserDialogFragment newInstance(int oldBgColor, int oldTextColor) {
        ColorChooserDialogFragment dialog = new ColorChooserDialogFragment();

        Bundle args = new Bundle();
        args.putInt(SettingsActivity.BGCOLOR_KEY, oldBgColor);
        args.putInt(SettingsActivity.TEXTCOLOR_KEY, oldTextColor);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCurrentColors();
        getColorChoices();
    }

    private void getCurrentColors() {
        if (getArguments() == null) return;
        int backgroundColor = getArguments().getInt(SettingsActivity.BGCOLOR_KEY);
        int foregroundColor = getArguments().getInt(SettingsActivity.TEXTCOLOR_KEY);
        if (backgroundColor != 0)
            mBgColor = backgroundColor;
        if (foregroundColor != 0)
            mTextColor = foregroundColor;
    }

    private void getColorChoices() {
        mColorChoices = getResources().getIntArray(R.array.color_choices);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_color_chooser, null);
        setupColorPreview(customView);
        setupRadioButtons(customView);
        setupDialogButtons(customView);
        setupRecyclerView(customView);
        builder.setView(customView);
        return builder.create();
    }

    private void setupColorPreview(View customView) {
        mColorPreview = customView.findViewById(R.id.ml_color_preview);
        mColorPreview.setTextColor(mTextColor);
        mColorPreview.setBackgroundColor(mBgColor);
    }

    private void setupRadioButtons(View customView) {
        // TODO: this functionality should be in mongol-library
        rbBackground = customView.findViewById(R.id.rb_bg_color);
        rbTextColor = customView.findViewById(R.id.rb_fg_color);
        MongolLabel mlBackground = customView.findViewById(R.id.ml_bg_color);
        MongolLabel mlForeground = customView.findViewById(R.id.ml_fg_color);
        rbBackground.setOnClickListener(onBackgroundClick);
        rbTextColor.setOnClickListener(onForegroundClick);
        mlBackground.setOnClickListener(onBackgroundClick);
        mlForeground.setOnClickListener(onForegroundClick);
    }

    private void setupDialogButtons(View customView) {
        FrameLayout negativeButton = customView.findViewById(R.id.dialog_button_negative);
        FrameLayout positiveButton = customView.findViewById(R.id.dialog_button_positive);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onColorDialogPositiveClick(mBgColor, mTextColor);
                dismiss();
            }
        });
    }

    private void setupRecyclerView(View customView) {
        RecyclerView recyclerView = customView.findViewById(R.id.color_choices_recycler_view);
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(customView.getContext(), numberOfColumns));
        adapter = new ColorsRvAdapter(getContext(), R.layout.color_grid_item, mColorChoices);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private View.OnClickListener onBackgroundClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rbBackground.setChecked(true);
            rbTextColor.setChecked(false);
        }
    };

    private View.OnClickListener onForegroundClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            rbBackground.setChecked(false);
            rbTextColor.setChecked(true);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ColorDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ColorDialogListener");
        }
    }

    @Override
    public void onColorItemClick(View view, int position) {
        int color = mColorChoices[position];
        if (rbBackground.isChecked()) {
            mColorPreview.setBackgroundColor(color);
            mBgColor = color;
        } else {
            mColorPreview.setTextColor(color);
            mTextColor = color;
        }
    }

}
