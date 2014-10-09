package com.onquantum.rockstar.qmidi;

/**
 * Created by Admin on 9/18/14.
 */
public class QMidiEvent {
    private final QMidiMessage message;
    private long tick;

    public QMidiEvent(QMidiMessage message, long tick) {
        this.message = message;
        this.tick = tick;
    }

    public QMidiMessage getMessage(){
        return message;
    }

    public void setTick(long tick){
        this.tick = tick;
    }

    public long getTick(){
        return tick;
    }

}
