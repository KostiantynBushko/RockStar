package com.onquantum.rockstar.svprimitive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.SystemClock;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Admin on 8/18/14.
 */
public class SGuitarString extends SBitmap {
    public volatile int animate = 0;
    private float stringDownUp;
    private int delay = 30;
    private PointF pos;

    public SGuitarString(float x, float y, float width, float height, Context context, int resource) {
        super(x, y, width, height, context, resource);
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        stringDownUp = size.y * 0.01f;
        pos = new PointF(x,y);
    }

    public void setAnimate() {
        if (animate == 0) {
            animate = 5;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (animate > 0) {
                        setTranslate(x, y - stringDownUp);
                        SystemClock.sleep(delay);
                        setTranslate(x,y + stringDownUp);
                        SystemClock.sleep(delay);
                        setTranslate(x,y + stringDownUp);
                        SystemClock.sleep(delay);
                        setTranslate(x,y - stringDownUp);
                        SystemClock.sleep(delay);
                        animate--;
                    }
                }
            }).start();
        }
    }

    public PointF getPosition() {
        return pos;
    }
}
