package com.onquantum.rockstar.svprimitive;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Admin on 7/23/14.
 */
public class SText extends SShape {

    private int centerX, centerY;
    private float size;
    private Paint paint;
    private String text = "0";

    public SText(int centerX, int centerY, float size) {
        super(SShape.TEXT);
        this.centerX = centerX;
        this.centerY = centerY;
        this.size = size;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(size);
    }
    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text,centerX, centerY-(size/2),paint);
    }

    @Override
    public void move(int x, int y) {
        centerY += y;
        centerX += x;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    @Override
    public float getX() {
        return centerX;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }
}
