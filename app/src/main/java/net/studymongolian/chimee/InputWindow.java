package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import net.studymongolian.mongollibrary.MongolEditText;

import java.util.concurrent.atomic.AtomicInteger;

public class InputWindow extends HorizontalScrollView {

    private static final int MIN_HEIGHT_DP = 150;
    private static final float MIN_HEIGHT_TO_WIDTH_PROPORTION = 2;
    private static final int HEIGHT_STEP_DP = 50;
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);


    private int mMinHeightPx;
    private int mHeightStepPx;
    private int mDesiredHeight;
    private boolean mIsManualScaling = false;
    private Rect mOldGoodSize;
    private MongolEditText editText;
    private int mBackgroundColor;
    private String mLastSavedContent;


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
        mBackgroundColor = DEFAULT_BACKGROUND_COLOR;

        editText = new MongolEditText(context, attrs, defStyleAttr);
        editText.setId(getUniqueId());
        editText.setPadding(10, 10, 10, 10);
        this.addView(editText);
    }

    private int getUniqueId() {
        if (Build.VERSION.SDK_INT < 17) {
            return generateViewIdCompat();
        } else {
            return View.generateViewId();
        }
    }

    /**
     * taken from View.generateViewId()
     */
    public static int generateViewIdCompat() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    private int convertDpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // assuming that mode is always AT_MOST (ie, xml set to WRAP_CONTENT)
        // if generalizing this class for other uses then you will need to
        // handle Unspecified and Exactly

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        Rect desiredSize;
        if (mIsManualScaling) {
            desiredSize = getDesiredManualSizeForInputWindow(heightSize);
        } else {
            desiredSize = getBestAutoSizeForInputWindow(heightSize);
        }

        //Measure Height
        int height = Math.min(desiredSize.height(), heightSize);
        if (desiredSize.height() != height && !mIsManualScaling) {
            desiredSize = getUpdatedDesiredSizeBasedOnHeight(height);
        }

        //Measure Width
        int width = Math.min(desiredSize.width(), widthSize);

        setMeasuredDimension(width, height);
    }

    private Rect getUpdatedDesiredSizeBasedOnHeight(int height) {
        int w = measureWidthFromHeight(height);
        mOldGoodSize = new Rect(0, 0, w, height);
        return mOldGoodSize;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        giveTouchEventsToEditTextWhenSmall(event);
        return super.onTouchEvent(event);
    }

    private void giveTouchEventsToEditTextWhenSmall(MotionEvent event) {
        Rect editTextRect = new Rect();
        editText.getHitRect(editTextRect);
        if (!editTextRect.contains((int) event.getX(), (int) event.getY())) {
            editText.onTouchEvent(event);
        }
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

    public float getTextSize() {
        return editText.getTextSize();
    }

    public void setTextSize(float size) {
        editText.setTextSize(size);
    }

    public void setTextColor(int color) {
        editText.setTextColor(color);
    }

    public void setTypeface(Typeface typeface) {
        editText.setTypeface(typeface);
    }

    public CharSequence getText() {
        return editText.getText();
    }

    public void setCursorVisible(boolean visible) {
        editText.setCursorVisible(visible);
    }

    public Bitmap getBitmap() {
        int inputWidth = getWidth();
        int editTextWidth = editText.getWidth();
        int width = Math.max(inputWidth, editTextWidth);
        int height = editText.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        colorBackground(canvas);
        editText.draw(canvas);
        return bitmap;
    }

    private void colorBackground(Canvas canvas) {
        int color = DEFAULT_BACKGROUND_COLOR;
        Drawable background = getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
    }

    public void recordSavedContent() {
        mLastSavedContent = editText.getText().toString();
    }

    public boolean hasUnsavedContent() {
        String currentContent = editText.getText().toString();
        return !TextUtils.isEmpty(currentContent) && !currentContent.equals(mLastSavedContent);
    }
}
