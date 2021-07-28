package net.studymongolian.chimee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.ExtractEditText;
import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import net.studymongolian.mongollibrary.ImeContainer;
import net.studymongolian.mongollibrary.MongolFont;

public class ChimeeInputMethodService extends InputMethodService
        implements ImeContainer.OnSystemImeListener,
        ImeDataSourceHelper.DataSourceHelperListener {

    CustomImeContainer imeContainer;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        imeContainer = (CustomImeContainer) inflater.inflate(R.layout.system_keyboard, null, false);
        imeContainer.showSystemKeyboardsOption("ᠰᠢᠰᠲ᠋ᠧᠮ");
        imeContainer.setDataSource(new ImeDataSourceHelper(this));
        imeContainer.setOnSystemImeListener(this);
        imeContainer.setBackgroundColor(getResources().getColor(R.color.keyboard_background));
        return imeContainer;
    }

    @Override
    public InputConnection getInputConnection() {
        return getCurrentInputConnection();
    }

    @Override
    public void onComputeInsets(InputMethodService.Insets outInsets) {
        super.onComputeInsets(outInsets);

        // This gives an invisible padding at the top so that key popups will show in API 28+
        // Touch events on this padding are passed on to whatever views are below it.
        if (imeContainer == null) return;
        outInsets.visibleTopInsets = imeContainer.getVisibleTop();
        outInsets.contentTopInsets = imeContainer.getVisibleTop();
    }

    @Override
    public void onSystemKeyboardRequest() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im == null) return;
        im.showInputMethodPicker();
    }

    @Override
    public void onHideKeyboardRequest() {
        requestHideSelf(0);
    }

    @Override
    public CustomImeContainer getImeContainer() {
        return imeContainer;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public View onCreateExtractTextView() {
        View extractedView = super.onCreateExtractTextView();
        ExtractEditText editText = extractedView.findViewById(android.R.id.inputExtractEditText);
        editText.setTypeface(MongolFont.get(MongolFont.QAGAN, getApplicationContext()));
        return extractedView;
    }
}