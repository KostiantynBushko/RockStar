package com.onquantum.rockstar.common;

import android.graphics.Point;

import com.onquantum.rockstar.svprimitive.SCircle;

/**
 * Created by Admin on 6/24/14.
 */
public class Pentatonic {
    public int line = 0;
    public int bar = 0;
    public long delay = 0L;
    public long playTime = 0L;
    public Point position = new Point();
    public SCircle mask;

    public Pentatonic(int line, int bar, long delay, long playTime, SCircle mask) {
        this.line = line;
        this.bar = bar;
        this.delay = delay;
        this.playTime = playTime;
        int x = bar;
        int y = line;
        position = new Point(x,y);
        this.mask = mask;
    }
    public Pentatonic() {
        this.line = 0;
        this.bar = 0;
        this.delay = 0;
        this.playTime = 0;
        mask = null;
    }
}
