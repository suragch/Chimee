package net.studymongolian.chimee;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;

import net.studymongolian.mongollibrary.MongolEditText;

public class ResizingScrollView extends HorizontalScrollView {

    private static final int MIN_HEIGHT_DP = 150;
    private static final int MIN_WIDTH_DP = 75;
    private static final int HEIGHT_STEP_DP = 50;


    private int mOldHeight;
    private int mOldWidth;
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
        mOldHeight = mMinHeightPx;
        mOldWidth = mMinWidthPx;

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
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            //return;
        }

        int desiredHeight = Math.max(mMinHeightPx, mOldHeight);
        int desiredWidth;

        int specHeight = MeasureSpec.makeMeasureSpec(mOldHeight, MeasureSpec.EXACTLY);
        int specWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        editText.measure(specWidth, specHeight);
        int editTextWidth = editText.getMeasuredWidth();
        int editTextHeight = editText.getMeasuredHeight();
        desiredWidth = Math.max(mMinWidthPx, editTextWidth);

        if (editTextWidth > mOldWidth) {
            if (editTextWidth > 1.5 * mOldHeight) {
                desiredHeight = (int) Math.sqrt(2*editTextHeight*editTextWidth);
                desiredHeight = Math.min(desiredHeight, heightSize);
                specHeight = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);
                editText.measure(specWidth, specHeight);
                desiredWidth = editText.getMeasuredWidth();
            } else if (editTextWidth > mOldHeight) {
                desiredHeight = mOldHeight + mHeightStepPx;
                desiredHeight = Math.min(desiredHeight, heightSize);
                specHeight = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);
                editText.measure(specWidth, specHeight);
                desiredWidth = editText.getMeasuredWidth();
            } else {
                desiredWidth = editTextWidth;
            }
        } else if (editTextWidth < mOldWidth) {
            if (3 * editTextWidth < mOldHeight) {
                desiredHeight = (int) Math.sqrt(2*editTextHeight*editTextWidth);
                desiredHeight = Math.min(desiredHeight, heightSize);
                specHeight = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);
                editText.measure(specWidth, specHeight);
                desiredWidth = editText.getMeasuredWidth();
            } else if (2 * editTextWidth < mOldHeight) {
                desiredHeight = mOldHeight - mHeightStepPx;
                desiredHeight = Math.min(desiredHeight, heightSize);
                specHeight = MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY);
                editText.measure(specWidth, specHeight);
                desiredWidth = editText.getMeasuredWidth();
            }
            desiredHeight = Math.max(desiredHeight, mMinHeightPx);
            desiredWidth = Math.max(desiredWidth, mMinWidthPx);
        }



        Log.i("TAG", "onMeasure: " + desiredWidth);


        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        if (height != desiredHeight) {
            specHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            editText.measure(specWidth, specHeight);
        }

        mOldWidth = Math.max(mMinWidthPx, editText.getMeasuredWidth());
        mOldHeight = Math.max(mMinHeightPx, editText.getMeasuredHeight());
        setMeasuredDimension(width, height);
    }
//
//    private boolean widthIsGreaterThanHeight() {
//        return mDesiredWidth > mDesiredHeight;
//    }
//
//    private boolean widthIsMuchGreaterThanHeight() {
//        return mDesiredWidth > 1.5 * mDesiredHeight;
//    }
//
//    private Rect getDesiredSize() {
//        return null;
//    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    public MongolEditText getEditText() {
        return editText;
    }
}
