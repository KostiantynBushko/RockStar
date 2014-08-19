package com.onquantum.rockstar.svprimitive;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Admin on 7/11/14.
 */
public class SLine extends SShape {

    private float startX, startY, endX, endY;

    public SLine(float startX, float startY, float endX, float endY) {
        super(SShape.LINE);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.paint = new Paint();
    }

    public SLine(float startX, float startY, float endX, float endY, Paint paint) {
        super(SShape.LINE);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.paint = paint;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawLine(startX,startY,endX,endY,paint);
    }

    @Override
    public void move(int x, int y) {
        if (!isKinematic())
            return;
        this.startX += x;
        this.startY += y;
        this.endX += x;
        this.endY += y;
    }
    @Override
    public float getX() {
        return (int)startX;
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
