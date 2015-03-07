package com.onquantum.rockstar;

import android.content.Context;
import android.content.SharedPreferences;

import com.onquantum.rockstar.common.FretsSlider;

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

    // Guitar neck view options
    private static final String SHOW_FRETS_NUMBER = "show_frets_number";
    private static final String SHOW_FRETS_SLIDER = "show_frets_slider";
    private static final String SHOW_TOUCHES = "show_touches";

    // Guitar package type
    private static final String CURRENT_GUITAR_PACKAGE = "current_guitar_package";

    private final SharedPreferences settings;

    /**********************************************************************************************/
    // Interface section
    public interface FretsNumberVisible{
        public void isFretsNumberVisible(boolean visibility);
    }
    private FretsNumberVisible fretsNumberVisible;
    public void setOnFretsNumberVisibleListener(FretsNumberVisible fretsNumberVisible){
        this.fretsNumberVisible = fretsNumberVisible;
    }

    public interface ShowTouchesListener{
        public void showTouches(boolean visibility);
    }
    private ShowTouchesListener showTouchesListener;
    public void setShowTouchesListener(ShowTouchesListener showTouchesListener){
        this.showTouchesListener = showTouchesListener;
    }

    public interface FretsSliderListener {
        public void showFretsSlider(boolean visibility);
    }
    public FretsSliderListener fretsSliderListener;
    public void setOnFretsSliderListener(FretsSliderListener fretsSliderListener){
        this.fretsSliderListener = fretsSliderListener;
    }

    public interface GuitarPackageListener {
        public void onGuitarPackageChange(String guitarPackage);
    }
    public static GuitarPackageListener guitarPackageListener;
    public static void SetOnGuitarPackageChange(GuitarPackageListener guitarPackageListener) {
        Settings.guitarPackageListener = guitarPackageListener;
    }
    // End interface section
    /**********************************************************************************************/

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

    // View options
    public boolean isFretsNumberVisible() {
        return settings.getBoolean(SHOW_FRETS_NUMBER, false);
    }
    public void setFretNumberVisibility(boolean visibility) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SHOW_FRETS_NUMBER, visibility);
        editor.commit();
        if (fretsNumberVisible != null)
            fretsNumberVisible.isFretsNumberVisible(visibility);
    }
    public boolean isFretsSliderVisible() {
        return settings.getBoolean(SHOW_FRETS_SLIDER, false);
    }
    public void setFretsSliderVisibility(boolean visibility) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SHOW_FRETS_SLIDER, visibility);
        editor.commit();
        if (fretsSliderListener != null)
            fretsSliderListener.showFretsSlider(visibility);
    }
    public boolean isTouchesVisible(){
        return settings.getBoolean(SHOW_TOUCHES,false);
    }
    public void setTouchesVisibility(boolean visibility){
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SHOW_TOUCHES,visibility);
        editor.commit();
        if (showTouchesListener != null)
            showTouchesListener.showTouches(visibility);
    }

    // Guitar type
    public String getCurrentGuitarPackage() {
        return settings.getString(CURRENT_GUITAR_PACKAGE, "clean");
    }
    public void setCurrentGuitarPackage(String guitarPackage) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CURRENT_GUITAR_PACKAGE, guitarPackage);
        editor.commit();
        if(guitarPackageListener != null) {
            guitarPackageListener.onGuitarPackageChange(guitarPackage);
        }
    }
}
