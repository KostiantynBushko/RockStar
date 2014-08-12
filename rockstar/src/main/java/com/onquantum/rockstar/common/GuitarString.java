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
    private int soundID[] = new int[24];
    private Context context = null;

    public GuitarString(int streamId, int x, int y, Context context,SoundPool soundPool) {
        this.x = x;
        this.y = y;
        this.streamId = streamId;
        this.context = context;
        this.soundPool = soundPool;
    }

    public void set(int streamId, int x, int y) {
        this.x = x;
        this.y = y;
        this.streamId = streamId;
    }

    public void set(int x, int y) {
        Log.i("info", " X = " + Integer.toString(x));
        this.x = x;
        this.y = y;
        for(int xx = 0; xx< new Settings(context).getFretNumbers(); xx++) {
            int playId = (y + 1) + (6 * xx);
            soundID[xx] = soundPool.play(playId,0,0,1,0,1.0f);
        }
        soundPool.setVolume(soundID[x],volume,volume);
        soundPool.setPriority(soundID[x],1);
    }

    public void move(final int x, final int y) {
        try{
            soundPool.setVolume(soundID[this.x],0,0);
            soundPool.setVolume(soundID[x],volume,volume);
            this.x = x;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    float vol = 0.8f;
                    while (vol > 0.01f){
                        Log.i("info"," ... ");
                        soundPool.setVolume(soundID[x],vol,vol);
                        SystemClock.sleep(10);
                        vol-=0.1f;
                    }
                }
            });
        }catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        Log.i("info"," GuitarString STOP");
        for (int i = 0; i < new Settings(context).getFretNumbers(); i++) {
            if(i != this.x) {
                soundPool.stop(soundID[i]);
            }
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
