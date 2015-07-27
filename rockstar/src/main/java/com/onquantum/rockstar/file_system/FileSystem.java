package com.onquantum.rockstar.file_system;

import android.content.Context;

import com.onquantum.rockstar.RockStarApplication;

import java.io.File;

/**
 * Created by Admin on 7/19/15.
 */
public class FileSystem {
    private static String ROOT_PATH = RockStarApplication.getContext().getExternalFilesDir("/").toString();
    private static String ICON_PATH = ROOT_PATH + "/icon_files/";
    private static String SOUND_FILES_PATH = ROOT_PATH + "/sound_files/";

    public static String GetRootPath() {
        File rootPath = new File(ROOT_PATH);
        if(rootPath.exists() == false) {
            if(rootPath.mkdirs() == false) {
                return null;
            }
        }
        return rootPath.toString();
    }

    public static String GetIconPath() {
        File iconPath = new File(ICON_PATH);
        if(iconPath.exists() == false) {
            if(iconPath.mkdirs() == false) {
                return null;
            }
        }
        return iconPath.toString();
    }

    public static String GetSoundFilesPath() {
        File soundFilesPath = new File(SOUND_FILES_PATH);
        if(soundFilesPath.exists() == false) {
            if(soundFilesPath.mkdirs() == false) {
                return null;
            }
        }
        return soundFilesPath.toString();
    }
}
