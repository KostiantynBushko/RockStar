package com.onquantum.rockstar.svprimitive;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;

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
    private boolean isKinematic = true;

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

    public abstract void move(int x, int y);
    public abstract float getX();
    public abstract float getY();
    public abstract float getWidth();
    public abstract float getHeight();
    public abstract void setAlpha(int alpha);

    public boolean isKinematic() {return isKinematic;}
    public void setKinematic(boolean kinematic) {
        isKinematic = kinematic;
    }

    private int layer;
    public int getLayer(){ return layer; }
    public void setLayer(int layer) { this.layer = layer; }

    public boolean isActive = true;
}
