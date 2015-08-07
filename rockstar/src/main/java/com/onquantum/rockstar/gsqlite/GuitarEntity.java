package com.onquantum.rockstar.gsqlite;

import android.util.Log;

import com.onquantum.rockstar.file_system.FileSystem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Admin on 7/12/15.
 */
public class GuitarEntity {
    public int id = 0;
    public String name = null;
    public String article = null;
    public String icon = null;
    public int purchase_id = 0;
    public String sample_sound = null;
    public String description = null;
    public boolean success_purchased = false;
    public boolean is_active = false;

    @Override
    public String toString() {
        return "id : " + id
                + ", name : " + name
                + ", article : " + article
                + ", icon : " + icon
                + ", purchase_id : " + purchase_id
                + ", sample_sound = " + sample_sound
                + ", description = " + description;
    }

    public static GuitarEntity CreateGuitarEntity(JSONObject jsonObject) {
        GuitarEntity guitarEntity = new GuitarEntity();
        try {
            guitarEntity.id = jsonObject.getInt(DBGuitarTable.ID);
            guitarEntity.name = jsonObject.getString(DBGuitarTable.NAME);
            guitarEntity.article = jsonObject.getString(DBGuitarTable.ARTICLE);
            guitarEntity.purchase_id = jsonObject.getInt(DBGuitarTable.PURCHASE_ID);
            guitarEntity.icon = jsonObject.getString(DBGuitarTable.ICON);
            guitarEntity.sample_sound = jsonObject.getString(DBGuitarTable.SAMPLE_SOUND);
            guitarEntity.success_purchased = false;
            guitarEntity.description = jsonObject.getString(DBGuitarTable.DESCRIPTION);
            guitarEntity.is_active = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return guitarEntity;
    }

    public boolean isSoundPackAvailable() {
        if(id == 1)
            return true;
        File filesDir = new File(FileSystem.GetSoundFilesPath(article));
        if(filesDir.isDirectory()) {
            long fileCount = filesDir.list().length;
            if(fileCount < 150)
                return false;
            return true;
        }
        return false;
    }

    public boolean isSoundPackAvailable(long[] progress) {
        if(id == 1)
            return true;
        File filesDir = new File(FileSystem.GetSoundFilesPath(article));
        if(filesDir.isDirectory()) {
            long fileCount = filesDir.list().length;
            progress[0] = fileCount;
            if(fileCount < 150)
                return false;
            return true;
        }
        return false;
    }

}
