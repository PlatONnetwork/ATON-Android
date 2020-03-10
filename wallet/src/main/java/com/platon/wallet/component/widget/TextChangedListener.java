package com.platon.wallet.component.widget;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @author matrixelement
 */
public abstract class TextChangedListener implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    protected abstract void onTextChanged(CharSequence s);
}
