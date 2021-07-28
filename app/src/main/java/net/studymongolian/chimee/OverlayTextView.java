package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class OverlayTextView extends ViewGroup {


    private static final int PADDING_PX = 2;
    static final float TEXT_PADDING_DP = 10;
    private static final int BORDER_SIZE_PX = 1;
    private static final float BOX_CONTROL_SIZE_DP = 10;
    private static final float CONTROL_TOUCH_AREA_SIZE_DP = 48;
    private static final int MIN_TEXT_LINES_FOR_LINE_SPACING = 3;
    private static final float LINE_SPACING_ADJUSTMENT_DP = 5;

    boolean hasFocus = true;

    private ScalableTextView mTextView;
    private TextSizeControl xScaleControl;
    private TextSizeControl fontSizeControl;

    private float dX;
    private float dY;
    private int textPadding;
    private int boxControlSize;
    private Paint borderPaint;
    private int controlTouchAreaSize;
    public float mStrokeMultiplier = 0;
    private float mShadowRadiusMultiplier = 0;
    private float mShadowDxMultiplier = 0;
    private float mShadowDyMultiplier = 0;
    private float mBgCornerRadiusMultiplier = 0;
    private int lineSpacingPx;

    public OverlayTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setDpValues();
        setPadding(PADDING_PX, PADDING_PX, PADDING_PX, PADDING_PX);
        initPaint();
        setupTextView(context);
        setupControls(context);
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void setDpValues() {
        textPadding = convertDpToPx(TEXT_PADDING_DP);
        controlTouchAreaSize = convertDpToPx(CONTROL_TOUCH_AREA_SIZE_DP);
        boxControlSize = convertDpToPx(BOX_CONTROL_SIZE_DP);
        lineSpacingPx = convertDpToPx(LINE_SPACING_ADJUSTMENT_DP);
    }

    private int convertDpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private float convertPxToSp(float sizePx) {
        return sizePx / getResources().getDisplayMetrics().scaledDensity;
    }

    private float convertSpToPx(float sizeSp) {
        return sizeSp * getResources().getDisplayMetrics().scaledDensity;
    }

    private void initPaint() {
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_SIZE_PX);
        borderPaint.setAntiAlias(true);
    }

    private void setupTextView(Context context) {
        mTextView = new ScalableTextView(context);
        mTextView.setText("");
        mTextView.setPadding(textPadding, textPadding, textPadding, textPadding);
        addView(mTextView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupControls(Context context) {
        xScaleControl = new TextSizeControl(context);
        fontSizeControl = new TextSizeControl(context);

        xScaleControl.setControlType(TextSizeControl.ControlType.BOX);
        fontSizeControl.setControlType(TextSizeControl.ControlType.CIRCLE);

        xScaleControl.setVisibleItemSize(boxControlSize);
        fontSizeControl.setVisibleItemSize(2 * boxControlSize);

        xScaleControl.setOnTouchListener(xScaleTouchListener);
        fontSizeControl.setOnTouchListener(fontSizeTouchListener);

        addView(xScaleControl);
        addView(fontSizeControl);
    }

    OnTouchListener xScaleTouchListener = new OnTouchListener() {

        float dx;
        final int[] textViewLocation = new int[2];
        float oldDesiredWidth;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mTextView.getLocationOnScreen(textViewLocation);
                    int rightEdgeStart = mTextView.getRight() + textViewLocation[0];
                    dx = rightEdgeStart - event.getRawX();
                    oldDesiredWidth = getDesiredWidth(event);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float desiredWidth = getDesiredWidth(event);
                    if (mTextView.getLineCount() >= MIN_TEXT_LINES_FOR_LINE_SPACING) {
                        setLineSpacing(desiredWidth);
                    } else {
                        scaleText(desiredWidth);
                    }
                    oldDesiredWidth = desiredWidth;
                    return true;
            }
            return true;
        }

        private float getDesiredWidth(MotionEvent event) {
            return event.getRawX() + dx - textViewLocation[0];
        }

        private void setLineSpacing(float desiredWidth) {
            float currentLineSpacing = mTextView.getLineSpacingExtra();
            int numLines = mTextView.getLineCount();
            float adjustment = lineSpacingPx / (float) (numLines - 1);
            if (oldDesiredWidth < desiredWidth) {
                mTextView.setLineSpacing(currentLineSpacing + adjustment, 1);
            } else {
                mTextView.setLineSpacing(currentLineSpacing - adjustment, 1);
            }
        }

        private void scaleText(float desiredWidth) {
            float scale = desiredWidth / mTextView.getUnscaledWidth();
            mTextView.setScaleX(scale);
        }
    };

    OnTouchListener fontSizeTouchListener = new OnTouchListener() {

        float lastY;
        float dy;
        final int[] textViewLocation = new int[2];

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mTextView.getLocationOnScreen(textViewLocation);
                    int bottomEdgeStart = mTextView.getBottom() + textViewLocation[1];
                    dy = bottomEdgeStart - event.getRawY();
                    lastY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float desiredHeight = event.getRawY() + dy - textViewLocation[1];
                    updateTextSizeForDesiredHeight(desiredHeight);
                    return true;
            }
            return true;
        }
    };

    private void updateTextSizeForDesiredHeight(float height) {
        int currentHeight = mTextView.getHeight();
        float scale = height / currentHeight;
        float fontSizeSp = convertPxToSp(mTextView.getTextSize());
        updateTextSize(fontSizeSp * scale);
    }

    public void updateTextSize(float fontSizeSp) {
        // text
        mTextView.setTextSize(fontSizeSp);
        // stroke
        mTextView.setStrokeWidth(fontSizeSp * mStrokeMultiplier);
        // shadow
        if (mTextView.getShadowRadius() > 0 && mTextView.getShadowColor() != Color.TRANSPARENT) {
            float sizePx = convertSpToPx(fontSizeSp);
            float radius = sizePx * mShadowRadiusMultiplier;
            float dx = sizePx * mShadowDxMultiplier;
            float dy = sizePx * mShadowDyMultiplier;
            mTextView.setShadowLayer(radius, dx, dy, mTextView.getShadowColor());
        }
        // bg corner radius
        if (mTextView.getRoundBackgroundColor() != Color.TRANSPARENT) {
            mTextView.setRoundBackgroundCornerRadius(fontSizeSp * mBgCornerRadiusMultiplier);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        int width = getPaddingLeft()
                + mTextView.getMeasuredWidth()
                + controlTouchAreaSize
                + getPaddingRight();
        int height = getPaddingTop()
                + mTextView.getMeasuredHeight()
                + controlTouchAreaSize
                + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int tvWidth = mTextView.getMeasuredWidth();
        int tvHeight = mTextView.getMeasuredHeight();

        // text view
        int left = getTextViewLeft();
        int top = getTextViewTop();
        int right = left + tvWidth;
        int bottom = top + tvHeight;
        mTextView.layout(left, top, right, bottom);

        // scale x control
        left = getPaddingLeft() + tvWidth - controlTouchAreaSize / 2 + BORDER_SIZE_PX;
        top = getPaddingTop() + (tvHeight - controlTouchAreaSize) / 2;
        right = left + controlTouchAreaSize;
        bottom = top + controlTouchAreaSize;
        xScaleControl.measure(
                MeasureSpec.makeMeasureSpec(controlTouchAreaSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(controlTouchAreaSize, MeasureSpec.EXACTLY));
        xScaleControl.layout(left, top, right, bottom);

        // font size control
        left = getMeasuredWidth() - controlTouchAreaSize - getPaddingRight();
        top = getMeasuredHeight() - controlTouchAreaSize - getPaddingBottom();
        right = left + controlTouchAreaSize;
        bottom = top + controlTouchAreaSize;
        fontSizeControl.measure(
                MeasureSpec.makeMeasureSpec(controlTouchAreaSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(controlTouchAreaSize, MeasureSpec.EXACTLY));
        fontSizeControl.layout(left, top, right, bottom);
    }

    // in local coordinates
    private int getTextViewTop() {
        return getPaddingTop();
    }

    // in local coordinates
    private int getTextViewLeft() {
        return getPaddingLeft();
    }

    // in local coordinates
    private int getTextViewBottom() {
        return getTextViewTop() + mTextView.getHeight();
        //return getPaddingLeft();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (hasFocus) {
            drawBorder(canvas);
        }
    }

    private void drawBorder(Canvas canvas) {

        int left = getTextViewLeft();
        int top = getTextViewTop();
        int right = left + mTextView.getWidth();
        int bottom = top + mTextView.getHeight();
        borderPaint.setColor(Color.BLACK);
        canvas.drawRect(left, top, right, bottom, borderPaint);
        borderPaint.setColor(Color.WHITE);
        canvas.drawRect(
                left - BORDER_SIZE_PX,
                top - BORDER_SIZE_PX,
                right + BORDER_SIZE_PX,
                bottom + BORDER_SIZE_PX,
                borderPaint);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();
                if (!hasFocus) {
                    hasFocus = true;
                    showControls();
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                moveView(event);
                return true;

            case MotionEvent.ACTION_UP:
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void showControls() {
        xScaleControl.setVisibility(VISIBLE);
        fontSizeControl.setVisibility(VISIBLE);
    }


    private void hideControls() {
        xScaleControl.setVisibility(INVISIBLE);
        fontSizeControl.setVisibility(INVISIBLE);

    }

    private void moveView(MotionEvent event) {
        setY(event.getRawY() + dY);
        setX(event.getRawX() + dX);
    }

    public void setFocused(boolean focused) {
        this.hasFocus = focused;
        invalidate();
        if (!focused) {
            hideControls();
        }
    }

    public CharSequence getText() {
        return mTextView.getText();
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public int getTextColor() {
        return mTextView.getTextColor();
    }

    public void setTypeface(Typeface typeface) {
        mTextView.setTypeface(typeface);
    }

    /**
     * this sets the stroke width as a percentage of the font size
     * @param multiplier a value usually from 0 to about 0.1 (0 means no stroke)
     */
    public void setStrokeWidthMultiplier(float multiplier) {
        mStrokeMultiplier = multiplier;
        float currentTextSize = convertPxToSp(mTextView.getTextSize());
        float strokeWidth = currentTextSize * multiplier;
        mTextView.setStrokeWidth(strokeWidth);
    }

    public float getStrokeWidthMultiplier() {
        return mStrokeMultiplier;
    }

    public int getStrokeColor() {
        return mTextView.getStrokeColor();
    }

    public void setStrokeColor(int color) {
        mTextView.setStrokeColor(color);
    }

    /**
     * this sets the shadow radius and offset as a percentage of the font size
     * @param radiusMultiplier a value usually from 0 to about 1 (0 means no shadow)
     * @param dxMultiplier x offset multiplier
     * @param dyMultiplier y offset multiplier
     * @param color shadow color, no multiplier needed
     */
    public void setShadowLayerMultipliers(
            float radiusMultiplier,
            float dxMultiplier,
            float dyMultiplier,
            int color) {

        mShadowRadiusMultiplier = radiusMultiplier;
        mShadowDxMultiplier = dxMultiplier;
        mShadowDyMultiplier = dyMultiplier;
        float textSizePx = mTextView.getTextSize();
        float shadowRadius = textSizePx * radiusMultiplier;
        float dx = textSizePx * dxMultiplier;
        float dy = textSizePx * dyMultiplier;
        mTextView.setShadowLayer(shadowRadius, dx, dy, color);
    }

    public void setShadowColor(int color) {
        float radius = mTextView.getShadowRadius();
        float dx = mTextView.getShadowDx();
        float dy = mTextView.getShadowDy();
        mTextView.setShadowLayer(radius, dx, dy, color);
    }

    public float getShadowRadiusMultiplier() {
        return mShadowRadiusMultiplier;
    }

    public int getShadowColor() {
        return mTextView.getShadowColor();
    }

    public float getShadowDxMultiplier() {
        return mShadowDxMultiplier;
    }

    public float getShadowDyMultiplier() {
        return mShadowDyMultiplier;
    }

    public float getTextSize() {
        return mTextView.getTextSize();
    }

    public int getRoundBackgroundColor() {
        return mTextView.getRoundBackgroundColor();
    }

    public float getBgCornerRadiusMultiplier() {
        return mBgCornerRadiusMultiplier;
    }

    /**
     * this sets the background corner radius as a percentage of the font size
     * @param multiplier a value usually from 0 to about 0.2 (0 means 90 degree corners)
     */
    public void setBackgroundCornerRadiusMultiplier(float multiplier) {
        mBgCornerRadiusMultiplier = multiplier;
        float currentTextSize = convertPxToSp(mTextView.getTextSize());
        float cornerRadius = currentTextSize * multiplier;
        mTextView.setRoundBackgroundCornerRadius(cornerRadius);
    }

    public void setRoundBackgroundColor(int color) {
        mTextView.setRoundBackgroundColor(color);
    }

    // in parent coordinates
    public PointF getTextViewTopLeft() {
        float x = getX() + getTextViewLeft();// + mTextView.getPaddingLeft();
        float y = getY() + getTextViewTop();// + mTextView.getPaddingTop();
        return new PointF(x, y);
    }

    // in parent coordinates
    public PointF getTextViewBottomLeft() {
        float x = getX() + getTextViewLeft();// + mTextView.getPaddingLeft();
        float y = getY() + getTextViewBottom();// - mTextView.getPaddingBottom();
        return new PointF(x, y);
    }

    public PointF getTextTopLeft() {
        float x = getX() + getTextViewLeft() + mTextView.getPaddingLeft();
        float y = getY() + getTextViewTop() + mTextView.getPaddingTop();
        return new PointF(x, y);
    }

    ScalableTextView getTextViewCopy() {
        ScalableTextView textView = new ScalableTextView(getContext());
        textView.setPadding(mTextView.getPaddingLeft(), mTextView.getPaddingTop(),
                mTextView.getPaddingRight(), mTextView.getPaddingBottom());
        textView.measure(
                MeasureSpec.makeMeasureSpec(mTextView.getWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mTextView.getHeight(), MeasureSpec.EXACTLY));
        textView.setLeft(mTextView.getLeft());
        textView.setTop(mTextView.getTop());
        textView.setRight(mTextView.getRight());
        textView.setBottom(mTextView.getBottom());
        textView.setText(mTextView.getText());
        textView.setTextSize(convertPxToSp(mTextView.getTextSize()));
        textView.setTypeface(mTextView.getTypeface());
        textView.setTextColor(mTextView.getTextColor());
        textView.setStrokeColor(mTextView.getStrokeColor());
        textView.setStrokeWidth(convertPxToSp(mTextView.getStrokeWidth()));
        textView.setShadowLayer(
                mTextView.getShadowRadius(),
                mTextView.getShadowDx(),
                mTextView.getShadowDy(),
                mTextView.getShadowColor());
        textView.setRoundBackgroundColor(mTextView.getRoundBackgroundColor());
        textView.setRoundBackgroundCornerRadius(mTextView.getRoundBackgroundCornerRadius());
        textView.setScaleX(mTextView.getScaleX());
        return textView;
    }

    public void setTextPaddingDp(int paddingDp) {
        int paddingPx = convertDpToPx(paddingDp);
        mTextView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
    }
}

