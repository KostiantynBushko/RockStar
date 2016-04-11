package com.onquantum.rockstar.svprimitive;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Admin on 7/6/14.
 */
public class SCircle extends SShape {

    protected float centerX = 0, centerY = 0,radius = 0;
    private Paint textPaint = null;
    private String text = null;
    private int number = 0;
    private int textSize = 0;

    public SCircle() {
        super(SShape.CIRCLE);
        this.paint = new Paint();
    }

    public SCircle(float centerX, float centerY, float radius) {
        super(SShape.CIRCLE);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.paint = new Paint();
        textSize = (int)radius;
    }

    public SCircle(float centerX, float centerY, float radius,Paint paint) {
        super(SShape.CIRCLE);
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.paint = paint;
        textSize = (int)radius;
    }

    @Override
    public void draw(Canvas canvas) {
        if(visibleArea != null) {
            if((centerX - radius) < visibleArea.left || (centerX + radius) > visibleArea.right)
                return;
            canvas.drawCircle(centerX,centerY, radius, paint);
            if (text == null)
                return;
            canvas.drawText(text,centerX,centerY+radius/4,textPaint);
        }
    }

    @Override
    public void move(int x, int y) {
        if (!isKinematic())
            return;
        this.centerX += x;
        this.centerY += y;
    }

    @Override
    public float getX() {
        return (int)centerX;
    }

    @Override
    public float getY() {
        return radius * 2;
    }

    @Override
    public float getWidth() {
        return radius * 2;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    public void SetPosition(float x, float y) {
        this.centerX = x;
        this.centerY = y;
    }

    public void drawNumber(int number, Paint paint) {
        this.textPaint = paint;
        this.textPaint.setTextSize(radius);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        text = Integer.toString(number);
        this.number = number;
    }

    public void drawText(String text, Paint paint) {
        this.textPaint = paint;
        this.textPaint.setTextSize(radius);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.text = text;
    }

    public int getNumber() {
        return number;
    }
}
