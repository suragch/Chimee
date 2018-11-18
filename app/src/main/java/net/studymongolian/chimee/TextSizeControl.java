package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class TextSizeControl extends View {

    private static final int BORDER_STROKE_WIDTH_PX = 1;

    private ControlType type = ControlType.BOX;
    private int visibleItemSize = 20;
    private Paint fillPaint;
    private Paint borderPaint;


    public TextSizeControl(Context context) {
        super(context);
        init();
    }

    private void init() {

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_STROKE_WIDTH_PX);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setAntiAlias(true);

        fillPaint = new Paint();
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);
    }

    enum ControlType {
        BOX,
        CIRCLE
    }

    public void setControlType(ControlType type) {
        this.type = type;
    }

    /**
     * This sets how big the visible control will be in the center of the view.
     * It does not set the view's size, which will be bigger to capture touch events.
     * @param sizePx size in pixels
     */
    public void setVisibleItemSize(int sizePx) {
        this.visibleItemSize = sizePx;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = visibleItemSize;
        int desiredHeight = visibleItemSize;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (type) {
            case BOX:
                drawCenteredBox(canvas);
                break;
            case CIRCLE:
                drawCenteredCircle(canvas);
                break;
        }
    }

    private void drawCenteredBox(Canvas canvas) {

        int left = (getWidth() - visibleItemSize) / 2;
        int top = (getHeight() - visibleItemSize) / 2;
        int right = left + visibleItemSize;
        int bottom = top + visibleItemSize;
        canvas.drawRect(left, top, right, bottom, fillPaint);
        borderPaint.setColor(Color.BLACK);
        canvas.drawRect(left, top, right, bottom, borderPaint);
    }

    private void drawCenteredCircle(Canvas canvas) {
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        float radius = visibleItemSize / 2;
        canvas.drawCircle(x, y, radius, fillPaint);
        borderPaint.setColor(Color.BLACK);
        canvas.drawCircle(x, y, radius, borderPaint);
    }
}
