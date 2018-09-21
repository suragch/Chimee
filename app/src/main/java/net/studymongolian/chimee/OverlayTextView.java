package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.studymongolian.mongollibrary.MongolTextView;

import static android.content.ContentValues.TAG;

public class OverlayTextView extends ViewGroup {


    private static final int PADDING_PX = 2;
    private static final float TEXT_PADDING_DP = 5;
    private static final int BORDER_SIZE_PX = 1;
    private static final float BOX_CONTROL_SIZE_DP = 10;
    private static final float CONTROL_TOUCH_AREA_SIZE_DP = 48;

    boolean hasFocus = true;

    private ScalableTextView mTextView;
    private TextSizeControl xScaleControl;
    private TextSizeControl yScaleControl;
    private TextSizeControl fontSizeControl;
    private float xScale = 1f;
    private float yScale = 1f;

    private float dX;
    private float dY;
    private int textPadding;
    private int boxControlSize;
    private Paint backgroundPaint;
    private Paint borderPaint;
    //private Paint fillPaint;
    private int controlTouchAreaSize;
    private CharSequence mText;
    //int lastAction;

    public OverlayTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setDpValues();
        //int paddingLeft = (int) (textPadding + 2 * BORDER_SIZE_PX + EXTRA_PADDING_PX);
        //int paddingTopAndRight = (int) (paddingLeft + controlTouchAreaSize / 2);
        //int paddingBottom = (int) (paddingLeft + controlTouchAreaSize / 2);

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
        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.white_5));
        backgroundPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_SIZE_PX);
    }

    private void setupTextView(Context context) {
        mText = "";
        mTextView = new ScalableTextView(context);
        mTextView.setText(mText);
        mTextView.setPadding(textPadding, textPadding, textPadding, textPadding);
        addView(mTextView);
    }

    private void setupControls(Context context) {
        xScaleControl = new TextSizeControl(context);
        yScaleControl = new TextSizeControl(context);
        fontSizeControl = new TextSizeControl(context);

        xScaleControl.setControlType(TextSizeControl.ControlType.BOX);
        yScaleControl.setControlType(TextSizeControl.ControlType.BOX);
        fontSizeControl.setControlType(TextSizeControl.ControlType.CIRCLE);

        xScaleControl.setVisibleItemSize(boxControlSize);
        yScaleControl.setVisibleItemSize(boxControlSize);
        fontSizeControl.setVisibleItemSize(2 * boxControlSize);

        xScaleControl.setOnTouchListener(xScaleTouchListener);
        yScaleControl.setOnTouchListener(yScaleTouchListener);
        //fontSizeControl.setOnTouchListener(fontSizeTouchListener);

        addView(xScaleControl);
        addView(yScaleControl);
        addView(fontSizeControl);
    }

    OnTouchListener xScaleTouchListener = new OnTouchListener() {

        //private int rightEdgeStart;
        //private float downX;
        float dx;
        int[] textViewLocation = new int[2];

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mTextView.getLocationOnScreen(textViewLocation);
                    int rightEdgeStart = mTextView.getRight() + textViewLocation[0];
                    dx = rightEdgeStart - event.getRawX();
//                    Log.i(TAG, "DOWN rightEdgeStart: " + rightEdgeStart);
//                    Log.i(TAG, "DOWN downX: " + event.getRawX());
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float desiredWidth = event.getRawX() + dx - textViewLocation[0];
                    float scale = desiredWidth/mTextView.getUnscaledWidth();
//                    Log.i(TAG, "MOVE dx: " + dx);
//                    Log.i(TAG, "MOVE event.getRawX(): " + event.getRawX());
//                    Log.i(TAG, "MOVE desiredWidth: " + desiredWidth);
//                    Log.i(TAG, "MOVE getUnscaledWidth: " + mTextView.getUnscaledWidth());
//                    Log.i(TAG, "MOVE scale: " + scale);
//                    Log.i(TAG, "MOVE -------------- ");
                    mTextView.setScaleX(scale);
                    //verlayTextView.this.invalidate();
                    //OverlayTextView.this.requestLayout();
                    return true;
            }
            return true;
        }
    };

    OnTouchListener yScaleTouchListener = new OnTouchListener() {
        // distance of point (m,n) to line y=-x is (m+n)/√2
        int lastDistance;
        float lastY;
        float dy;
        int[] textViewLocation = new int[2];

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mTextView.getLocationOnScreen(textViewLocation);
                    int bottomEdgeStart = mTextView.getBottom() + textViewLocation[1];
                    dy = bottomEdgeStart - event.getRawY();
                    lastY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float desiredHeight = event.getRawY() + dy - textViewLocation[1];
                    int currentHeight = mTextView.getHeight();
                    float thisY = event.getY();
                    if (thisY > lastY && desiredHeight > currentHeight)
                        increaseFontSize();
                    else if (thisY < lastY && desiredHeight < currentHeight)
                        decreaseFontSize();
                    return true;
            }
            return true;
        }

        private void increaseFontSize() {
            float currentTextSize = convertPxToSp(mTextView.getTextSize());
            //if (currentTextSize >= 100) return;
            mTextView.setTextSize(currentTextSize + 1);
        }

        private void decreaseFontSize() {
            float currentTextSize = convertPxToSp(mTextView.getTextSize());
            mTextView.setTextSize(currentTextSize - 1);
        }


        private int getDistance(MotionEvent event) {
            return (int)((event.getX() + event.getY())/Math.sqrt(2));
        }
    };

    OnTouchListener fontSizeTouchListener = new OnTouchListener() {
        // distance of point (m,n) to line y=-x is (m+n)/√2
        int lastDistance;
        float lastY;
        float dy;
        int[] textViewLocation = new int[2];

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mTextView.getLocationOnScreen(textViewLocation);
                    int bottomEdgeStart = mTextView.getBottom() + textViewLocation[1];
                    dy = bottomEdgeStart - event.getRawY();
                    lastY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float desiredHeight = event.getRawY() + dy - textViewLocation[1];
                    int currentHeight = mTextView.getHeight();
                    float thisY = event.getY();
                    if (thisY > lastY && desiredHeight > currentHeight)
                        increaseFontSize();
                    else if (thisY < lastY && desiredHeight < currentHeight)
                        decreaseFontSize();
                    return true;
            }
            return true;
        }

        private void increaseFontSize() {
            float currentTextSize = convertPxToSp(mTextView.getTextSize());
            //if (currentTextSize >= 100) return;
            mTextView.setTextSize(currentTextSize + 1);
        }

        private void decreaseFontSize() {
            float currentTextSize = convertPxToSp(mTextView.getTextSize());
            mTextView.setTextSize(currentTextSize - 1);
        }


        private int getDistance(MotionEvent event) {
            return (int)((event.getX() + event.getY())/Math.sqrt(2));
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

        // scale y control
        left = getPaddingLeft() + (tvWidth - controlTouchAreaSize) / 2;
        top = getPaddingTop() + tvHeight - controlTouchAreaSize / 2;
        right = left + controlTouchAreaSize;
        bottom = top + controlTouchAreaSize;
        yScaleControl.measure(
                MeasureSpec.makeMeasureSpec(controlTouchAreaSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(controlTouchAreaSize, MeasureSpec.EXACTLY));
        yScaleControl.layout(left, top, right, bottom);

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
        super.onDraw(canvas);
        drawBackground(canvas);
        if (hasFocus) {
            drawBorder(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                startScaleX = getScaleX();
//                startScaleY = getScaleY();
                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();
                if (!hasFocus) {
                    hasFocus = true;
                    showControls();
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
//                if (isStretchEvent(event)) {
//                    resizeView(event);
//                } else if (isChangeHeightEvent(event)) {
//                    changeHeight(event);
//                } else {
                    moveView(event);
//                }
                return true;

            case MotionEvent.ACTION_UP:
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void showControls() {
        xScaleControl.setVisibility(VISIBLE);
        yScaleControl.setVisibility(VISIBLE);
        fontSizeControl.setVisibility(VISIBLE);
    }


    private void hideControls() {
        xScaleControl.setVisibility(INVISIBLE);
        yScaleControl.setVisibility(INVISIBLE);
        fontSizeControl.setVisibility(INVISIBLE);

    }

    //    }

    //    private boolean isStretchEvent(MotionEvent event) {
//    }
    //        int touchAreaLeft = getWidth() - controlTouchAreaSize;
    //        int touchAreaTop = getHeight() - controlTouchAreaSize;
    //        return event.getX() > touchAreaLeft && event.getY() > touchAreaTop;
//
//    private boolean isChangeHeightEvent(MotionEvent event) {
//        int topOfTouchArea = getHeight() - (int) (controlTouchAreaSize) - EXTRA_PADDING_PX;
//        return event.getY() > topOfTouchArea;
//    }
//
//    private float startScaleX, startScaleY;
//
//    private void resizeView(MotionEvent event) {
//        float newScaleX = startScaleX * (getX() - event.getRawX())/dX;
//        float newScaleY = startScaleY * (getY() - event.getRawY())/dY;
//
//        //scale
//        setScaleX(newScaleX);
//        setScaleY(newScaleY);
//        TextViewCompat
//
//    }
//
//    private void changeHeight(MotionEvent event) {
//        Log.i(TAG, "changeHeight: ");
//    }
//
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
        //invalidate();
        //requestLayout();
    }
}

