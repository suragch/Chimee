package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import net.studymongolian.mongollibrary.MongolAlertDialog;
import net.studymongolian.mongollibrary.MongolFont;
import net.studymongolian.mongollibrary.MongolToast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class PhotoOverlayActivity extends BaseActivity
        implements ColorsRvAdapter.ItemClickListener, FontRvAdapter.ItemClickListener {


    public static final String CURRENT_MESSAGE_KEY = "message";
    public static final String CURRENT_TYPEFACE_KEY = "typeface";
    private static final float STROKE_WIDTH_MULTIPLIER_MAX = 0.2f;
    private static final float STROKE_WIDTH_MULTIPLIER_MIN = 0.02f;
    private static final float SHADOW_SPIRAL_ANGLE_RADIANS_MIN = 0;
    private static final float SHADOW_SPIRAL_ANGLE_RADIANS_MAX = (float) (6 * Math.PI);
    private static final float SHADOW_RADIUS_MULTIPLIER_MIN = 0.01f;
    private static final float SHADOW_RADIUS_MULTIPLIER_MAX = 0.2f;
    private static final float DEFAULT_SHADOW_RADIUS_MULTIPLIER = 0.1f;
    private static final float BG_CORNER_RADIUS_MULTIPLIER_MIN = 0;
    private static final float BG_CORNER_RADIUS_MULTIPLIER_MAX = 1f;
    private static final int OPACITY_MIN = 0x00;
    private static final int OPACITY_MAX = 0xff;
    private static final int TEXT_PADDING_DP_MIN = 0;
    private static final int TEXT_PADDING_DP_MAX = 100;
    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_SHADOW_COLOR = Color.BLACK;
    private static final int DEFAULT_BG_COLOR = Color.LTGRAY;
    private static final int DEFAULT_BG_ALPHA = 0x80;

    private CharSequence currentMessage;
    private Typeface defaultTypeface;
    private Bitmap bitmap;
    private TouchImageView mImageView;
    private OverlayTextView textOverlayView;
    private RecyclerView rvChooser;
    private RelativeLayout rlColorChooser;
    private FrameLayout flCancelColorButton;
    private LinearLayout llSeekBars;
    private SeekBar alphaSeekBar;
    private SeekBar leftSeekBar;
    private SeekBar rightSeekBar;
    private ColorsRvAdapter colorAdapter;
    int[] mColorChoices;
    private FontRvAdapter fontAdapter;
    private View selectedView;

    private int textOpacitySeekBarProgress = 100;
    private int borderOpacitySeekBarProgress = 100;
    private int strokeSeekBarProgress = 50;
    private int shadowOpacitySeekBarProgress = 100;
    private int shadowLeftSeekBarProgress = 50;
    private int shadowRightSeekBarProgress = 50;
    private int backgroundOpacitySeekBarProgress = 100;
    private int backgroundCornerRadiusSeekBarProgress = 50;
    private int backgroundPaddingSeekBarProgress = 10;

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

    @SuppressLint("ClickableViewAccessibility")
    private void setupPhotoView() {
        mImageView = findViewById(R.id.imageView);
        mImageView.setOnTouchListener(mImageViewTouchListener);
    }


    private final View.OnTouchListener mImageViewTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                textOverlayView.setFocused(false);
                hideSettingBars();
            }
            return false;
        }
    };


    private void getIntentData() {
        currentMessage = getIntent().getCharSequenceExtra(CURRENT_MESSAGE_KEY);
        String fontLocation = getIntent().getStringExtra(CURRENT_TYPEFACE_KEY);
        defaultTypeface = MongolFont.get(fontLocation, this);

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
        textOverlayView.setTypeface(defaultTypeface);
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
        alphaSeekBar = findViewById(R.id.alpha_seekbar);
        alphaSeekBar.setOnSeekBarChangeListener(alphaSeekBarChangeListener);
        llSeekBars = findViewById(R.id.ll_seekbar);
        leftSeekBar = findViewById(R.id.left_seekbar);
        leftSeekBar.setOnSeekBarChangeListener(leftSeekBarChangeListener);
        rightSeekBar = findViewById(R.id.right_seekbar);
        rightSeekBar.setOnSeekBarChangeListener(rightSeekBarListener);
    }

    private final SeekBar.OnSeekBarChangeListener alphaSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final int viewId = selectedView.getId();
            if (viewId == R.id.fl_photo_overlay_text_color) {
                updateTextFromProgress(progress);
            } else if (viewId == R.id.fl_photo_overlay_border) {
                updateBorderFromProgress(progress);
            } else if (viewId == R.id.fl_photo_overlay_shadow) {
                updateShadowFromProgress(progress);
            } else if (viewId == R.id.fl_photo_overlay_background) {
                updateBackgroundFromProgress(progress);
            }
        }

        private void updateTextFromProgress(int progress) {
            textOpacitySeekBarProgress = progress;
            int color = textOverlayView.getTextColor();
            int alpha = getAlphaFromSeekBarProgress(progress);
            int colorWithAlpha = getColorWithAlpha(alpha, color);
            textOverlayView.setTextColor(colorWithAlpha);
        }

        private void updateBorderFromProgress(int progress) {
            borderOpacitySeekBarProgress = progress;
            int color = textOverlayView.getStrokeColor();
            int alpha = getAlphaFromSeekBarProgress(progress);
            int colorWithAlpha = getColorWithAlpha(alpha, color);
            textOverlayView.setStrokeColor(colorWithAlpha);
        }

        private void updateShadowFromProgress(int progress) {
            shadowOpacitySeekBarProgress = progress;
            int color = textOverlayView.getShadowColor();
            int alpha = getAlphaFromSeekBarProgress(progress);
            int colorWithAlpha = getColorWithAlpha(alpha, color);
            textOverlayView.setShadowColor(colorWithAlpha);
        }

        private void updateBackgroundFromProgress(int progress) {
            backgroundOpacitySeekBarProgress = progress;
            int color = textOverlayView.getRoundBackgroundColor();
            int alpha = getAlphaFromSeekBarProgress(progress);
            int colorWithAlpha = getColorWithAlpha(alpha, color);
            textOverlayView.setRoundBackgroundColor(colorWithAlpha);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private int getColorWithAlpha(int alpha, int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private final SeekBar.OnSeekBarChangeListener leftSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final int viewId = selectedView.getId();
            if (viewId == R.id.fl_photo_overlay_border) {
                updateStrokeWidthFromProgress(progress);
            } else if (viewId == R.id.fl_photo_overlay_shadow) {
                updateShadowXyFromProgress(progress);
            } else if (viewId == R.id.fl_photo_overlay_background) {
                updatePaddingFromProgress(progress);
            }
        }

        private void updateStrokeWidthFromProgress(int progress) {
            strokeSeekBarProgress = progress;
            float multiplier = getStrokeMultiplierFromSeekBarProgress(progress);
            textOverlayView.setStrokeWidthMultiplier(multiplier);
            if (textOverlayView.getStrokeColor() == Color.TRANSPARENT) {
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

        private void updatePaddingFromProgress(int progress) {
            backgroundPaddingSeekBarProgress = progress;

            int color = textOverlayView.getRoundBackgroundColor();
            if (color == 0) {
                int colorWithAlpha = getColorWithAlpha(DEFAULT_BG_ALPHA, DEFAULT_BG_COLOR);
                textOverlayView.setRoundBackgroundColor(colorWithAlpha);
            }

            int padding = getPaddingFromSeekBarProgress(progress);
            textOverlayView.setTextPaddingDp(padding);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private PointFloat getShadowOffsetFromSeekBarProgress(int progress) {
        // do a spiral looping three times (spiral of Archimedes)
        float radians = SHADOW_SPIRAL_ANGLE_RADIANS_MIN
                + (SHADOW_SPIRAL_ANGLE_RADIANS_MAX - SHADOW_SPIRAL_ANGLE_RADIANS_MIN) * progress / 100;
        float dx = (float) (radians * Math.cos(radians));
        float dy = (float) (radians * Math.sin(radians));
        return new PointFloat(dx, dy);
    }

    private float getStrokeMultiplierFromSeekBarProgress(int progress) {
        return STROKE_WIDTH_MULTIPLIER_MIN
                + (STROKE_WIDTH_MULTIPLIER_MAX - STROKE_WIDTH_MULTIPLIER_MIN) * progress / 100;
    }

    private float getShadowRadiusMultiplierFromSeekBarProgress(int progress) {
        return SHADOW_RADIUS_MULTIPLIER_MIN
                + (SHADOW_RADIUS_MULTIPLIER_MAX - SHADOW_RADIUS_MULTIPLIER_MIN) * progress / 100;
    }

    private float getBgCornerMultiplierFromSeekBarProgress(int progress) {
        return BG_CORNER_RADIUS_MULTIPLIER_MIN
                + (BG_CORNER_RADIUS_MULTIPLIER_MAX - BG_CORNER_RADIUS_MULTIPLIER_MIN) * progress / 100;
    }

    private int getAlphaFromSeekBarProgress(int progress) {
        return OPACITY_MIN
                + (OPACITY_MAX - OPACITY_MIN) * progress / 100;
    }

    private int getPaddingFromSeekBarProgress(int progress) {
        return TEXT_PADDING_DP_MIN
                + (TEXT_PADDING_DP_MAX - TEXT_PADDING_DP_MIN) * progress / 100;
    }

    private final SeekBar.OnSeekBarChangeListener rightSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final int viewId = selectedView.getId();
            if (viewId == R.id.fl_photo_overlay_shadow) {
                updateShadowRadiusFromProgress(progress);
            } else if (viewId == R.id.fl_photo_overlay_background) {
                updateBackgroundCornerRadiusFromProgress(progress);
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
            backgroundCornerRadiusSeekBarProgress = progress;

            int color = textOverlayView.getRoundBackgroundColor();
            if (color == Color.TRANSPARENT) {
                int colorWithAlpha = getColorWithAlpha(DEFAULT_BG_ALPHA, DEFAULT_BG_COLOR);
                textOverlayView.setRoundBackgroundColor(colorWithAlpha);
            }

            float radiusMultiplier = getBgCornerMultiplierFromSeekBarProgress(progress);
            textOverlayView.setBackgroundCornerRadiusMultiplier(radiusMultiplier);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private void setupBottomToolbar() {
        ivColor = findViewById(R.id.iv_color);
        ivFont = findViewById(R.id.iv_font);
        ivBorder = findViewById(R.id.iv_border);
        ivShadow = findViewById(R.id.iv_shadow);
        ivBackground = findViewById(R.id.iv_background);
        rlColorChooser = findViewById(R.id.rl_photo_overlay_colors);
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
        final int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            savePhoto();
            return true;
        } else if (itemId == R.id.action_share) {
            sharePhoto();
            return true;
        } else if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (rlColorChooser.getVisibility() == View.VISIBLE)
            hideSettingBars();
        else {
            super.onBackPressed();
        }
    }

    private void savePhoto() {
        if (PermissionsHelper.getWriteExternalStoragePermission(this))
            new SavePhoto(this).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionsHelper.isWritePermissionRequestGranted(requestCode, grantResults)) {
            new SavePhoto(this).execute();
        } else {
            PermissionsHelper.notifyUserThatTheyCantSaveFileWithoutWritePermission(this);
        }
    }

    private void sharePhoto() {
        new SharePhoto(this).execute();
    }

    private Bitmap renderBitmap() {

        // text location in ImageView coordinates
        PointF textViewTopLeft = textOverlayView.getTextViewTopLeft();
        PointF textViewBottomLeft = textOverlayView.getTextViewBottomLeft();
        PointF textTopLeft = textOverlayView.getTextTopLeft();

        // text location in ImageView bitmap coordinates
        PointF textViewTopLeftBitmap = mImageView.transformZoomedCoordToBitmapCoord(
                textViewTopLeft.x, textViewTopLeft.y);
        PointF textViewBottomLeftBitmap = mImageView.transformZoomedCoordToBitmapCoord(
                textViewBottomLeft.x, textViewBottomLeft.y);
        PointF textTopLeftBitmap = mImageView.transformZoomedCoordToBitmapCoord(
                textTopLeft.x, textTopLeft.y);
        float textViewHeightBitmap = textViewBottomLeftBitmap.y - textViewTopLeftBitmap.y;
        float textViewPaddingLeftBitmap = textTopLeftBitmap.x - textViewTopLeftBitmap.x;
        float textViewPaddingTopBitmap = textTopLeftBitmap.y - textViewTopLeftBitmap.y;

        // text location in original bitmap coordinates
        float scale = (float) bitmap.getWidth() / mImageView.getDrawable().getIntrinsicWidth();
        float x = textViewTopLeftBitmap.x * scale;
        float y = textViewTopLeftBitmap.y * scale;
        float height = textViewHeightBitmap * scale;
        float paddingLeft = textViewPaddingLeftBitmap * scale;
        float paddingTop = textViewPaddingTopBitmap * scale;


        // recreate text view with correct size
        ScalableTextView textView = textOverlayView.getTextViewCopy();
        textView.setPadding((int)paddingLeft, (int)paddingTop, (int)paddingLeft, (int)paddingTop);
        setTextSizeToMatchHeight(textView, height);
        layoutTextView(textView);

        // set stroke
        float fontSizePx = textView.getTextSize();
        float fontSizeSp = convertPxToSp(fontSizePx);
        float strokeWidthMultiplier = textOverlayView.getStrokeWidthMultiplier();
        textView.setStrokeWidth(fontSizeSp * strokeWidthMultiplier);

        // set shadow
        if (textView.getShadowRadius() > 0 && textView.getShadowColor() != Color.TRANSPARENT) {
            float radius = fontSizePx * textOverlayView.getShadowRadiusMultiplier();
            float dx = fontSizePx * textOverlayView.getShadowDxMultiplier();
            float dy = fontSizePx * textOverlayView.getShadowDyMultiplier();
            textView.setShadowLayer(radius, dx, dy, textView.getShadowColor());
        }

        // bg corner radius
        if (textView.getRoundBackgroundColor() != Color.TRANSPARENT) {
            textView.setRoundBackgroundCornerRadius(fontSizeSp * textOverlayView.getBgCornerRadiusMultiplier());
        }

        // draw text on bitmap
        Bitmap bitmapOut = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapOut);

        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.translate(x, y);
        textView.draw(canvas);

        return bitmapOut;
    }

    private boolean isCloseEnough(float currentHeight, float desiredHeight) {
        return Math.abs(currentHeight - desiredHeight) <= 2;
    }

    private void setTextSizeToMatchHeight(ScalableTextView textView, float desiredHeight) {
        int step = 50;
        float low;
        float high = 0;
        do {
            low = high;
            high += step;
            textView.setTextSize(high);
            measureTextView(textView);
        } while (textView.getMeasuredHeight() < desiredHeight);

        float sizeToTry;
        int count = 0;
        while (!isCloseEnough(textView.getMeasuredHeight(), desiredHeight)) {
            sizeToTry = (low + high) / 2;
            textView.setTextSize(sizeToTry);
            measureTextView(textView);
            if (textView.getMeasuredHeight() < desiredHeight) {
                low = sizeToTry;
            } else {
                high = sizeToTry;
            }

            // the font can't get the size close enough in some situations
            // so make a way to exit if needed
            float highLowDiff = high - low;
            count++;
            if (count > 20 || highLowDiff < 0.1)
                return;
        }
    }

    private void measureTextView(ScalableTextView textView) {
        textView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    }

    private void layoutTextView(ScalableTextView textView) {
        int tvWidth = textView.getMeasuredWidth();
        int tvHeight = textView.getMeasuredHeight();

        // text view
        int left = textView.getLeft();
        int top = textView.getTop();
        int right = left + tvWidth;
        int bottom = top + tvHeight;
        textView.layout(left, top, right, bottom);
    }

    private float convertPxToSp(float sizePx) {
        return sizePx / getResources().getDisplayMetrics().scaledDensity;
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
        if (view == null) return;

        // hide if already selected
        if (selectedView == view &&
                rlColorChooser.getVisibility() == View.VISIBLE) {
            hideSettingBars();
            return;
        }
        selectedView = view;

        // show setting bars
        rlColorChooser.setVisibility(View.VISIBLE);
        unselectAllItems();

        final int id = view.getId();
        if (id == R.id.fl_photo_overlay_text_color) {
            setTextColorSettingsVisibility();
        } else if (id == R.id.fl_photo_overlay_font) {
            setFontSettingsVisibility();
        } else if (id == R.id.fl_photo_overlay_border) {
            setBorderSettingsVisibility();
        } else if (id == R.id.fl_photo_overlay_shadow) {
            setShadowSettingsVisibility();
        } else if (id == R.id.fl_photo_overlay_background) {
            setBgSettingsVisibility();
        }
    }

    private void setTextColorSettingsVisibility() {
        ivColor.setSelected(true);
        flCancelColorButton.setVisibility(View.VISIBLE);
        alphaSeekBar.setVisibility(View.VISIBLE);
        llSeekBars.setVisibility(View.INVISIBLE);
        rightSeekBar.setVisibility(View.GONE);
        updateProgressWithoutSettingValue(alphaSeekBar, textOpacitySeekBarProgress);
    }

    private void setFontSettingsVisibility() {
        ivFont.setSelected(true);
        flCancelColorButton.setVisibility(View.GONE);
        alphaSeekBar.setVisibility(View.GONE);
        llSeekBars.setVisibility(View.INVISIBLE);
        rightSeekBar.setVisibility(View.GONE);
    }

    private void setBorderSettingsVisibility() {
        ivBorder.setSelected(true);
        flCancelColorButton.setVisibility(View.VISIBLE);
        alphaSeekBar.setVisibility(View.VISIBLE);
        llSeekBars.setVisibility(View.VISIBLE);
        rightSeekBar.setVisibility(View.GONE);
        updateProgressWithoutSettingValue(alphaSeekBar, borderOpacitySeekBarProgress);
        updateProgressWithoutSettingValue(leftSeekBar, strokeSeekBarProgress);
    }

    private void updateProgressWithoutSettingValue(SeekBar seekBar, int progress) {
        seekBar.setOnSeekBarChangeListener(null);
        seekBar.setProgress(progress);
        if (seekBar == leftSeekBar)
            seekBar.setOnSeekBarChangeListener(leftSeekBarChangeListener);
        else if (seekBar == rightSeekBar)
            seekBar.setOnSeekBarChangeListener(rightSeekBarListener);
        else if (seekBar == alphaSeekBar)
            seekBar.setOnSeekBarChangeListener(alphaSeekBarChangeListener);
    }

    private void setShadowSettingsVisibility() {
        ivShadow.setSelected(true);
        flCancelColorButton.setVisibility(View.VISIBLE);
        alphaSeekBar.setVisibility(View.VISIBLE);
        llSeekBars.setVisibility(View.VISIBLE);
        rightSeekBar.setVisibility(View.VISIBLE);
        updateProgressWithoutSettingValue(alphaSeekBar, shadowOpacitySeekBarProgress);
        updateProgressWithoutSettingValue(leftSeekBar, shadowLeftSeekBarProgress);
        updateProgressWithoutSettingValue(rightSeekBar, shadowRightSeekBarProgress);
    }

    private void setBgSettingsVisibility() {
        ivBackground.setSelected(true);
        flCancelColorButton.setVisibility(View.VISIBLE);
        alphaSeekBar.setVisibility(View.VISIBLE);
        llSeekBars.setVisibility(View.VISIBLE);
        rightSeekBar.setVisibility(View.VISIBLE);
        updateProgressWithoutSettingValue(alphaSeekBar, backgroundOpacitySeekBarProgress);
        updateProgressWithoutSettingValue(leftSeekBar, backgroundPaddingSeekBarProgress);
        updateProgressWithoutSettingValue(rightSeekBar, backgroundCornerRadiusSeekBarProgress);
    }

    private void hideSettingBars() {
        rlColorChooser.setVisibility(View.INVISIBLE);
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
        int alpha = getAlphaFromSeekBarProgress(alphaSeekBar.getProgress());
        if (alpha == 0) alpha = 0xff;
        int colorWithAlpha = getColorWithAlpha(alpha, color);

        final int id = selectedView.getId();
        if (id == R.id.fl_photo_overlay_text_color) {
            textOverlayView.setTextColor(colorWithAlpha);
        } else if (id == R.id.fl_photo_overlay_border) {
            textOverlayView.setStrokeColor(colorWithAlpha);
            float multiplier = getStrokeMultiplierFromSeekBarProgress(leftSeekBar.getProgress());
            textOverlayView.setStrokeWidthMultiplier(multiplier);
        } else if (id == R.id.fl_photo_overlay_shadow) {
            setShadowColor(colorWithAlpha);
        } else if (id == R.id.fl_photo_overlay_background) {
            textOverlayView.setRoundBackgroundColor(colorWithAlpha);
        }
    }

    public void onCancelColorToolbarItemClick(View view) {
        int color = Color.TRANSPARENT;

        final int id = selectedView.getId();
        if (id == R.id.fl_photo_overlay_text_color) {
            textOverlayView.setTextColor(color);
        } else if (id == R.id.fl_photo_overlay_border) {
            textOverlayView.setStrokeColor(color);
        } else if (id == R.id.fl_photo_overlay_shadow) {
            textOverlayView.setShadowLayerMultipliers(0, 0, 0, color);
        } else if (id == R.id.fl_photo_overlay_background) {
            textOverlayView.setRoundBackgroundColor(color);
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

    private static class SharePhoto extends AsyncTask<Void, Void, Intent> {

        WeakReference<PhotoOverlayActivity> activityReference;
        private AlertDialog progress;

        SharePhoto(PhotoOverlayActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            PhotoOverlayActivity activity = activityReference.get();
            showProgressDialog(activity);
        }

        @SuppressLint("InflateParams")
        void showProgressDialog(PhotoOverlayActivity activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final View customLayout = activity.getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            builder.setView(customLayout);
            progress = builder.create();
            progress.show();
        }

        @Override
        protected Intent doInBackground(Void... params) {
            PhotoOverlayActivity activity = activityReference.get();
            Bitmap bitmap = activity.renderBitmap();
            return FileUtils.getShareImageIntent(activity, bitmap);
        }

        @Override
        protected void onPostExecute(Intent shareIntent) {
            if (shareIntent == null) return;
            PhotoOverlayActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            activity.startActivity(Intent.createChooser(shareIntent, null));
        }
    }

    private static class SavePhoto extends AsyncTask<Void, Void, String> {

        WeakReference<PhotoOverlayActivity> activityReference;
        private AlertDialog progress;

        SavePhoto(PhotoOverlayActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            PhotoOverlayActivity activity = activityReference.get();
            showProgressDialog(activity);
        }

        @SuppressLint("InflateParams")
        void showProgressDialog(PhotoOverlayActivity activity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            final View customLayout = activity.getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            builder.setView(customLayout);
            progress = builder.create();
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            PhotoOverlayActivity activity = activityReference.get();
            Bitmap bitmap = activity.renderBitmap();
            return FileUtils.saveOverlayPhoto(activity, bitmap);
        }

        @Override
        protected void onPostExecute(String savedFileNameName) {
            PhotoOverlayActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
            if (TextUtils.isEmpty(savedFileNameName)) {
                MongolToast.makeText(activity, R.string.couldnt_be_saved, MongolToast.LENGTH_SHORT).show();
            } else {
                notifyUserOfPhotoLocation(activity, savedFileNameName);
            }
        }

        private void notifyUserOfPhotoLocation(Context context, String pathName) {
            MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(context);
            builder.setMessage(context.getString(R.string.alert_where_to_find_photo, pathName));
            builder.setPositiveButton(context.getString(R.string.dialog_got_it), null);
            MongolAlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
