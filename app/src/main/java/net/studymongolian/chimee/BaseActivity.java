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
        // Look for common bottom containers that need padding
        View imeContainer = findViewById(R.id.imeContainer);
        if (imeContainer != null) {
            imeContainer.setPadding(
                imeContainer.getPaddingLeft(),
                imeContainer.getPaddingTop(),
                imeContainer.getPaddingRight(),
                bottomInset
            );
            return; // Found and fixed, no need to continue
        }
        
        // Look for common RecyclerViews that might need bottom padding
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
                break; // Found RecyclerView
            }
        }
        
        // For CodeConverter activity: fix bottom button containers
        int[] bottomButtonIds = {
            R.id.flPaste,
            R.id.flConvert, 
            R.id.flDetails,
            R.id.flCopy
        };
        
        boolean hasBottomButtons = false;
        for (int id : bottomButtonIds) {
            View button = findViewById(id);
            if (button != null) {
                hasBottomButtons = true;
                break;
            }
        }
        
        // If we found both RecyclerView and bottom buttons (CodeConverter), fix the buttons too
        if (foundRecyclerView && hasBottomButtons) {
            for (int id : bottomButtonIds) {
                View button = findViewById(id);
                if (button != null) {
                    android.view.ViewGroup.MarginLayoutParams params = 
                        (android.view.ViewGroup.MarginLayoutParams) button.getLayoutParams();
                    params.bottomMargin += bottomInset;
                    button.setLayoutParams(params);
                }
            }
            return; // Fixed both RecyclerView and buttons
        }
        
        if (foundRecyclerView) {
            return; // Already fixed RecyclerView
        }
        
        // Look for ScrollViews that might need bottom padding
        int[] commonScrollViewIds = {
            android.R.id.content // This catches most full-screen layouts
        };
        
        for (int id : commonScrollViewIds) {
            View scrollView = findViewById(id);
            if (scrollView != null && scrollView instanceof android.view.ViewGroup) {
                // For generic containers, add padding to the root content view
                android.view.ViewGroup viewGroup = (android.view.ViewGroup) scrollView;
                if (viewGroup.getChildCount() > 0) {
                    View firstChild = viewGroup.getChildAt(0);
                    if (firstChild.getLayoutParams().height == android.view.ViewGroup.LayoutParams.MATCH_PARENT) {
                        firstChild.setPadding(
                            firstChild.getPaddingLeft(),
                            firstChild.getPaddingTop(),
                            firstChild.getPaddingRight(),
                            bottomInset
                        );
                        return;
                    }
                }
            }
        }
        
        // Fallback: Look for HorizontalScrollView (like in settings)
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
    }
}