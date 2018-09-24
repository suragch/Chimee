package net.studymongolian.chimee;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SelectableImageView extends ImageView {

    public SelectableImageView(Context context) {
        super(context);
    }

    public SelectableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected()) {
            drawSelectionSquare(canvas);
        }
    }

    private void drawSelectionSquare(Canvas canvas) {

    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }
}
