package com.onquantum.rockstar.file_system;

import com.onquantum.rockstar.RockStarApplication;

/**
 * Created by Admin on 7/19/15.
 */
public class FileSystem {
    public static String ROOT_PATH = RockStarApplication.getContext().getExternalFilesDir("/").toString();
    public static String ICON_PATH = ROOT_PATH + "/icon_files/";
    public static String SOUND_FILES_PATH = ROOT_PATH + "/sound_files/";
}
