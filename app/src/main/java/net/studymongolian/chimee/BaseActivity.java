package net.studymongolian.chimee;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.graphics.Insets;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable edge-to-edge for Android 15+ (API 35+)
        if (Build.VERSION.SDK_INT >= 35) {
            Window window = getWindow();
            
            // Make status bar transparent (Android 15 requirement)
            window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
            // Set navigation bar to black for better contrast
            window.setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black));
            
            // Set status bar content to light (white text/icons)
            WindowInsetsControllerCompat windowInsetsController = 
                WindowCompat.getInsetsController(window, window.getDecorView());
            if (windowInsetsController != null) {
                windowInsetsController.setAppearanceLightStatusBars(false);
            }
            
            // Enable edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupWindowInsets();
    }

    private void setupWindowInsets() {
        // Only apply insets handling for Android 15+ (API 35+) where edge-to-edge is enforced
        if (Build.VERSION.SDK_INT >= 35) {
            View rootView = findViewById(android.R.id.content);
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, windowInsets) -> {
                Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                
                // Create fake status bar background
                createFakeStatusBar(systemBars.top);
                
                // Find and fix toolbar if present
                Toolbar toolbar = findToolbar();
                if (toolbar != null) {
                    android.view.ViewGroup.MarginLayoutParams toolbarParams = 
                        (android.view.ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                    toolbarParams.topMargin = systemBars.top;
                    toolbar.setLayoutParams(toolbarParams);
                }
                
                // Find and fix bottom containers if present
                fixBottomContainer(systemBars.bottom);
                
                return WindowInsetsCompat.CONSUMED;
            });
        }
    }

    private void createFakeStatusBar(int statusBarHeight) {
        if (statusBarHeight <= 0) return;
        
        // Find or create the fake status bar
        View fakeStatusBar = findViewById(android.R.id.statusBarBackground);
        if (fakeStatusBar == null) {
            // Create a fake status bar view
            fakeStatusBar = new View(this);
            fakeStatusBar.setId(android.R.id.statusBarBackground);
            fakeStatusBar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
            
            // Add it to the root view
            android.view.ViewGroup rootView = findViewById(android.R.id.content);
            if (rootView instanceof android.view.ViewGroup) {
                android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    statusBarHeight
                );
                params.gravity = android.view.Gravity.TOP;
                
                ((android.view.ViewGroup) rootView).addView(fakeStatusBar, 0, params);
            }
        } else {
            // Update existing fake status bar
            android.view.ViewGroup.LayoutParams params = fakeStatusBar.getLayoutParams();
            params.height = statusBarHeight;
            fakeStatusBar.setLayoutParams(params);
            fakeStatusBar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
        }
    }

    /**
     * Override this method to return the toolbar for this activity
     */
    protected Toolbar findToolbar() {
        // Try common toolbar IDs
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        if (toolbar == null) {
            toolbar = findViewById(R.id.toolbar);
        }
        return toolbar;
    }

    /**
     * Override this method to apply bottom insets to specific views
     */
    protected void fixBottomContainer(int bottomInset) {
        // Priority 1: Look for keyboard containers first (MainActivity, SaveActivity)
        int[] keyboardContainerIds = {
            R.id.imeContainer,
            R.id.keyboard_container
        };
        
        for (int id : keyboardContainerIds) {
            View container = findViewById(id);
            if (container != null) {
                container.setPadding(
                    container.getPaddingLeft(),
                    container.getPaddingTop(),
                    container.getPaddingRight(),
                    bottomInset
                );
                return; // Found keyboard container, done
            }
        }
        
        // Priority 2: Look for RecyclerViews that extend to bottom
        int[] commonRecyclerViewIds = {
            R.id.rv_content,
            R.id.rv_all_favorites,
            R.id.rv_document_list,
            R.id.rv_message_history,
            R.id.rv_reader
        };
        
        boolean foundRecyclerView = false;
        for (int id : commonRecyclerViewIds) {
            View recyclerView = findViewById(id);
            if (recyclerView != null) {
                recyclerView.setPadding(
                    recyclerView.getPaddingLeft(),
                    recyclerView.getPaddingTop(),
                    recyclerView.getPaddingRight(),
                    bottomInset
                );
                foundRecyclerView = true;
                break;
            }
        }
        
        // Priority 3: For activities with bottom buttons (CodeConverter, About), fix buttons too
        int[] bottomButtonIds = {
            R.id.flPaste, R.id.flConvert, R.id.flDetails, R.id.flCopy, // CodeConverter
            R.id.flContact, R.id.flUpdates, R.id.flShare // About
        };
        
        boolean hasBottomButtons = false;
        for (int id : bottomButtonIds) {
            View button = findViewById(id);
            if (button != null) {
                android.view.ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                if (layoutParams instanceof android.view.ViewGroup.MarginLayoutParams) {
                    android.view.ViewGroup.MarginLayoutParams params = 
                        (android.view.ViewGroup.MarginLayoutParams) layoutParams;
                    params.bottomMargin += bottomInset;
                    button.setLayoutParams(params);
                }
                hasBottomButtons = true;
            }
        }
        
        if (foundRecyclerView || hasBottomButtons) {
            return; // Fixed content
        }
        
        // Priority 4: Fallback for Settings (HorizontalScrollView)
        View rootView = findViewById(android.R.id.content);
        if (rootView instanceof android.view.ViewGroup) {
            android.view.ViewGroup rootGroup = (android.view.ViewGroup) rootView;
            for (int i = 0; i < rootGroup.getChildCount(); i++) {
                View child = rootGroup.getChildAt(i);
                if (child instanceof android.widget.HorizontalScrollView) {
                    child.setPadding(
                        child.getPaddingLeft(),
                        child.getPaddingTop(),
                        child.getPaddingRight(),
                        bottomInset
                    );
                    return;
                }
            }
        }
        
        // Priority 5: Ultimate fallback - add padding to the main content view
        if (rootView instanceof android.view.ViewGroup) {
            android.view.ViewGroup rootGroup = (android.view.ViewGroup) rootView;
            if (rootGroup.getChildCount() > 0) {
                View mainContent = rootGroup.getChildAt(0);
                if (mainContent.getLayoutParams().height == android.view.ViewGroup.LayoutParams.MATCH_PARENT) {
                    mainContent.setPadding(
                        mainContent.getPaddingLeft(),
                        mainContent.getPaddingTop(),
                        mainContent.getPaddingRight(),
                        bottomInset
                    );
                }
            }
        }
    }
}