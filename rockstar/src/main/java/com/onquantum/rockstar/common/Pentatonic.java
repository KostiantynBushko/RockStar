package com.onquantum.rockstar.common;

/**
 * Created by Admin on 6/24/14.
 */
public class Pentatonic {
    public int line = 0;
    public int bar = 0;
    public int delay = 0;
    public int playTime = 0;

    public Pentatonic(int line, int bar, int delay, int playTime) {
        this.line = line;
        this.bar = bar;
        this.delay = delay;
        this.playTime = playTime;
    }
    public Pentatonic() {
        this.line = 0;
        this.bar = 0;
        this.delay = 0;
        this.playTime = 0;
    }
}
