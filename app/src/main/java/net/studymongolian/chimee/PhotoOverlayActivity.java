package net.studymongolian.chimee;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private static final float SHADOW_SPIRAL_ANGLE_RADIANS_MAX = (float) (6 * Math.PI);
    private static final float SHADOW_RADIUS_MULTIPLIER_MIN = 0.01f;
    private static final float SHADOW_RADIUS_MULTIPLIER_MAX = 0.2f;
    private static final float DEFAULT_SHADOW_RADIUS_MULTIPLIER = 0.1f;
    private static final float BG_CORNER_RADIUS_MULTIPLIER_MIN = 0;
    private static final float BG_CORNER_RADIUS_MULTIPLIER_MAX = 1f;
    private static final int BG_OPACITY_MIN = 0x00;
    private static final int BG_OPACITY_MAX = 0xff;
    private static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_SHADOW_COLOR = Color.BLACK;
    private static final int DEFAULT_BG_COLOR = Color.LTGRAY;
    private static final int DEFAULT_BG_ALPHA = 0x80;

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

        private void updateBackgroundOpacityFromProgress(int progress) {
            backgroundLeftSeekBarProgress = progress;
            int color = textOverlayView.getRoundBackgroundColor();
            int alpha = getBackgroundAlphaFromSeekBarProgress(progress);
            if (color == 0)
                color = DEFAULT_BG_COLOR;
            textOverlayView.setRoundBackgroundColor(alpha, color);
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

    private int getBackgroundAlphaFromSeekBarProgress(int progress) {
        return BG_OPACITY_MIN
                + (BG_OPACITY_MAX - BG_OPACITY_MIN) * progress / 100;
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

            int color = textOverlayView.getRoundBackgroundColor();
            if (color == Color.TRANSPARENT)
                textOverlayView.setRoundBackgroundColor(DEFAULT_BG_ALPHA, DEFAULT_BG_COLOR);

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
        if (llColorChooser.getVisibility() == View.VISIBLE)
            hideSettingBars();
        else {
            super.onBackPressed();
        }
    }

    private void savePhoto() {

    }

    private void sharePhoto() {
        Bitmap bitmap = renderBitmap();
        mImageView.setImageBitmap(bitmap);
        //Intent shareIntent = FileUtils.getShareImageIntent(this, bitmap);
        //startActivity(Intent.createChooser(shareIntent, null));
    }

    private Bitmap renderBitmap() {

        // text location in ImageView coordinates
        PointF textViewTopLeft = textOverlayView.getTextViewTopLeft();
        PointF textViewTopRight = textOverlayView.getTextViewTopRight();
        PointF textViewBottomLeft = textOverlayView.getTextViewBottomLeft();
        PointF textTopLeft = textOverlayView.getTextTopLeft();

        // text location in ImageView bitmap coordinates
        PointF textViewTopLeftBitmap = mImageView.transformZoomedCoordToBitmapCoord(
                textViewTopLeft.x, textViewTopLeft.y);
        PointF textViewTopRightBitmap = mImageView.transformZoomedCoordToBitmapCoord(
                textViewTopRight.x, textViewTopRight.y);
        PointF textViewBottomLeftBitmap = mImageView.transformZoomedCoordToBitmapCoord(
                textViewBottomLeft.x, textViewBottomLeft.y);
        PointF textTopLeftBitmap = mImageView.transformZoomedCoordToBitmapCoord(
                textTopLeft.x, textTopLeft.y);
        float textViewHeightBitmap = textViewBottomLeftBitmap.y - textViewTopLeftBitmap.y;
        float textViewWidthBitmap = textViewTopRightBitmap.x - textViewTopLeftBitmap.x;
        float textViewPaddingLeftBitmap = textTopLeftBitmap.x - textViewTopLeftBitmap.x;
        float textViewPaddingTopBitmap = textTopLeftBitmap.y - textViewTopLeftBitmap.y;

        // text location in original bitmap coordinates
        float scale = (float) bitmap.getWidth() / mImageView.getDrawable().getIntrinsicWidth();
        float x = textViewTopLeftBitmap.x * scale;
        float y = textViewTopLeftBitmap.y * scale;
        float height = textViewHeightBitmap * scale;
        float width = textViewWidthBitmap * scale;
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
            //float bgPadding = ScalableTextView.BG_PADDING_PX;
            //textView.setRight(textView.getLeft() + (int) width);
            //textView.setBottom(textView.getTop() + (int) height);
            textView.setRoundBackgroundCornerRadius(fontSizeSp * textOverlayView.getBgCornerRadiusMultiplier());
        }

        // draw text on bitmap
        Bitmap bitmapOut = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapOut);

        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.translate(x, y);
//        canvas.drawRoundRect(new RectF(0, 0, width, height),
//                textView.getRoundBackgroundCornerRadius(),
//                textView.getRoundBackgroundCornerRadius(), textView.getBgPaint());
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

    private void colorBackground(Canvas canvas) {
        int color = Color.BLUE;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
    }

    public Bitmap drawTextToBitmap(Context mContext,  int resourceId,  String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
            Bitmap.Config bitmapConfig =   bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are immutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(110,110, 110));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/6;
            int y = (bitmap.getHeight() + bounds.height())/5;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception

            return null;
        }

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
                int alpha = getBackgroundAlphaFromSeekBarProgress(leftSeekBar.getProgress());
                textOverlayView.setRoundBackgroundColor(alpha, color);
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
                int alpha = 0;
                textOverlayView.setRoundBackgroundColor(alpha, color);
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
