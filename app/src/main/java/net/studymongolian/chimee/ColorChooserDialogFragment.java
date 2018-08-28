package net.studymongolian.chimee;

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
        implements ColorRecyclerViewAdapter.ItemClickListener {

    private int mBgColor = SettingsActivity.BGCOLOR_DEFAULT;
    private int mTextColor = SettingsActivity.TEXTCOLOR_DEFAULT;

    ColorRecyclerViewAdapter adapter;
    ColorDialogListener mListener;
    MongolLabel mColorPreview;
    RadioButton rbBackground;
    RadioButton rbTextColor;

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
        adapter = new ColorRecyclerViewAdapter(getContext(), mColorResIds);
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
    public void onItemClick(View view, int position) {
        int color = getResources().getColor(mColorResIds[position]);
        if (rbBackground.isChecked()) {
            mColorPreview.setBackgroundColor(color);
            mBgColor = color;
        } else {
            mColorPreview.setTextColor(color);
            mTextColor = color;
        }
    }

    private int[] mColorResIds = {
            R.color.white,
            R.color.grey_200,
            R.color.grey_500,
            R.color.black,
            R.color.blue_grey_100,
            R.color.blue_grey_200,
            R.color.blue_grey_500,
            R.color.blue_grey_800,
            R.color.indigo_100,
            R.color.indigo_200,
            R.color.indigo_500,
            R.color.indigo_800,
            R.color.blue_100,
            R.color.blue_200,
            R.color.blue_500,
            R.color.blue_800,
            R.color.light_blue_100,
            R.color.light_blue_200,
            R.color.light_blue_500,
            R.color.light_blue_800,
            R.color.cyan_100,
            R.color.cyan_200,
            R.color.cyan_500,
            R.color.cyan_800,
            R.color.teal_100,
            R.color.teal_200,
            R.color.teal_500,
            R.color.teal_800,
            R.color.green_100,
            R.color.green_200,
            R.color.green_500,
            R.color.green_800,
            R.color.light_green_100,
            R.color.light_green_200,
            R.color.light_green_500,
            R.color.light_green_800,
            R.color.lime_100,
            R.color.lime_200,
            R.color.lime_500,
            R.color.lime_800,
            R.color.yellow_100,
            R.color.yellow_200,
            R.color.yellow_500,
            R.color.yellow_800,
            R.color.amber_100,
            R.color.amber_200,
            R.color.amber_500,
            R.color.amber_800,
            R.color.orange_100,
            R.color.orange_200,
            R.color.orange_500,
            R.color.orange_800,
            R.color.deep_orange_100,
            R.color.deep_orange_200,
            R.color.deep_orange_500,
            R.color.deep_orange_800,
            R.color.red_100,
            R.color.red_200,
            R.color.red_500,
            R.color.red_800,
            R.color.pink_100,
            R.color.pink_200,
            R.color.pink_500,
            R.color.pink_800,
            R.color.purple_100,
            R.color.purple_200,
            R.color.purple_500,
            R.color.purple_800,
            R.color.deep_purple_100,
            R.color.deep_purple_200,
            R.color.deep_purple_500,
            R.color.deep_purple_800,
            R.color.brown_100,
            R.color.brown_200,
            R.color.brown_500,
            R.color.brown_800
    };
}
