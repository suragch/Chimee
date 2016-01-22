package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MongolTextView extends TextView {

	private TextPaint textPaint;
	private Paint cursorPaint = new Paint();
	private boolean mCursorIsVisible;
	private CursorTouchLocationListener listener;

	// Naming is based on pre-rotated/mirrored values
	private float mCursorBaseY;
	private float mCursorBottomY;
	private float mCursorAscentY; // This is a negative number
	private float mCursorX;
	
	private static final float CURSOR_THICKNESS = 2f; 

	// Constructors
	public MongolTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MongolTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MongolTextView(Context context) {
		super(context);
		init();
	}

	// This class requires the mirrored Mongolian font to be in the assets/fonts folder
	private void init() {
		//Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
		//		"fonts/ChimeeWhiteMirrored.ttf");
		//setTypeface(tf);
		
		// Use the above commented code is using a single font in another application 
		Typeface tf = FontCache.get(SettingsActivity.FONT_DEFAULT, getContext());
        if(tf != null) {
        	setTypeface(tf);
        }

		this.mCursorIsVisible = true;

		cursorPaint.setStrokeWidth(CURSOR_THICKNESS);
		cursorPaint.setColor(Color.BLACK); // TODO should be same as text color
		
	}

	// This interface may be deleted if touch functionality is not needed
	public interface CursorTouchLocationListener {

		/**
		 * Returns the touch location to be used for the cursor so you can update the insert
		 * location in a text string.
		 * 
		 * @param glyphIndex
		 *            You will need to translate glyphIndex into a Unicode index if you are using a
		 *            Unicode string.
		 */
		public void onCursorTouchLocationChanged(int glyphIndex);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// swap the height and width
		super.onMeasure(heightMeasureSpec, widthMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		textPaint = getPaint();
		textPaint.setColor(getCurrentTextColor());
		textPaint.drawableState = getDrawableState();

		canvas.save();

		// flip and rotate the canvas
		canvas.translate(getWidth(), 0);
		canvas.rotate(90);
		canvas.translate(0, getWidth());
		canvas.scale(1, -1);
		canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());

		// draw the cursor
		if (mCursorIsVisible) {
			canvas.drawLine(mCursorX, mCursorBottomY, mCursorX, mCursorBaseY + mCursorAscentY,
					cursorPaint);
		}

		getLayout().draw(canvas);

		canvas.restore();
	}

	/*public int whatWouldBeTheWidth(int height){

		this.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		//this.getMeasuredHeight();

		return this.getMeasuredHeight()
	}*/

	public void showCursor(boolean visible) {
		mCursorIsVisible = visible;
		this.invalidate();
		// TODO make the cursor blink
	}
	
	public void setCursorColor(int color) {
		cursorPaint.setColor(color);
	}

	public void setCursorLocation(int characterOffset) {
		
		Layout layout = this.getLayout();
		
		if (layout!=null){
			
			try {
				// This method is giving a lot of crashes so just surrounding with 
				// try catch for now
				
				int line = layout.getLineForOffset(characterOffset);
				mCursorX = layout.getPrimaryHorizontal(characterOffset);
				mCursorBaseY = layout.getLineBaseline(line);
				mCursorBottomY = layout.getLineBottom(line);
				mCursorAscentY = layout.getLineAscent(line);
				
				this.invalidate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class InputWindowTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent event) {

			Layout layout = ((TextView) view).getLayout();

			// swapping x and y for touch events
			int y = (int) event.getX();
			int x = (int) event.getY();

			if (layout != null) {

				int line = layout.getLineForVertical(y);
				int offset = layout.getOffsetForHorizontal(line, x);

				mCursorX = layout.getPrimaryHorizontal(offset);
				mCursorBaseY = layout.getLineBaseline(line);
				mCursorBottomY = layout.getLineBottom(line);
				mCursorAscentY = layout.getLineAscent(line);
				//mCursorHeightY = layout.getLineTop(line);

				view.invalidate();
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//handler.postDelayed(mLongPressed, 1000);
					listener.onCursorTouchLocationChanged(offset);
					break;
				case MotionEvent.ACTION_UP:
					//handler.removeCallbacks(mLongPressed);
					// notify the host activity of the new cursor location
					
					break;
				}

			}

			return false;
		}
		
	}
	
	public void setCursorTouchLocationListener(CursorTouchLocationListener listener) {
        this.listener = listener;
    }
}