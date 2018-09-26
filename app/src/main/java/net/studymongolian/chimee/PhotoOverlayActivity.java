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
import android.widget.LinearLayout;
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
    private static final float SHADOW_SPIRAL_ANGLE_RADIANS_MIN = 0;
    private static final float SHADOW_SPIRAL_ANGLE_RADIANS_MAX = (float) (6*Math.PI);
    private static final float SHADOW_RADIUS_MULTIPLIER_MIN = 0.01f;
    private static final float SHADOW_RADIUS_MULTIPLIER_MAX = 0.2f;
    private static final float DEFAULT_SHADOW_RADIUS_MULTIPLIER = 0.1f;
    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_SHADOW_COLOR = Color.BLACK;

    private CharSequence currentMessage;
    private Bitmap bitmap;
    private TouchImageView mImageView;
    private OverlayTextView textOverlayView;
    private RecyclerView rvChooser;
    private LinearLayout llColorChooser;
    private FrameLayout flCancelColorButton;
    private LinearLayout llSeekBars;
    private SeekBar leftSeekBar;
    private SeekBar rightSeekBar;
    private ColorsRvAdapter colorAdapter;
    int[] mColorChoices;
    private FontRvAdapter fontAdapter;
    private View selectedView;

    private int borderSeekBarProgress = 50;
    private int shadowLeftSeekBarProgress = 50;
    private int shadowRightSeekBarProgress = 50;
    private int backgroundLeftSeekBarProgress = 50;
    private int backgroundRightSeekBarProgress = 50;

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
        llSeekBars = findViewById(R.id.ll_seekbar);
        leftSeekBar = findViewById(R.id.left_seekbar);
        leftSeekBar.setOnSeekBarChangeListener(leftSeekBarChangeListener);
        rightSeekBar = findViewById(R.id.right_seekbar);
        rightSeekBar.setOnSeekBarChangeListener(rightSeekBarListener);
    }


    private SeekBar.OnSeekBarChangeListener leftSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (selectedView.getId()) {
                case R.id.fl_photo_overlay_border:
                    updateBorderFromProgress(progress);
                    break;
                case R.id.fl_photo_overlay_shadow:
                    updateShadowXyFromProgress(progress);
                    break;
                case R.id.fl_photo_overlay_background:
                    updateBackgroundOpacityFromProgress(progress);
                    break;
            }

        }

        private void updateBorderFromProgress(int progress) {
            borderSeekBarProgress = progress;
            float multiplier = getStrokeMultiplierFromSeekBarProgress(progress);
            textOverlayView.setStrokeWidthMultiplier(multiplier);
            if (textOverlayView.getStrokeColor() == 0) {
                textOverlayView.setStrokeColor(DEFAULT_STROKE_COLOR);
            }
        }

        private void updateShadowXyFromProgress(int progress) {
            shadowLeftSeekBarProgress = progress;

            int color = textOverlayView.getShadowColor();
            if (color == Color.TRANSPARENT)
                color = DEFAULT_SHADOW_COLOR;

            float radiusMultiplier = textOverlayView.getShadowRadiusMultiplier();
            if (radiusMultiplier == 0)
                radiusMultiplier = DEFAULT_SHADOW_RADIUS_MULTIPLIER;

            PointFloat offset = getShadowOffsetFromSeekBarProgress(progress);
            float dxMultiplier = offset.getX() / textOverlayView.getTextSize();
            float dyMultiplier = offset.getY() / textOverlayView.getTextSize();
            textOverlayView.setShadowLayerMultipliers(
                    radiusMultiplier, dxMultiplier, dyMultiplier, color);
        }

        private void updateBackgroundOpacityFromProgress(int progress) {
            backgroundLeftSeekBarProgress = progress;

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private PointFloat getShadowOffsetFromSeekBarProgress(int progress) {
        // do a spiral looping three times (spiral of Archimedes)
        float radians = SHADOW_SPIRAL_ANGLE_RADIANS_MIN
                + (SHADOW_SPIRAL_ANGLE_RADIANS_MAX - SHADOW_SPIRAL_ANGLE_RADIANS_MIN) * progress / 100;
        float dx = (float) (radians * Math.cos(radians));
        float dy = (float) (radians * Math.sin(radians));
        return new PointFloat(dx, dy);
    }

    private float getShadowRadiusMultiplierFromSeekBarProgress(int progress) {
        return SHADOW_RADIUS_MULTIPLIER_MIN
                + (SHADOW_RADIUS_MULTIPLIER_MAX - SHADOW_RADIUS_MULTIPLIER_MIN) * progress / 100;
    }

    private float getStrokeMultiplierFromSeekBarProgress(int progress) {
        return STROKE_WIDTH_MULTIPLIER_MIN
                + (STROKE_WIDTH_MULTIPLIER_MAX - STROKE_WIDTH_MULTIPLIER_MIN) * progress / 100;
    }

    private int getSeekBarProgressFromStrokeMultiplier(float multiplier) {
        return (int) (100 * (multiplier - STROKE_WIDTH_MULTIPLIER_MIN)
                / (STROKE_WIDTH_MULTIPLIER_MAX - STROKE_WIDTH_MULTIPLIER_MIN));
    }

    private SeekBar.OnSeekBarChangeListener rightSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (selectedView.getId()) {
                case R.id.fl_photo_overlay_shadow:
                    updateShadowRadiusFromProgress(progress);
                    break;
                case R.id.fl_photo_overlay_background:
                    updateBackgroundCornerRadiusFromProgress(progress);
                    break;
            }
        }

        private void updateShadowRadiusFromProgress(int progress) {
            shadowRightSeekBarProgress = progress;

            int color = textOverlayView.getShadowColor();
            if (color == Color.TRANSPARENT)
                color = DEFAULT_SHADOW_COLOR;

            float dxMultiplier = textOverlayView.getShadowDxMultiplier();
            float dyMultiplier = textOverlayView.getShadowDyMultiplier();
            float radiusMultiplier = getShadowRadiusMultiplierFromSeekBarProgress(progress);
            textOverlayView.setShadowLayerMultipliers(
                    radiusMultiplier, dxMultiplier, dyMultiplier, color);
        }

        private void updateBackgroundCornerRadiusFromProgress(int progress) {

            backgroundRightSeekBarProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private void setupBottomToolbar() {
        ivColor = findViewById(R.id.iv_color);
        ivFont = findViewById(R.id.iv_font);
        ivBorder = findViewById(R.id.iv_border);
        ivShadow = findViewById(R.id.iv_shadow);
        ivBackground = findViewById(R.id.iv_background);
        llColorChooser = findViewById(R.id.ll_photo_overlay_colors);
        flCancelColorButton = findViewById(R.id.fl_cancel_color);
        setupSeekBar();
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
                llColorChooser.getVisibility() == View.VISIBLE) {
            hideSettingBars();
            return;
        }
        selectedView = view;

        // show setting bars
        llColorChooser.setVisibility(View.VISIBLE);
        unselectAllItems();
        int id = view.getId();
        switch (id) {
            case R.id.fl_photo_overlay_color:
                setColorSettingsVisibility();
                break;
            case R.id.fl_photo_overlay_font:
                setFontSettingsVisibility();
                break;
            case R.id.fl_photo_overlay_border:
                setBorderSettingsVisibility();
                break;
            case R.id.fl_photo_overlay_shadow:
                setShadowSettingsVisibility();
                break;
            case R.id.fl_photo_overlay_background:
                setBgSettingsVisibility();
                break;
        }
    }

    private void setColorSettingsVisibility() {
        ivColor.setSelected(true);
        flCancelColorButton.setVisibility(View.VISIBLE);
        llSeekBars.setVisibility(View.INVISIBLE);
        rightSeekBar.setVisibility(View.GONE);
    }

    private void setFontSettingsVisibility() {
        ivFont.setSelected(true);
        flCancelColorButton.setVisibility(View.GONE);
        llSeekBars.setVisibility(View.INVISIBLE);
        rightSeekBar.setVisibility(View.GONE);
    }

    private void setBorderSettingsVisibility() {
        ivBorder.setSelected(true);
        flCancelColorButton.setVisibility(View.VISIBLE);
        llSeekBars.setVisibility(View.VISIBLE);
        rightSeekBar.setVisibility(View.GONE);
        updateProgressWithoutSettingValue(leftSeekBar, borderSeekBarProgress);
    }

    private void updateProgressWithoutSettingValue(SeekBar seekBar, int progress) {
        seekBar.setOnSeekBarChangeListener(null);
        seekBar.setProgress(progress);
        if (seekBar == leftSeekBar)
            seekBar.setOnSeekBarChangeListener(leftSeekBarChangeListener);
        else if (seekBar == rightSeekBar)
            seekBar.setOnSeekBarChangeListener(rightSeekBarListener);
    }

    private void setShadowSettingsVisibility() {
        ivShadow.setSelected(true);
        flCancelColorButton.setVisibility(View.VISIBLE);
        llSeekBars.setVisibility(View.VISIBLE);
        rightSeekBar.setVisibility(View.VISIBLE);
        updateProgressWithoutSettingValue(leftSeekBar, shadowLeftSeekBarProgress);
        updateProgressWithoutSettingValue(rightSeekBar, shadowRightSeekBarProgress);
    }

    private void setBgSettingsVisibility() {
        ivBackground.setSelected(true);
        flCancelColorButton.setVisibility(View.VISIBLE);
        llSeekBars.setVisibility(View.VISIBLE);
        rightSeekBar.setVisibility(View.VISIBLE);
        updateProgressWithoutSettingValue(leftSeekBar, backgroundLeftSeekBarProgress);
        updateProgressWithoutSettingValue(rightSeekBar, backgroundRightSeekBarProgress);
    }

    private void hideSettingBars() {
        llColorChooser.setVisibility(View.INVISIBLE);
        llSeekBars.setVisibility(View.INVISIBLE);
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
                float multiplier = getStrokeMultiplierFromSeekBarProgress(leftSeekBar.getProgress());
                textOverlayView.setStrokeWidthMultiplier(multiplier);
                break;
            case R.id.fl_photo_overlay_shadow:
                setShadowColor(color);
                break;
            case R.id.fl_photo_overlay_background:
                //textOverlayView.setTextColor(color);
                break;
        }
    }

    public void onCancelColorToolbarItemClick(View view) {
        int color = Color.TRANSPARENT;
        int id = selectedView.getId();
        switch (id) {
            case R.id.fl_photo_overlay_color:
                textOverlayView.setTextColor(color);
                break;
            case R.id.fl_photo_overlay_border:
                textOverlayView.setStrokeColor(color);
                break;
            case R.id.fl_photo_overlay_shadow:
                textOverlayView.setShadowLayerMultipliers(0, 0, 0, color);
                break;
            case R.id.fl_photo_overlay_background:
                //textOverlayView.setTextColor(color);
                break;
        }
    }



    private void setShadowColor(int color) {
        PointFloat offset = getShadowOffsetFromSeekBarProgress(shadowLeftSeekBarProgress);
        float radiusMultiplier = getShadowRadiusMultiplierFromSeekBarProgress(shadowRightSeekBarProgress);
        float dxMultiplier = offset.getX() / textOverlayView.getTextSize();
        float dyMultiplier = offset.getY() / textOverlayView.getTextSize();
        textOverlayView.setShadowLayerMultipliers(radiusMultiplier, dxMultiplier, dyMultiplier, color);
    }

    @Override
    public void onFontItemClick(View view, int position) {
        Font font = fontAdapter.getFontAtPosition(position);
        Typeface typeface = MongolFont.get(font.getFileLocation(), this);
        textOverlayView.setTypeface(typeface);

    }
}
