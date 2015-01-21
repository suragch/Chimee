package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MongolFontTextView extends TextView {

	// This class does not rotate the textview. It only displays the Mongol font.
	// For use with MongolLayout, which does all the rotation and mirroring.

	// Constructors
	public MongolFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MongolFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MongolFontTextView(Context context) {
		super(context);
		init();
	}

	// This class requires the mirrored Mongolian font to be in the assets/fonts folder
	private void init() {
		// Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
		// "fonts/ChimeeWhiteMirrored.ttf");
		// setTypeface(tf);

		// Use the above commented code is using a single font in another application
		Typeface tf = FontCache.get(SettingsActivity.FONT_DEFAULT, getContext());
		if (tf != null) {
			setTypeface(tf);
		}
	}

}