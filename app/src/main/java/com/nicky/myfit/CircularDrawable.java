package com.nicky.myfit;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

/**
 * Created by nicholas on 12/19/14.
 */
public class CircularDrawable extends Drawable {

    @Override
    public void setAlpha(int alpha) {
        this.setAlpha(alpha);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
