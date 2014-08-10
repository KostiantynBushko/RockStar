package com.onquantum.rockstar.common;

import android.content.Context;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.Log;

import com.onquantum.rockstar.Settings;

/**
 * Created by Admin on 8/5/14.
 */
public class GuitarString {
    public int x;
    public int y;
    public int streamId;
    public volatile float volume = 1.0f;
    SoundPool soundPool = null;
    public float currRate = 1.0f;
    private int soundID[] = new int[24];
    private Context context = null;

    public GuitarString(int streamId, int x, int y, Context context) {
        this.x = x;
        this.y = y;
        this.streamId = streamId;
        this.context = context;
    }

    public void set(int streamId, int x, int y, SoundPool soundPool) {
        this.x = x;
        this.y = y;
        this.streamId = streamId;
        this.soundPool = soundPool;

    }

    public void set(int x, int y, SoundPool soundPool) {
        Log.i("info", " X = " + Integer.toString(x));
        this.x = x;
        this.y = y;
        this.soundPool = soundPool;
        for(int xx = 0; xx< new Settings(context).getFretNumbers(); xx++) {
            int playId = (y + 1) + (6 * xx);
            Log.i("info", " ::: playId = " + Integer.toString(playId));
            soundID[xx] = soundPool.play(playId,0,0,1,0,1.0f);
        }
        soundPool.setVolume(soundID[x],volume,volume);
        soundPool.setPriority(soundID[x],1);
    }

    public void move(int x, int y) {
        try{
            soundPool.setVolume(soundID[this.x],0,0);
            soundPool.setVolume(soundID[x],volume,volume);
            this.x = x;
        }catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        for (int i = 0; i<24; i++) {
            if(i != this.x)
                soundPool.stop(soundID[i]);
        }
        new Thread(new Runnable() {
            int _x = x;
            int _id = soundID[_x];
            @Override
            public void run() {
                float volume = 0.8f;
                while (volume > 0.01f){
                    soundPool.setVolume(_id,volume,volume);
                    SystemClock.sleep(15);
                    volume-=0.01f;
                }
                soundPool.stop(_id);
            }
        }).start();
    }
}
