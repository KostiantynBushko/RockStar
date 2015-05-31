package com.onquantum.rockstar.sequencer;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Debug;
import android.util.Log;

import com.onquantum.rockstar.Settings;

import java.util.ArrayList;
import java.util.List;

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
    private List<SoundPool> soundPools = new ArrayList<SoundPool>();
    private boolean loadedInProgress = false;
    private boolean successLoaded = false;

    private String path = null;
    private String prefix;

    private Context context;
    private int load = 0;
    private int loadProgress;
    private static final float onePercentage = 24.0f * 6.0f  / 100.0f;

    public interface OnSoundPoolSuccessLoaded {
        public void soundPoolSuccessLoaded();
    }
    private OnSoundPoolSuccessLoaded onSoundPoolSuccessLoaded;
    public void setOnSoundPoolSuccessLoaded(OnSoundPoolSuccessLoaded onSoundPoolSuccessLoaded) {
        if (successLoaded) {
            onSoundPoolSuccessLoaded.soundPoolSuccessLoaded();
        } else {
            this.onSoundPoolSuccessLoaded = onSoundPoolSuccessLoaded;
        }
    }

    public interface OnProgressUpdate {
        public void progressUpdate(int progress);
    }
    OnProgressUpdate onProgressUpdate;
    public void setOnProgressUpdate(OnProgressUpdate onProgressUpdate) {
        this.onProgressUpdate = onProgressUpdate;
    }

    private QSoundPool(){
        Settings.SetOnGuitarPackageChange(new Settings.GuitarPackageListener() {
            @Override
            public void onGuitarPackageChange(String guitarPackage) {
                Log.i("info"," QSoundPool : onGuitarPackageChange ");
            }
        });
        soundPool = new SoundPool(200, AudioManager.STREAM_MUSIC,0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                load += 1;
                loadProgress = (int)(load / onePercentage);
                if (onProgressUpdate != null && loadProgress < 100)
                    onProgressUpdate.progressUpdate(loadProgress);
                if(sampleId >= (24 * 6)) {
                    Log.i("info"," Sequencer soundPool : success loaded ");
                    successLoaded = true;
                    loadedInProgress = false;
                    if (onSoundPoolSuccessLoaded != null)
                        onSoundPoolSuccessLoaded.soundPoolSuccessLoaded();
                }
            }
        });
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private boolean stop = false;
    public void loadSound() {
        Log.i("info","QSoundPool START LOAD SOUND" + soundPool.toString());
        if(successLoaded || loadedInProgress) {
            Log.i("info","  QSOUND POOL LOADED IN PROGRESS");
            return;
        }
        prefix = new Settings(context).getCurrentGuitarPackage();

        successLoaded = false;
        loadedInProgress = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 25; i++) {
                    for (int j = 0; j < 6; j++){
                        String file = prefix + "_" + Integer.toString(i) + "_" + Integer.toString(j);
                        int id = context.getResources().getIdentifier(file,"raw",context.getPackageName());
                        Log.i("info"," FILE : " + file + " id = " + id);
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
        Log.i("info","QSoundPool GET SOUND POOL " + soundPool.toString());
        return soundPool;
    }
    public int getSuccessLoad() {
        return load;
    }
    public int getLoadProgress() {
        return loadProgress;
    }

    public void Destroy() {

    }
}
