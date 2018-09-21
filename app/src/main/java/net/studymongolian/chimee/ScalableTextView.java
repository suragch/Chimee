package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;

import net.studymongolian.mongollibrary.MongolTextView;

public class ScalableTextView extends MongolTextView {

    private float mScaleX = 1f;
    private float mScaleY = 1f;
    private int unscaledWidth;
    private int unScaledHeight;

    public ScalableTextView(Context context) {
        super(context);
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
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.scale(mScaleX, mScaleY);
        mLayout.draw(canvas);
        canvas.restore();
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
}
