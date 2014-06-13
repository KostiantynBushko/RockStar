package com.onquantum.rockstar;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by saiber on 01.03.14.
 */
public class Settings {
    private static final String FRET_NUMBER = "fret_number";
    private static final String SOUND_CHANNELS = "sound_channels";

    private final SharedPreferences settings;

    public Settings(Context context) {
        settings = context.getSharedPreferences(context.getResources().getString(R.string.app_name),Context.MODE_PRIVATE);
    }
    public void setFretNumbers(int count) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(FRET_NUMBER,count);
        editor.commit();
    }
    public int getFretNumbers() {
        return settings.getInt(FRET_NUMBER,8);
    }
    public void setSoundChannels(int soundChannels) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SOUND_CHANNELS, soundChannels);
        editor.commit();
    }
    public int getSoundChannels() {
        return settings.getInt(SOUND_CHANNELS,2);
    }
}
