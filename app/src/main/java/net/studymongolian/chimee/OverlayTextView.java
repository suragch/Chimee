package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import net.studymongolian.mongollibrary.MongolTextView;

public class OverlayTextView extends MongolTextView {


    private static final int EXTRA_PADDING_PX = 2;
    private static final float TEXT_PADDING_DP = 5;
    private static final int BORDER_SIZE_PX = 1;
    private static final float CONTROL_TOUCH_AREA_SIZE_DP = 48;
    private static final float BOX_CONTROL_SIZE_DP = 10;

    boolean hasFocus = true;

    float dX;
    float dY;
    private float textPadding;
    private float boxControlSize;
    private Paint backgroundPaint;
    private Paint borderPaint;
    private Paint fillPaint;
    private float controlTouchAreaSize;
    //int lastAction;

    public OverlayTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setDpValues();
        int paddingLeft = (int) (textPadding + 2 * BORDER_SIZE_PX + EXTRA_PADDING_PX);
        int paddingTopAndRight = (int) (paddingLeft + controlTouchAreaSize / 2);
        int paddingBottom = (int) (paddingLeft + controlTouchAreaSize / 2);

        setPadding(paddingLeft, paddingTopAndRight, paddingTopAndRight, paddingBottom);
        initPaint();
    }

    private void setDpValues() {
        textPadding = convertDpToPx(TEXT_PADDING_DP);
        controlTouchAreaSize = convertDpToPx(CONTROL_TOUCH_AREA_SIZE_DP);
        boxControlSize = convertDpToPx(BOX_CONTROL_SIZE_DP);
    }

    private float convertDpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private void initPaint() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.white_5));
        backgroundPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_SIZE_PX);

        fillPaint = new Paint();
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);

        super.onDraw(canvas);
        if (hasFocus) {
            drawBorder(canvas);
            drawStretchControl(canvas);
            drawLineWrapHeightControl(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
    }

    private void drawBorder(Canvas canvas) {

        int left = (int) (getPaddingLeft() - textPadding - BORDER_SIZE_PX);
        int top = (int) (getPaddingTop() - textPadding - BORDER_SIZE_PX);
        int right = (int) (getWidth() - getPaddingRight() + textPadding + BORDER_SIZE_PX);
        int bottom = (int) (getHeight() - getPaddingBottom() + textPadding + BORDER_SIZE_PX);
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

    private void drawStretchControl(Canvas canvas) {
        int left = (int) (getWidth() - controlTouchAreaSize - EXTRA_PADDING_PX);
        int top = EXTRA_PADDING_PX;
        int right = getWidth() - EXTRA_PADDING_PX;
        int bottom = (int) (controlTouchAreaSize) + EXTRA_PADDING_PX;
        borderPaint.setColor(Color.BLACK);
        canvas.drawRect(left, top, right, bottom, borderPaint);

        left = (int) (getWidth() - controlTouchAreaSize / 2 - EXTRA_PADDING_PX - boxControlSize / 2);
        top = (int) (EXTRA_PADDING_PX + controlTouchAreaSize / 2 - boxControlSize / 2);
        canvas.drawRect(left, top, left + boxControlSize, top + boxControlSize, fillPaint);
        borderPaint.setColor(Color.BLACK);
        canvas.drawRect(left, top, left + boxControlSize, top + boxControlSize, borderPaint);
    }

    private void drawLineWrapHeightControl(Canvas canvas) {
        int left = (int) (getWidth() + getPaddingLeft() - getPaddingRight() - boxControlSize) / 2;
        int top = (int) (getHeight() - getPaddingBottom()
                + textPadding + BORDER_SIZE_PX - boxControlSize / 2);
        int right = left + (int) boxControlSize;
        int bottom = top + (int) boxControlSize;
        canvas.drawRect(left, top, right, bottom, fillPaint);
        borderPaint.setColor(Color.BLACK);
        canvas.drawRect(left, top, right, bottom, borderPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();
                if (!hasFocus) {
                    hasFocus = true;
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (isStretchEvent()) {
                    resizeView();
                } else if (isChangeHeightEvent()) {
                    changeHeight();
                } else {
                    moveView(event);
                }

                return true;

            case MotionEvent.ACTION_UP:
                return true;
        }

        return super.onTouchEvent(event);
    }

    private boolean isStretchEvent() {
        return false;
    }

    private boolean isChangeHeightEvent() {
        return false;
    }

    private void resizeView() {

    }

    private void changeHeight() {

    }

    private void moveView(MotionEvent event) {
        setY(event.getRawY() + dY);
        setX(event.getRawX() + dX);
    }

    public void setFocused(boolean focused) {
        this.hasFocus = focused;
        invalidate();
    }
}

