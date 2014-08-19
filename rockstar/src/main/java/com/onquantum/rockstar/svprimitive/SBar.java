package com.onquantum.rockstar.svprimitive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by Admin on 7/16/14.
 */
public class SBar extends SShape {

    private int x, y, width, height;
    private Paint paint;
    private Context context;
    private Bitmap bitmap;

    private int textSize = 0;
    private Paint textPaint;
    private int number = 1;
    private int textX, textY;

    public SBar(int x, int y, int width, int height, Context context, int resource) {
        super(SShape.BITMAP);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        bitmap = resizeBitmap(bitmap, width, height);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textSize = Math.abs(height / 3);

        textX = this.x + width / 2;
        textY = (int)(this.y - height * 0.1f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap,x,y,paint);
        canvas.drawText(Integer.toString(number),textX, textY,textPaint);
    }

    @Override
    public void move(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void setColor(int color) {
        textPaint.setColor(color);
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public int getNumber() {
        return number;
    }

    @Override
    public float getX() {
        return x;
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

    public Bitmap resizeBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
}
