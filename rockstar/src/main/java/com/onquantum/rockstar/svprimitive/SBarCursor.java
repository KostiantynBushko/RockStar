package com.onquantum.rockstar.svprimitive;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Admin on 7/16/14.
 */
public class SBarCursor extends SShape{

    private int number = 1;
    private int left, top, right, bottom;
    private int textSize = 0;
    private int width, height;
    private Paint textPaint;
    private int textX, textY;

    public SBarCursor(int left, int top, int right, int bottom) {
        super(SShape.RECT);
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setAlpha(120);

        textSize = (int)Math.abs((top - bottom) / 3);
        width = this.right - this.left;
        height = this.bottom - this.top;

        textX = this.left + width / 2;
        textY = (int)(this.top - height * 0.1f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(left,top,right,bottom,paint);
        //canvas.drawText(Integer.toString(number),textX, textY,textPaint);
    }

    @Override
    public void move(int x, int y) {
        left = x;
        right = x + width;
        textX = this.left + width / 2;
    }

    @Override
    public float getX() {
        return 0;
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

    public void setNumber(int number) {
        this.number = number;
    }
    public int getNumber() {
        return number;
    }


}
