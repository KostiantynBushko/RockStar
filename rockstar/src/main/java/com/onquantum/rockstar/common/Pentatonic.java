package com.onquantum.rockstar.common;

import android.graphics.Point;

/**
 * Created by Admin on 6/24/14.
 */
public class Pentatonic {
    public int line = 0;
    public int bar = 0;
    public long delay = 0L;
    public long playTime = 0L;
    public Point position = new Point();

    public Pentatonic(int line, int bar, long delay, long playTime) {
        this.line = line;
        this.bar = bar;
        this.delay = delay;
        this.playTime = playTime;
        int x = bar;
        int y = line;
        position = new Point(x,y);
    }
    public Pentatonic() {
        this.line = 0;
        this.bar = 0;
        this.delay = 0;
        this.playTime = 0;
    }
}
