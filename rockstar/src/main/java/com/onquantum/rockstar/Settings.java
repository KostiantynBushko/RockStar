package com.onquantum.rockstar;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by saiber on 01.03.14.
 */
public class Settings {

    private static final String FRET_NUMBER = "fret_number";
    private static final String SOUND_CHANNELS = "sound_channels";
    private static final String GUITAR_STYLE = "guitar_style";
    private static final String DISTORTION = "distortion";
    private static final String SLIDE = "slide";

    public static final String STYLE_CLEAN = "clean";
    public static final String STYLE_DISTORTION = "style_distortion";
    public static final int MAX_FRET = 13;


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

    public void setGuitarStyle(String style) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(GUITAR_STYLE, style);
        editor.commit();
    }
    public String getGuitarStyle() {
        return settings.getString(GUITAR_STYLE, STYLE_DISTORTION);
    }

    // Distortion option on/off
    public boolean getDistortion() {
        return settings.getBoolean(DISTORTION,false);
    }
    public void setDistortion(boolean distortion) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(DISTORTION,distortion);
        editor.commit();
    }

    // Slide option on/off
    public boolean getSlide() {
        return settings.getBoolean(SLIDE,true);
    }
    public void setSlide(boolean isOn) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SLIDE,isOn);
        editor.commit();
    }

}
