package com.onquantum.rockstar.glprimitive;

import android.os.SystemClock;

/**
 * Created by Admin on 7/8/14.
 */
public class GLDGuitarString extends GLDTexture {

    public volatile int animate = 0;
    private float stringDownUp = 0.2f;
    private float scale = 0.005f;
    private int delay = 30;

    public GLDGuitarString(float bottomLeftX, float bottomLeftY, final float width, final float height, boolean object) {
        super(bottomLeftX, bottomLeftY, width, height, object);
    }

    public GLDGuitarString(float bottomLeftX, float bottomLeftY, final float width, final float height) {
        super(bottomLeftX, bottomLeftY, width, height);
    }

    public void setAnimate() {
        if (animate == 0) {
            animate = 5;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (animate > 0) {
                        setTranslate(x, y - stringDownUp, width, height - scale);
                        SystemClock.sleep(delay);
                        setTranslate(x,y + stringDownUp,width,height + scale);
                        SystemClock.sleep(delay);
                        setTranslate(x,y + stringDownUp,width,height + scale);
                        SystemClock.sleep(delay);
                        setTranslate(x,y - stringDownUp,width,height - scale);
                        SystemClock.sleep(delay);
                        animate--;
                    }
                }
            }).start();
        }
    }
}
