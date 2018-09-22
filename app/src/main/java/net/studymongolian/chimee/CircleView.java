package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircleView extends View implements View.OnClickListener {

    private static final float BORDER_WIDTH_NORMAL = 3;
    private static final float BORDER_WIDTH_PRESSED = 3;
    private static final float CIRCLE_PROPORTION_OF_SIZE = 0.7f;
    private int mColor;
    private boolean isPressed;

    private Paint fillPaint;
    private Paint borderPaint;
    private int mBorderPressedColor = Color.WHITE;
    private int mBorderNormalColor = Color.BLACK;

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
            borderPaint.setColor(mBorderNormalColor);
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

    public void setBorderPressedColor(int color) {
        borderPaint.setColor(color);
    }

    public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //super.onTouchEvent(event);
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
        return false;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    @Override
    public void onClick(View v) {

    }
}
