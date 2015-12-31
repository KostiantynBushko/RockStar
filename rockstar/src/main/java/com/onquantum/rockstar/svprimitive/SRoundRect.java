package com.onquantum.rockstar.svprimitive;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Admin on 12/25/15.
 */
public class SRoundRect extends SShape {

    public int x, y, width, height;
    public SRoundRect(int x, int y, int width, int height, Paint paint) {
        super(SShape.RECT);
        this.paint = paint;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(new RectF(x, y, x + width, y + height), height, height, paint);
    }

    @Override
    public void move(int x, int y) {
        if (!isKinematic())
            return;
        this.x += x;
        this.y += y;
    }

    @Override
    public float getX() {
        return (float) x;
    }

    @Override
    public float getY() {
        return (float)y;
    }

    @Override
    public float getWidth() {
        return (float) width;
    }

    @Override
    public float getHeight() {
        return (float)height;
    }

    @Override
    public void setAlpha(int alpha) {

    }
}
