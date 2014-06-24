package com.onquantum.rockstar.glprimitive;

import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by kbushko on 7/24/13.
 */
abstract public class GLShape {

    public boolean remove = false;
    public int row = -1;
    public int column = -1;

    protected float scale = 1.0f;
    protected static final float u_color = 0.0039f;

    abstract public void draw(GL10 gl);

    abstract public void setColor(float r, float g, float b, float a);

    abstract public void setAlpha(float alpha);

    public void Remove(final int delay) {
        Log.i("info","Remove object");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(delay);
                remove = true;
                Log.i("info", " Removed");
            }
        }).start();
    }

    public int layer;
}
