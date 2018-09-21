package net.studymongolian.chimee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.studymongolian.mongollibrary.MongolTextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PhotoOverlayActivity extends AppCompatActivity {

    public static final String CURRENT_MESSAGE_KEY = "message";
    public static final String PHOTO_URI_KEY = "uri";


    CharSequence currentMessage;
    private Bitmap bitmap;
    private TouchImageView mImageView;
    private OverlayTextView textOverlayView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_overlay);

        setupToolbar();
        setupImageView();

        getIntentData();
        createTextOverlay();
        layoutTextOverlay();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }
    }

    private void setupImageView() {
        mImageView = findViewById(R.id.imageView);
        mImageView.setOnTouchListener(mImageViewTouchListener);
    }


    private View.OnTouchListener mImageViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                textOverlayView.setFocused(false);
            }
            return false;
        }
    };

    private void getIntentData() {
        currentMessage = getIntent().getCharSequenceExtra(CURRENT_MESSAGE_KEY);

        try {
            if (bitmap != null) {
                bitmap.recycle();
            }
            Uri uri = getIntent().getData();
            if (uri == null) return;
            InputStream stream = getContentResolver().openInputStream(uri);
            if (stream == null) return;
            bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            int newHeight = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, newHeight, true);
            mImageView.setImageBitmap(scaled);
            mImageView.setZoom(1f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTextOverlay() {
        textOverlayView = new OverlayTextView(this);
        textOverlayView.setText(currentMessage);

//        RelativeLayout.LayoutParams layoutParams =
//                new RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT);
//        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        textOverlayView.setLayoutParams(layoutParams);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.CENTER;
        //textOverlayView.setLayoutParams(layoutParams);
        FrameLayout layout = findViewById(R.id.photo_frame_layout);
        layout.addView(textOverlayView);
    }

    private void layoutTextOverlay() {
        //final View content = findViewById(android.R.id.content);
        textOverlayView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textOverlayView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                FrameLayout layout = findViewById(R.id.photo_frame_layout);
                int x = (layout.getWidth() - textOverlayView.getWidth())/2 ;
                int y = (layout.getHeight() - textOverlayView.getHeight())/2 ;
                textOverlayView.setX(x);
                textOverlayView.setY(y);
            }
        });
    }


    private View.OnTouchListener textOverlayTouchListener = new View.OnTouchListener() {

        float dX;
        float dY;
        int lastAction;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    lastAction = MotionEvent.ACTION_DOWN;
                    break;

                case MotionEvent.ACTION_MOVE:
                    view.setY(event.getRawY() + dY);
                    view.setX(event.getRawX() + dX);
                    lastAction = MotionEvent.ACTION_MOVE;
                    break;

                case MotionEvent.ACTION_UP:
                    if (lastAction == MotionEvent.ACTION_DOWN)
                        Toast.makeText(PhotoOverlayActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    return false;
            }
            return true;
        }
    };

    private void chooseImage() {
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            default:
//                super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    private void onImageResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (data == null || data.getData() == null) return;

    }

}
