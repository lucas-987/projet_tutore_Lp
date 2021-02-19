package com.example.projet_tut.UtilitariesClass;

import android.view.View;

public abstract class OverridableOnClickListenner<T> implements View.OnClickListener {

    private T param;

    OverridableOnClickListenner(T param) {
        this.param = param;
    }
}
