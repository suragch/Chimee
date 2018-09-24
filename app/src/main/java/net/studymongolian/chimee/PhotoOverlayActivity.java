package net.studymongolian.chimee;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import net.studymongolian.mongollibrary.MongolFont;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PhotoOverlayActivity extends AppCompatActivity
        implements ColorsRvAdapter.ItemClickListener, FontRvAdapter.ItemClickListener {


    public static final String CURRENT_MESSAGE_KEY = "message";
    private static final float STROKE_WIDTH_MULTIPLIER_MAX = 0.2f;
    private static final float STROKE_WIDTH_MULTIPLIER_MIN = 0.02f;
    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private CharSequence currentMessage;
    private Bitmap bitmap;
    private TouchImageView mImageView;
    private OverlayTextView textOverlayView;
    private RecyclerView rvChooser;
    private FrameLayout flSeekbar;
    private SeekBar seekBar;
    private ColorsRvAdapter colorAdapter;
    int[] mColorChoices;
    private FontRvAdapter fontAdapter;
    private View selectedView;

    ImageView ivColor;
    ImageView ivFont;
    ImageView ivBorder;
    ImageView ivShadow;
    ImageView ivBackground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_overlay);

        setupToolbar();
        setupPhotoView();
        getIntentData();
        createTextOverlay();
        layoutTextOverlay();
        setupRecyclerView();
        setupColorAdapter();
        setupFontAdapter();
        setupSeekBar();
        setupBottomToolbar();


    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }
    }

    private void setupPhotoView() {
        mImageView = findViewById(R.id.imageView);
        mImageView.setOnTouchListener(mImageViewTouchListener);
    }


    private View.OnTouchListener mImageViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                textOverlayView.setFocused(false);
            }
            return false;
        }
    };

    private void getIntentData() {
        currentMessage = getIntent().getCharSequenceExtra(CURRENT_MESSAGE_KEY);

        try {
            if (bitmap != null) {
                bitmap.recycle();
            }
            Uri uri = getIntent().getData();
            if (uri == null) return;
            InputStream stream = getContentResolver().openInputStream(uri);
            if (stream == null) return;
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            int newHeight = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, newHeight, true);
            mImageView.setImageBitmap(scaled);
            mImageView.setZoom(1f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTextOverlay() {
        textOverlayView = new OverlayTextView(this);
        textOverlayView.setText(currentMessage);
        textOverlayView.setTextColor(DEFAULT_TEXT_COLOR);
        FrameLayout layout = findViewById(R.id.photo_frame_layout);
        layout.addView(textOverlayView);
    }

    private void layoutTextOverlay() {
        textOverlayView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        textOverlayView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                        FrameLayout layout = findViewById(R.id.photo_frame_layout);
                        int x = (layout.getWidth() - textOverlayView.getWidth()) / 2;
                        int y = (layout.getHeight() - textOverlayView.getHeight()) / 2;
                        textOverlayView.setX(x);
                        textOverlayView.setY(y);
                    }
                });
    }


    private void setupRecyclerView() {
        rvChooser = findViewById(R.id.rv_photo_overlay_choices);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvChooser.setLayoutManager(horizontalLayoutManager);
    }

    private void setupColorAdapter() {
        int[] viewColors = getColorChoices();
        colorAdapter = new ColorsRvAdapter(this, R.layout.colors_rv_item, viewColors);
        colorAdapter.setClickListener(this);
    }

    private int[] getColorChoices() {
        if (mColorChoices == null)
            mColorChoices = getResources().getIntArray(R.array.color_choices);
        return mColorChoices;
    }

    private void setupFontAdapter() {
        List<Font> fonts = Font.getAvailableFonts(this);
        fontAdapter = new FontRvAdapter(this, fonts);
        fontAdapter.setClickListener(this);
    }

    private void setupSeekBar() {
        flSeekbar = findViewById(R.id.fl_seekbar);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float multiplier = getStrokeMultiplierFromSeekBarProgress(progress);
            textOverlayView.setStrokeWidthMultiplier(multiplier);
            if (textOverlayView.getStrokeColor() == 0)
                textOverlayView.setStrokeColor(DEFAULT_STROKE_COLOR);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private float getStrokeMultiplierFromSeekBarProgress(int progress) {
        return STROKE_WIDTH_MULTIPLIER_MIN
                + (STROKE_WIDTH_MULTIPLIER_MAX - STROKE_WIDTH_MULTIPLIER_MIN) * progress / 100;
    }

    private void setupBottomToolbar() {
        ivColor = findViewById(R.id.iv_color);
        ivFont = findViewById(R.id.iv_font);
        ivBorder = findViewById(R.id.iv_border);
        ivShadow = findViewById(R.id.iv_shadow);
        ivBackground = findViewById(R.id.iv_background);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_overlay_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePhoto();
                return true;
            case R.id.action_share:
                sharePhoto();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (rvChooser.getVisibility() == View.VISIBLE)
            hideSettingBars();
        else
            super.onBackPressed();
    }

    private void savePhoto() {

    }

    private void sharePhoto() {

    }

    public void onColorToolbarItemClick(View view) {
        setColorAdapter();
        setVisibility(view);
    }

    private void setColorAdapter() {
        if (rvChooser.getAdapter() == colorAdapter) return;
        rvChooser.setAdapter(colorAdapter);
    }

    private void setFontAdapter() {
        if (rvChooser.getAdapter() == fontAdapter) return;
        rvChooser.setAdapter(fontAdapter);
    }

    public void onFontToolbarItemClick(View view) {
        setFontAdapter();
        setVisibility(view);
    }

    private void setVisibility(View view) {


        // hide if already selected
        if (selectedView == view &&
                rvChooser.getVisibility() == View.VISIBLE) {
            hideSettingBars();
            return;
        }
        selectedView = view;

        // show setting bars
        rvChooser.setVisibility(View.VISIBLE);
        unselectAllItems();
        int id = view.getId();
        switch (id) {
            case R.id.fl_photo_overlay_color:
                ivColor.setSelected(true);
                flSeekbar.setVisibility(View.INVISIBLE);
                break;
            case R.id.fl_photo_overlay_font:
                ivFont.setSelected(true);
                flSeekbar.setVisibility(View.INVISIBLE);
                break;
            case R.id.fl_photo_overlay_border:
                ivBorder.setSelected(true);
                flSeekbar.setVisibility(View.VISIBLE);
                break;
            case R.id.fl_photo_overlay_shadow:
                ivShadow.setSelected(true);
                flSeekbar.setVisibility(View.VISIBLE);
                break;
            case R.id.fl_photo_overlay_background:
                ivBackground.setSelected(true);
                flSeekbar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void hideSettingBars() {
        rvChooser.setVisibility(View.INVISIBLE);
        flSeekbar.setVisibility(View.INVISIBLE);
        unselectAllItems();
    }

    private void unselectAllItems() {
        ivColor.setSelected(false);
        ivFont.setSelected(false);
        ivBorder.setSelected(false);
        ivShadow.setSelected(false);
        ivBackground.setSelected(false);
    }

    public void onBorderToolbarItemClick(View view) {
        setColorAdapter();
        setVisibility(view);
    }

    public void onShadowToolbarItemClick(View view) {
        setColorAdapter();
        setVisibility(view);
    }

    public void onBackgroundToolbarItemClick(View view) {
        setColorAdapter();
        setVisibility(view);
    }

    @Override
    public void onColorItemClick(View view, int position) {
        int color = colorAdapter.getColorAtPosition(position);
        int id = selectedView.getId();
        switch (id) {
            case R.id.fl_photo_overlay_color:
                textOverlayView.setTextColor(color);
                break;
            case R.id.fl_photo_overlay_border:
                textOverlayView.setStrokeColor(color);
                float multiplier = getStrokeMultiplierFromSeekBarProgress(seekBar.getProgress());
                textOverlayView.setStrokeWidthMultiplier(multiplier);
                break;
            case R.id.fl_photo_overlay_shadow:
                //textOverlayView.setTextColor(color);
                break;
            case R.id.fl_photo_overlay_background:
                //textOverlayView.setTextColor(color);
                break;
        }
    }

    @Override
    public void onFontItemClick(View view, int position) {
        Font font = fontAdapter.getFontAtPosition(position);
        Typeface typeface = MongolFont.get(font.getFileLocation(), this);
        textOverlayView.setTypeface(typeface);

    }
}
