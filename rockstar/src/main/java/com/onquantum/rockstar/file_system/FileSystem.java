package com.onquantum.rockstar.file_system;

import android.content.Context;
import android.util.Log;

import com.onquantum.rockstar.RockStarApplication;

import org.apache.http.cookie.CookieAttributeHandler;

import java.io.File;

/**
 * Created by Admin on 7/19/15.
 */
public class FileSystem {
    private static String ICON_PATH = "/icon_files/";
    private static String SOUND_FILES_PATH = "/sound_files/";
    private static String TABS_FILES_PATH = "/tabs/";
    private static String CACHE = "/cache/";

    public static String GetRootPath() {
        File rootPath = new File(RockStarApplication.getContext().getExternalFilesDir("/").toString());
        if(rootPath.exists() == false) {
            if(rootPath.mkdirs() == false) {
                return null;
            }
        }
        return rootPath.toString();
    }

    public static String GetIconPath() {
        File iconPath = new File(GetRootPath() + ICON_PATH);
        if(iconPath.exists() == false) {
            if(iconPath.mkdirs() == false) {
                return null;
            }
        }
        return iconPath.toString();
    }

    public static String GetSoundFilesPath() {
        File soundFilesPath = new File(GetRootPath() + SOUND_FILES_PATH);
        if(soundFilesPath.exists() == false) {
            if(soundFilesPath.mkdirs() == false) {
                return null;
            }
        }
        return soundFilesPath.toString();
    }

    public static String GetSoundFilesPath(String dir) {
        File soundFilesPath = new File(GetRootPath() + SOUND_FILES_PATH + dir);
        if(soundFilesPath.exists() == false) {
            if(soundFilesPath.mkdirs() == false) {
                return null;
            }
        }
        return soundFilesPath.toString();
    }

    public static String GetTabsFilesPath() {
        File tabsPath = new File(GetRootPath() + TABS_FILES_PATH);
        if(tabsPath.exists() == false) {
            if(tabsPath.mkdirs() == false) {
                return null;
            }
        }
        return tabsPath.toString();
    }
    public static String GetCachePath() {
        File cache = new File(GetRootPath() + CACHE);
        if(cache.exists() == false) {
            if(cache.mkdirs() == false){
                return null;
            }
        }
        return cache.toString();
    }
}
