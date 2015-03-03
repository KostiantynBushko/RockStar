package com.onquantum.rockstar.svprimitive;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

/**
 * Created by Admin on 7/6/14.
 */
public abstract class SShape {

    public static final int LINE = 1;
    public static final int CIRCLE = 2;
    public static final int RECT = 3;
    public static final int BITMAP = 4;
    public static final int TEXT = 5;

    public boolean remove = false;

    protected Paint paint = null;

    private int type = 0;
    private boolean isKinematic = false;

    public int getType() {
        return type;
    }

    protected SShape(int type) {
        this.type = type;
    }

    public abstract void draw(Canvas canvas);

    public void Remove(final int delay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(delay);
                remove = true;
            }
        }).start();
    }

    public void RemoveMilliseconds(final long milliseconds) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(milliseconds);
                    remove = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public abstract void move(int x, int y);
    public abstract float getX();
    public abstract float getY();
    public abstract float getWidth();
    public abstract float getHeight();
    public abstract void setAlpha(int alpha);

    public Paint getPaint() {
        return paint;
    }

    public void setColor(int color) {
        int alpha = paint.getAlpha();
        paint.setColor(color);
        paint.setAlpha(alpha);
    }


    public boolean isKinematic() {return isKinematic;}
    public void setKinematic(boolean kinematic) {
        isKinematic = kinematic;
    }

    private int layer;
    public int getLayer(){ return layer; }
    public void setLayer(int layer) { this.layer = layer; }

    public boolean isActive = true;
}
