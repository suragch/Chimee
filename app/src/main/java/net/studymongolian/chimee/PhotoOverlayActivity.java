package net.studymongolian.chimee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.studymongolian.mongollibrary.MongolTextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PhotoOverlayActivity extends AppCompatActivity {

    public static final String CURRENT_MESSAGE_KEY = "message";

    private static final int IMAGE_REQUEST_CODE = 0;

    String currentMessage;
    private Bitmap bitmap;
    private TouchImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_overlay);

        setupToolbar();
        setupImageView();

        currentMessage = getIntent().getStringExtra(CURRENT_MESSAGE_KEY);
        chooseImage();
        createTextOverlay();
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

    }

    private void createTextOverlay() {
        MongolTextView textOverlayView = new MongolTextView(this);
        textOverlayView.setText(currentMessage);

        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textOverlayView.setLayoutParams(layoutParams);
        RelativeLayout rootLayout = findViewById(R.id.photo_overlay_root_layout);
        textOverlayView.setOnTouchListener(textOverlayTouchListener);
        rootLayout.addView(textOverlayView);
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                onImageResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onImageResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (data == null || data.getData() == null) return;
        try {
            if (bitmap != null) {
                bitmap.recycle();
            }
            InputStream stream = getContentResolver().openInputStream(data.getData());
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

}
