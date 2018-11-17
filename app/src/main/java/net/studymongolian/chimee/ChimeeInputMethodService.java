package net.studymongolian.chimee;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import net.studymongolian.mongollibrary.ImeContainer;

public class ChimeeInputMethodService extends InputMethodService
        implements ImeContainer.OnSystemImeListener,
        ImeDataSourceHelper.DataSourceHelperListener {

    CustomImeContainer imeContainer;

    @Override
    public View onCreateInputView() {
        LayoutInflater inflater = getLayoutInflater();
        imeContainer = (CustomImeContainer) inflater.inflate(R.layout.system_keyboard, null, false);
        imeContainer.showSystemKeyboardsOption("ᠰᠢᠰᠲ᠋ᠧᠮ");
        imeContainer.setDataSource(new ImeDataSourceHelper(this));
        imeContainer.setOnSystemImeListener(this);
        return imeContainer;
    }

    @Override
    public InputConnection getInputConnection() {
        return getCurrentInputConnection();
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
}