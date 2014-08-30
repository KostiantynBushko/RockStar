package com.onquantum.rockstar.tools;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import com.onquantum.rockstar.Settings;

/**
 * Created by Admin on 8/28/14.
 */
public class ExtSoundPool extends SoundPool implements SoundPool.OnLoadCompleteListener{
    private int string = 0;
    private Context context;

    public ExtSoundPool(int maxStreams, int streamType, int srcQuality, Context context, int string) {
        super(maxStreams, streamType, srcQuality);
        this.setOnLoadCompleteListener(this);
        this.string = string;
        this.context = context;

        final String prefix;
        if(new Settings(context).getDistortion()) {
            prefix = "distortion ";
        }else {
            prefix = "clean";
        }

        for (int i = 0; i < new Settings(context).getFretNumbers(); i++) {
            String file = prefix + "_" + Integer.toString(i) + "_" + Integer.toString(string);
            Log.i("info"," file = " + file + " string = " + string);
            int id = context.getResources().getIdentifier(file,"raw",context.getPackageName());
            this.load(context,id,1);
        }

    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        //Log.i("info","Sound Pool for String: " + this.string + " sample id = " + sampleId + " status = " + status);
        if (sampleId == new Settings(context).getFretNumbers()) {
            Log.i("info"," Sound Pool for String: " + this.string + " - complete loaded");
        }
    }
}
