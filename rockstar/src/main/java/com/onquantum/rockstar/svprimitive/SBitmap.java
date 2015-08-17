package com.onquantum.rockstar.svprimitive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Debug;
import android.util.Log;

/**
 * Created by Admin on 7/23/14.
 */
public class SBitmap extends SShape {
    protected float x, y, width, height;
    private Paint paint;
    private Context context;
    private Bitmap bitmap;

    public SBitmap(float x, float y, float width, float height, Context context, int resource) {
        super(SShape.BITMAP);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        bitmap = resizeBitmap(bitmap, (int)width, (int)height);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(Canvas canvas) {
        if(visibleArea != null) {
            if (x < visibleArea.left || x > visibleArea.right) {
                return;
            }
            canvas.drawBitmap(bitmap, x, y, paint);
        }
    }

    @Override
    public void move(int x, int y) {
        this.x += x;
        this.y += y;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return this.y;
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

    public void rotate(float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int)width, (int)height,matrix,false);
    }

    public void setTranslate(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
