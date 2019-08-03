package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircleView extends View {

    private static final float BORDER_WIDTH_NORMAL = 3;
    private static final float BORDER_WIDTH_PRESSED = 3;
    private static final float CIRCLE_PROPORTION_OF_SIZE = 0.7f;
    private static final int BORDER_PRESSED_NORMAL_COLOR = Color.BLACK;
    private boolean isPressed;

    private Paint fillPaint;
    private Paint borderPaint;
    private int mBorderPressedColor = Color.WHITE;

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_WIDTH_NORMAL);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(mBorderPressedColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawColor(canvas);
        drawBorder(canvas);
    }

    private void drawColor(Canvas canvas) {
        int x = getWidth() / 2;
        int y = getWidth() / 2;
        float radius = getWidth() * CIRCLE_PROPORTION_OF_SIZE / 2;
        canvas.drawCircle(x, y, radius, fillPaint);
    }

    private void drawBorder(Canvas canvas) {
        if (isPressed) {
            borderPaint.setColor(mBorderPressedColor);
            borderPaint.setStrokeWidth(BORDER_WIDTH_PRESSED);
        } else {
            borderPaint.setStrokeWidth(BORDER_WIDTH_NORMAL);
            borderPaint.setColor(BORDER_PRESSED_NORMAL_COLOR);
        }
        int x = getWidth() / 2;
        int y = getWidth() / 2;
        float radius = getWidth() * CIRCLE_PROPORTION_OF_SIZE / 2;
        canvas.drawCircle(x, y, radius, borderPaint);
    }

    public void setColor(int color) {
        fillPaint.setColor(color);
        invalidate();
    }

    public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            default:
                setPressed(false);
        }
        return super.onTouchEvent(event);
    }
}
