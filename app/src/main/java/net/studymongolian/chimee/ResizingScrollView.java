package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;

import net.studymongolian.mongollibrary.MongolEditText;

public class ResizingScrollView extends HorizontalScrollView {

    private static final int MIN_HEIGHT_DP = 150;
    private static final int MIN_WIDTH_DP = 75;
    private static final int HEIGHT_STEP_DP = 50;


    private float mOldHeight = 0;
    private float mOldWidth = 0;
    private int mDesiredHeight;
    private int mDesiredWidth;
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
        mDesiredHeight = (int) convertDpToPx(MIN_HEIGHT_DP);
        mDesiredWidth = (int) convertDpToPx(MIN_WIDTH_DP);
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
        //int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            //return;
        }

        int specHeight = MeasureSpec.makeMeasureSpec(mDesiredHeight, MeasureSpec.EXACTLY);
        int specWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        editText.measure(specWidth, specHeight);

        int editTextWidth = editText.getMeasuredWidth();
        int editTextHeight = editText.getMeasuredHeight();
        if (editTextWidth > mDesiredWidth) {
            mDesiredWidth = editTextWidth;
            if (mDesiredWidth > mDesiredHeight) {
                int newHeight = (int) Math.sqrt(2*editTextHeight*editTextWidth);
                if (newHeight - mDesiredHeight > mHeightStepPx) {
                    mDesiredHeight += mHeightStepPx;
                }
                if (mDesiredHeight > heightSize) {
                    mDesiredHeight = Math.min(mDesiredHeight, heightSize);
                }
                specHeight = MeasureSpec.makeMeasureSpec(mDesiredHeight, MeasureSpec.EXACTLY);
                editText.measure(specWidth, specHeight);
                mDesiredWidth = editText.getMeasuredWidth();
            }
        }
        Log.i("TAG", "onMeasure: " + mDesiredWidth);


        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(mDesiredWidth, widthSize);
        } else {
            width = mDesiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(mDesiredHeight, heightSize);
        } else {
            height = mDesiredHeight;
        }

        if (height != mDesiredHeight) {
            specHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            editText.measure(specWidth, specHeight);
        }

        setMeasuredDimension(width, height);
    }

    private Rect getDesiredSize() {
        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mOldHeight = oldh;
        mOldWidth = oldw;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    public MongolEditText getEditText() {
        return editText;
    }
}
