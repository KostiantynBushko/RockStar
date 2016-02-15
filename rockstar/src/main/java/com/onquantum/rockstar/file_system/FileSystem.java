package com.onquantum.rockstar.file_system;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.onquantum.rockstar.RockStarApplication;

import org.apache.http.cookie.CookieAttributeHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Admin on 7/19/15.
 */
public class FileSystem {
    private static String ICON_PATH = "/icon_files/";
    private static String SOUND_FILES_PATH = "/sound_files/";
    private static String TABS_FILES_PATH = "/tabs/";
    private static String CACHE = "/cache/";

    public static String GetRootPath() {
        String f = RockStarApplication.getContext().getFilesDir().toString();
        String c = RockStarApplication.getContext().getCacheDir().toString();
        //File rootPath = RockStarApplication.getContext().getFilesDir();//new File(RockStarApplication.getContext().getExternalFilesDir(null).toString());
        File rootPath = new File(RockStarApplication.getContext().getExternalFilesDir(null).toString());
        if(rootPath == null)
            return null;
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

    public static void DeleteTabFile(String fileName) {
        File file = new File(GetTabsFilesPath() + "/" + fileName);
        if(file.exists()) {
            file.delete();
        }
    }

    public static String GetCachePath() {
        File cache = new File(GetRootPath() + CACHE);
        if(cache == null)
            return null;
        if(cache.exists() == false) {
            if(cache.mkdirs() == false){
                return null;
            }
        }
        return cache.toString();
    }

    public static File CacheFile(File cacheFileSource) {
        File cacheFilePath = new File(Environment.getExternalStorageDirectory().toString() + "/rock_star_cache");
        if(!cacheFilePath.exists()) {
            cacheFilePath.mkdirs();
        } else {
            for (File file : cacheFilePath.listFiles())
                file.delete();
        }

        File cacheFileTarget = new File(cacheFilePath.toString() + "/" + cacheFileSource.getName());
        if(copyFile(cacheFileSource, cacheFileTarget)) {
            return cacheFileTarget;
        }
        return null;
    }

    public static void ClearCacheFile() {
        File file = new File(FileSystem.GetCachePath() + "/cache_tabs");
        if(file != null && file.exists())
            file.delete();
    }

    public static boolean copyFile(File source, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            bos = new BufferedOutputStream(new FileOutputStream(dest, false));

            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }
}
