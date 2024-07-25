package com.example.kcubewirelesscontroller.utils;

import android.media.MediaCas;
import android.view.MotionEvent;

@FunctionalInterface
public interface onLongPressListener  {

    void onLongPressListen(MotionEvent.PointerCoords coords);
}
