package com.onquantum.rockstar.sequencer;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

/**
 * Created by Admin on 12/20/14.
 */
public class QSoundPool {
    private static final QSoundPool instance = new QSoundPool();
    public static QSoundPool getInstance() {
        return instance;
    }

    private String soundPath = "";
    private SoundPool soundPool;
    private boolean loadedInProgress = false;
    private boolean successLoaded = false;

    private String prefix = "clean";

    private Context context;

    private QSoundPool(){
        soundPool = new SoundPool(200, AudioManager.STREAM_MUSIC,0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.i("info", " Sequencer soundPool : sampleId = " + sampleId + " status = " + status);
                if(sampleId == 24 * 6) {
                    Log.i("info"," Sequencer soundPool : success loaded ");
                    successLoaded = true;
                    loadedInProgress = false;
                }
            }
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void loadSound() {
        successLoaded = false;
        loadedInProgress = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    for (int j = 0; j < 6; j++){
                        String file = prefix + "_" + Integer.toString(i) + "_" + Integer.toString(j);
                        int id = context.getResources().getIdentifier(file,"raw",context.getPackageName());
                        try {
                            soundPool.load(context,id,1);
                        }catch (Resources.NotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public boolean isSuccessLoaded() {
        return successLoaded;
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }
}
