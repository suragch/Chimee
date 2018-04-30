package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import net.studymongolian.mongollibrary.MongolEditText;

public class InputWindow extends HorizontalScrollView {

    private static final int MIN_HEIGHT_DP = 150;
    private static final float MIN_HEIGHT_TO_WIDTH_PROPORTION = 2;
    private static final int HEIGHT_STEP_DP = 50;

    private int mMinHeightPx;
    private int mHeightStepPx;
    private int mDesiredHeight;
    private boolean mIsManualScaling = false;
    private Rect mOldGoodSize;
    private MongolEditText editText;

    public InputWindow(Context context) {
        super(context);
        init(context, null, 0);
    }

    public InputWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public InputWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mMinHeightPx = convertDpToPx(MIN_HEIGHT_DP);
        mDesiredHeight = mMinHeightPx;
        mHeightStepPx = convertDpToPx(HEIGHT_STEP_DP);
        int width = (int) (mDesiredHeight / MIN_HEIGHT_TO_WIDTH_PROPORTION);
        mOldGoodSize = new Rect(0, 0, width, mDesiredHeight);

        editText = new MongolEditText(context, attrs, defStyleAttr);
        editText.setPadding(10, 10, 10, 10);
        this.addView(editText);
    }

    private int convertDpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
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

        Rect desiredSize;
        if (mIsManualScaling) {
            desiredSize = getDesiredManualSizeForInputWindow(heightSize);
        } else {
            desiredSize = getBestAutoSizeForInputWindow(heightSize);
        }

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

    private Rect getDesiredManualSizeForInputWindow(int maxHeight) {

        int h = Math.min(mDesiredHeight, maxHeight);
        int absoluteMinWidth = getAbsoluteMinWidth();
        int w = Math.max(measureWidthFromHeight(h), absoluteMinWidth);
        float p = MIN_HEIGHT_TO_WIDTH_PROPORTION;
        if (w > h) {
            int oldHeight = mOldGoodSize.height();
            measureWidthFromHeight(oldHeight);
            return mOldGoodSize;
        }
        if (p * w < h) {
            w = (int) (h / p);
        }
        mMinHeightPx = h;
        mOldGoodSize = new Rect(0, 0, w, h);
        return mOldGoodSize;
    }

    private Rect getBestAutoSizeForInputWindow(int maxHeight) {


        int currentHeight = editText.getMeasuredHeight();
        if (currentHeight < mMinHeightPx) {
            currentHeight = mMinHeightPx;
        }

        int h = currentHeight;
        int w = measureWidthFromHeight(h);
        float p = MIN_HEIGHT_TO_WIDTH_PROPORTION;

        int minW = getMinWidth();
        if (w < minW) {
            w = minW;
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

        } else if (h > p * w && h > mMinHeightPx) { // need to decrease h

            while (h > mMinHeightPx) {
                h -= mHeightStepPx;

                if (h <= mMinHeightPx) {
                    h = mMinHeightPx;
                    w = measureWidthFromHeight(h);
                    break;
                }
                w = measureWidthFromHeight(h);
                if (h <= p * w) {
                    break;
                }
            }
        }

        if (w < minW) {
            w = minW;
        }

        mDesiredHeight = h;
        mOldGoodSize = new Rect(0, 0, w, h);
        return mOldGoodSize;
    }

    private int getMinWidth() {
        return (int) (mMinHeightPx / MIN_HEIGHT_TO_WIDTH_PROPORTION);
    }

    private int getAbsoluteMinWidth() {
        return (int) (convertDpToPx(MIN_HEIGHT_DP) / MIN_HEIGHT_TO_WIDTH_PROPORTION);
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

    public void setDesiredHeight(int height) {
        int absoluteMin = convertDpToPx(MIN_HEIGHT_DP);
        if (height < absoluteMin) return;
        mDesiredHeight = height;
        requestLayout();
    }

    public void setIsManualScaling(boolean manualScaling) {
        mIsManualScaling = manualScaling;
    }
}
