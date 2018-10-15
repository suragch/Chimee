package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import net.studymongolian.mongollibrary.MongolTextView;
import net.studymongolian.mongollibrary.TextPaintPlus;

public class ScalableTextView extends MongolTextView {

    private static final int BG_PADDING_PX = 4;
    private float mScaleX = 1f;
    private float mScaleY = 1f;
    private int unscaledWidth;
    private int unScaledHeight;
    private int mBgColor = Color.TRANSPARENT;
    private float mBgCornerRadius = 0;
    Paint bgPaint;

    public ScalableTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        unscaledWidth = getMeasuredWidth();
        unScaledHeight = getMeasuredHeight();
        int width = (int) ((unscaledWidth - getPaddingLeft() - getPaddingRight()) * mScaleX)
                + getPaddingLeft() + getPaddingRight();
        int height = (int) ((unScaledHeight - getPaddingTop() - getPaddingBottom()) * mScaleY
                + getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        drawBackground(canvas);
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.scale(mScaleX, mScaleY);
        mLayout.draw(canvas);
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        bgPaint.setColor(mBgColor);
        int left = getLeft();
        int top = getTop();
        int right = getRight() - BG_PADDING_PX;
        int bottom = getBottom() - BG_PADDING_PX;
        canvas.drawRoundRect(new RectF(left, top, right, bottom),
                mBgCornerRadius, mBgCornerRadius, bgPaint);
    }

    @Override
    public void setScaleX(float scaleX) {
        mScaleX = scaleX;
        invalidate();
        requestLayout();
    }

    @Override
    public void setScaleY(float scaleY) {
        mScaleY = scaleY;
        invalidate();
        requestLayout();
    }

    @Override
    public float getScaleX() {
        return mScaleX;
    }

    @Override
    public float getScaleY() {
        return mScaleY;
    }

    public int getUnscaledWidth() {
        return unscaledWidth;
    }

    public int getUnscaledHeight() {
        return unScaledHeight;
    }

    public int getRoundBackgroundColor() {
        return mBgColor;
    }

    public void setBackgroundCornerRadius(float cornerRadius) {
        mBgCornerRadius = cornerRadius;
        invalidate();
    }

    public void setRoundBackgroundColor(int color) {
        mBgColor = color;
        invalidate();
    }
//
//    public TextPaintPlus getPaint() {
//        return (TextPaintPlus) getLayout().getPaint();
//    }
//
//    public void setPaint(TextPaintPlus paint) {
//        getLayout().set
//    }
}
