package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import net.studymongolian.mongollibrary.MongolEditText;

public class ResizingScrollView extends HorizontalScrollView {

    private static final int MIN_HEIGHT_DP = 150;
    private static final int MIN_WIDTH_DP = 75;
    private static final int HEIGHT_STEP_DP = 50;

    private int mMinHeightPx;
    private int mMinWidthPx;
    private int mHeightStepPx;


    private MongolEditText editText;

    public ResizingScrollView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ResizingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ResizingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mMinHeightPx = (int) convertDpToPx(MIN_HEIGHT_DP);
        mMinWidthPx = (int) convertDpToPx(MIN_WIDTH_DP);
        mHeightStepPx = (int) convertDpToPx(HEIGHT_STEP_DP);

        editText = new MongolEditText(context, attrs, defStyleAttr);
        editText.setPadding(10, 10, 10, 10);
        this.addView(editText);
    }

    private float convertDpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            return;
        }

        Rect desiredSize = getBestSizeForInputWindow(heightSize);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredSize.width(), widthSize);
        } else {
            width = desiredSize.width();
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredSize.height(), heightSize);
        } else {
            height = desiredSize.height();
        }

        setMeasuredDimension(width, height);
    }

    private Rect getBestSizeForInputWindow(int maxHeight) {

        int currentHeight = editText.getMeasuredHeight();
        if (currentHeight < mMinHeightPx) {
            currentHeight = mMinHeightPx;
        }

        int h = currentHeight;
        int w = measureWidthFromHeight(h);

        if (w < mMinWidthPx) {
            w = mMinWidthPx;
        }
        if (maxHeight < mMinHeightPx) {
            maxHeight = mMinHeightPx;
        }


        if (h < w && h < maxHeight) { // need to increase h

            while (h < maxHeight) {
                h += mHeightStepPx;
                if (h >= maxHeight) {
                    h = maxHeight;
                    w = measureWidthFromHeight(h);
                    break;
                }
                w = measureWidthFromHeight(h);
                if (h >= w) {
                    break;
                }
            }

        } else if (h > 2 * w && h > mMinHeightPx) { // need to decrease h

            while (h > mMinHeightPx) {
                h -= mHeightStepPx;

                if (h <= mMinHeightPx) {
                    h = mMinHeightPx;
                    w = measureWidthFromHeight(h);
                    break;
                }
                w = measureWidthFromHeight(h);
                if (h <= 2 * w) {
                    break;
                }
            }
        }

        if (w < mMinWidthPx) {
            w = mMinWidthPx;
        }
        return new Rect(0, 0, w, h);
    }

    private int measureWidthFromHeight(int height) {
        int specWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int specHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        editText.measure(specWidth, specHeight);
        return editText.getMeasuredWidth();
    }

    public MongolEditText getEditText() {
        return editText;
    }
}
