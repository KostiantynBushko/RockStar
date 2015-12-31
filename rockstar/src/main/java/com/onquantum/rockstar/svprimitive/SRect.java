package com.onquantum.rockstar.svprimitive;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Admin on 12/27/15.
 */
public class SRect extends SShape {
    private float x, y, width, height;

    public SRect(int x, int y, int width, int height, Paint paint) {
        super(SShape.RECT);
        this.paint = paint;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(x,y, x + width, y + height, paint);
    }

    @Override
    public void move(int x, int y) {
        if (!isKinematic())
            return;
        this.x += x;
        this.y += y;
        Log.i("info"," SRect move " + x);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setWidth(float width) {
        this.width = width;
    }
}
