package com.serjardovic.testapp2;

import android.content.Context;

interface Callback {

    void manageSituation();
    Adapter getAdapter();
    Context getContext();

}
