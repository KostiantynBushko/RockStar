package com.onquantum.rockstar.common;

import android.content.Context;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.Log;

import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.midi.QSequence;
import com.onquantum.rockstar.sequencer.QSoundPool;

/**
 * Created by Admin on 8/5/14.
 */
public class GuitarString {
    public int x;
    public int y;
    public int streamId;
    public volatile float volume = 1.0f;
    private SoundPool soundPool = null;
    private int playID[];
    private Context context = null;
    private int fretsCount;
    private static int Slide = 0;

    private long startPlay = 0;
    private boolean isStoped = false;

    public GuitarString(int streamId, int x, int y, Context context,SoundPool soundPool) {
        playID = new int[25];
        this.x = x;
        this.y = y;
        this.streamId = streamId;
        this.context = context;
        this.soundPool = soundPool;
        fretsCount = new Settings(context).getFretNumbers();
        Slide = 0;
    }

    public void set(int streamId, int x, int y) {
        this.x = x;
        this.y = y;
        this.streamId = streamId;
        isStoped = false;
        startPlay = System.currentTimeMillis();
    }

    public void set(int x, int y) {
        isStoped = false;
        startPlay = System.currentTimeMillis();
        this.x = x;
        this.y = y;

        int playId1 = (y + 1) + (6 * x);
        int playId2 = 0;
        int playId3 = 0;
        playID[x] = soundPool.play(playId1,1,1,1,0,1);
        Log.i("info","Guitar String: X = " + x + " Y = " + y + " playID = " + playId1 + " NOTE = " + QSoundPool.getInstance().GetNoteForGuitarString(x, y));

        if ((x + 1) < (fretsCount + Slide)){
            playId2 = (y + 1) + (6 * (x+1));
            playID[x+1] = soundPool.play(playId2,0,0,0,0,1);
        }
        if((x - (Slide + 1)) >= 0) {
            playId3 = (y + 1) + (6 * (this.x-1));
            playID[this.x-1] = soundPool.play(playId3,0,0,0,0,1);
        }

        for(int xx = Slide; xx < (fretsCount + Slide); xx++) {
            int pId = (y + 1) + (6 * xx);
            if (pId != playId1 && pId != playId2 && pId != playId3) {
                playID[xx] = soundPool.play(pId,0,0,0,0,1);
            }
        }
    }

    public void move(int x, int y) {
        long currentTime = System.currentTimeMillis();
        long duration = (currentTime - startPlay) / 1000L;
        if (duration >= 4) {
            Log.i("info"," duration = " + duration);
            stop();
        } else {
            try{
                soundPool.setVolume(playID[this.x], 0, 0);
                soundPool.setPriority(playID[this.x], 0);
                soundPool.setVolume(playID[x], volume, volume);
                soundPool.setPriority(playID[x], 1);
                this.x = x;
            }catch (ArrayIndexOutOfBoundsException e) {}
        }
    }

    public void stop() {
        if (isStoped == true)
            return;
        isStoped = true;
        new Thread(new Runnable() {
            int _x = x;
            int _id = playID[_x];
            @Override
            public void run() {
                float volume = 0.8f;
                for (int i = Slide; i < (fretsCount + Slide); i++) {
                    if(i != this._x) {
                        soundPool.stop(playID[i]);
                    }
                }
                while (volume > 0.01f){
                    soundPool.setVolume(_id, volume, volume);
                    SystemClock.sleep(20);
                    volume-=0.01f;
                }
                soundPool.stop(_id);
            }
        }).start();
    }
    public static void setSlide(int slide) {
        Slide += slide;
    }
}
