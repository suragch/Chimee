package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class OverlayTextView extends ViewGroup {


    private static final int PADDING_PX = 2;
    private static final float TEXT_PADDING_DP = 5;
    private static final int BORDER_SIZE_PX = 1;
    private static final float BOX_CONTROL_SIZE_DP = 10;
    private static final float CONTROL_TOUCH_AREA_SIZE_DP = 48;

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
    }

    private void setDpValues() {
        textPadding = convertDpToPx(TEXT_PADDING_DP);
        controlTouchAreaSize = convertDpToPx(CONTROL_TOUCH_AREA_SIZE_DP);
        boxControlSize = convertDpToPx(BOX_CONTROL_SIZE_DP);
    }

    private int convertDpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private float convertPxToSp(float sizePx) {
        return sizePx / getResources().getDisplayMetrics().scaledDensity;
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
        int[] textViewLocation = new int[2];

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mTextView.getLocationOnScreen(textViewLocation);
                    int rightEdgeStart = mTextView.getRight() + textViewLocation[0];
                    dx = rightEdgeStart - event.getRawX();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float desiredWidth = event.getRawX() + dx - textViewLocation[0];
                    float scale = desiredWidth / mTextView.getUnscaledWidth();
                    mTextView.setScaleX(scale);
                    return true;
            }
            return true;
        }
    };

    OnTouchListener fontSizeTouchListener = new OnTouchListener() {

        private static final int TOUCH_SLOP = 0;
        float lastY;
        float dy;
        int[] textViewLocation = new int[2];

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
                    int currentHeight = mTextView.getHeight();
                    float thisY = event.getRawY();
                    if (thisY > lastY + TOUCH_SLOP
                            && desiredHeight > currentHeight) {
                        increaseFontSize();
                        lastY = thisY;
                    } else if (thisY < lastY - TOUCH_SLOP
                            && desiredHeight < currentHeight) {
                        decreaseFontSize();
                        lastY = thisY;
                    }
                    return true;
            }
            return true;
        }

        private void increaseFontSize() {
            float currentTextSize = convertPxToSp(mTextView.getTextSize());
            mTextView.setTextSize(currentTextSize + 0.5f);
        }

        private void decreaseFontSize() {
            float currentTextSize = convertPxToSp(mTextView.getTextSize());
            mTextView.setTextSize(currentTextSize - 0.5f);
        }
    };

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
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = left + tvWidth;
        int bottom = top + tvHeight;
        mTextView.layout(left, top, right, bottom);

        // scale x control
        left = getPaddingLeft() + tvWidth - controlTouchAreaSize / 2;
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

    @Override
    protected void onDraw(Canvas canvas) {
        if (hasFocus) {
            drawBorder(canvas);
        }
    }

    private void drawBorder(Canvas canvas) {

        int left = getPaddingLeft();
        int top = getPaddingTop();
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

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
        //invalidate();
    }
}

