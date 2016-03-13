package com.onquantum.rockstar.sequencer;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Debug;
import android.util.Log;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.gsqlite.DBGuitarTable;
import com.onquantum.rockstar.gsqlite.GuitarEntity;
import com.onquantum.rockstar.midi.*;

import com.onquantum.rockstar.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bushko on 12/20/14.
 */
public class QSoundPool {
    private static QSoundPool instance = new QSoundPool();
    public static QSoundPool getInstance() {
        return instance;
    }

    private SoundPool soundPool;
    private boolean loadedInProgress = false;
    private boolean successLoaded = false;

    //private String path = null;
    private String prefix;

    private Context context;
    private int load = 0;
    private int loadProgress;
    private static final float onePercentage = 24.0f * 6.0f  / 100.0f;


    private List<Integer>notes = new ArrayList<Integer>();
    private  QTrack[] tracks;

    public interface OnSoundPoolSuccessLoaded {
        public void soundPoolSuccessLoaded();
    }
    private OnSoundPoolSuccessLoaded onSoundPoolSuccessLoaded;
    public void setOnSoundPoolSuccessLoaded(OnSoundPoolSuccessLoaded onSoundPoolSuccessLoaded) {
        if(onSoundPoolSuccessLoaded == null) {
            this.onSoundPoolSuccessLoaded = null;
            return;
        }
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
                //Log.i("info"," QSoundPool : onGuitarPackageChange ");
            }
        });
    }

    private void Init() {
        soundPool = new SoundPool(200, AudioManager.STREAM_MUSIC,0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                load += 1;
                loadProgress = (int)(load / onePercentage);
                if (onProgressUpdate != null && loadProgress < 100)
                    onProgressUpdate.progressUpdate(loadProgress);
                if(sampleId >= (24 * 6)) {
                    //Log.i("info"," Sequencer soundPool : success loaded ");
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
        GuitarEntity guitarEntity = DBGuitarTable.GetCurrentActive(context);
        if(guitarEntity == null)
            return;
        Init();
        //Log.i("info","QSoundPool START LOAD SOUND" + soundPool.toString());
        loadSequenceNotes();
        if(successLoaded || loadedInProgress) {
            Log.i("info","  QSOUND POOL LOADED IN PROGRESS");
            return;
        }
        prefix = guitarEntity.article; //new Settings(context).getCurrentGuitarPackage();

        successLoaded = false;
        loadedInProgress = true;
        if(guitarEntity.id == 1) {
            new Thread(new LoadSoundFromRes()).start();
        } else {
            new Thread(new LoadSoundFromFile()).start();
        }
    }

    public void releaseSoundPool() {
        soundPool.release();
        soundPool = null;
        loadedInProgress = false;
        successLoaded = false;
        load = 0;
        loadProgress = 0;
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

    private void loadSequenceNotes() {
        InputStream inputStream = context.getResources().openRawResource(R.raw.baritone);
        try {
            QSequence sequence = QMidiSystem.getSequence(inputStream);
            tracks = sequence.getTracks();

        } catch (QInvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String GetNoteForGuitarString(int fretBar, int string) {
        return QMIDI.getStringForNote(tracks[0].get((string) + (6 * fretBar)).getMessage().getDataByteOne());
    }
    public int GetOctaveForGuitarString(int fretBar, int string) {
        return QMIDI.getOctaveForNote(tracks[0].get((string) + (6 * fretBar)).getMessage().getDataByteOne());
    }

    private class LoadSoundFromRes implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 25; i++) {
                for (int j = 0; j < 6; j++){
                    String file = prefix + "_" + Integer.toString(i) + "_" + Integer.toString(j);
                    int id = context.getResources().getIdentifier(file,"raw",context.getPackageName());
                    //Log.i("info"," FILE : " + file + " id = " + id);
                    try {
                        synchronized (QSoundPool.this) {
                            if(loadedInProgress) {
                                soundPool.load(context,id,1);
                            } else {
                                Log.i("info","  ************** BRAKE LOADING ID = " + prefix);
                                return;
                            }
                        }
                    }catch (Resources.NotFoundException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private class LoadSoundFromFile implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 25; i++) {
                for (int j = 0; j < 6; j++){
                    String file = prefix + "_" + Integer.toString(i) + "_" + Integer.toString(j);
                    File filePath = new File(FileSystem.GetSoundFilesPath(prefix) + "/" + file + ".ogg") ;
                    //Log.i("info"," FILE : " + filePath.getAbsolutePath());
                    try {
                        if(loadedInProgress) {
                            soundPool.load(filePath.getAbsolutePath(),1);
                        } else {
                            //Log.i("info","  ************** BRAKE LOADING FILE = " + file);
                            return;
                        }
                    }catch (Resources.NotFoundException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
