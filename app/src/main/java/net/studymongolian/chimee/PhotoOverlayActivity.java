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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_overlay_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePhoto();
                return true;
            case R.id.action_share:
                sharePhoto();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void savePhoto() {

    }

    private void sharePhoto() {

    }

    public void onColorToolbarItemClick(View view) {

    }

    public void onFontToolbarItemClick(View view) {

    }

    public void onBorderToolbarItemClick(View view) {

    }

    public void onShadowToolbarItemClick(View view) {

    }

    public void onBackgroundToolbarItemClick(View view) {

    }
}
