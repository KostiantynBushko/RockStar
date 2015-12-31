package com.onquantum.rockstar.gsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.util.Log;

import com.onquantum.rockstar.RockStarApplication;
import com.onquantum.rockstar.services.UpdateGuitarsIconService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 5/16/15.
 */
public class DBGuitarTable extends DBAbstractTable{
    public  static String DB_GUITAR_TABLE = "guitar_tb";
    public static String ID = "_id";
    public static String NAME = "_name";
    public static String ARTICLE = "_article";
    public static String PURCHASE_ID = "_purchase_id";
    public static String ICON = "_icon";
    public static String SAMPLE_SOUND = "_sample_sound";
    public static String DESCRIPTION = "_description";
    public static String SUCCESS_PURCHASED = "_success_purchased";
    public static String IS_ACTIVE = "is_available";

    @Override
    public void Create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_GUITAR_TABLE + " ("
                        + ID + " integer primary key autoincrement,"
                        + NAME + " text,"
                        + ARTICLE + " text,"
                        + PURCHASE_ID + " integer,"
                        + ICON + " text,"
                        + SAMPLE_SOUND + " text,"
                        + SUCCESS_PURCHASED + " integer,"
                        + DESCRIPTION + " text,"
                        + IS_ACTIVE + " integer" + ");"
        );
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,"Clean");
        contentValues.put(ARTICLE, "clean");
        contentValues.put(PURCHASE_ID,0);
        contentValues.put(SUCCESS_PURCHASED,1);
        contentValues.put(ICON,"clean.png");
        contentValues.put(SAMPLE_SOUND, "clean.mp3");
        contentValues.put(DESCRIPTION,"Clean electric guitar");
        contentValues.put(IS_ACTIVE,1);


        long id = db.insert(DB_GUITAR_TABLE,null,contentValues);
        Log.i("info","INSERT " + DB_GUITAR_TABLE + " id: " + id);
    }

    @Override
    public void Update(SQLiteDatabase db) {}


    public static List<GuitarEntity> GetAllGuitarsEntity(Context context) {
        List<GuitarEntity>guitars = new ArrayList<GuitarEntity>();

        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        Cursor cursor = db.query(DB_GUITAR_TABLE, null, null, null,null, null, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            GuitarEntity guitarEntity = new GuitarEntity();
            guitarEntity.id = cursor.getInt(0);
            guitarEntity.name = cursor.getString(1);
            guitarEntity.article = cursor.getString(2);
            guitarEntity.purchase_id = cursor.getInt(3);
            guitarEntity.icon = cursor.getString(4);
            guitarEntity.sample_sound = cursor.getString(5);
            guitarEntity.success_purchased = (cursor.getInt(6) == 1) ? true : false;
            guitarEntity.description = cursor.getString(7);
            guitarEntity.is_active = (cursor.getInt(8) == 1) ? true : false;
            guitars.add(guitarEntity);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return guitars;
    }

    public static GuitarEntity GetGuitarEntityByID(Context context, long id) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "SELECT * FROM " + DB_GUITAR_TABLE + " WHERE " + ID + "=" + "'" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();
        GuitarEntity guitarEntity = new GuitarEntity();
        guitarEntity.id = cursor.getInt(0);
        guitarEntity.name = cursor.getString(1);
        guitarEntity.article = cursor.getString(2);
        guitarEntity.purchase_id = cursor.getInt(3);
        guitarEntity.icon = cursor.getString(4);
        guitarEntity.sample_sound = cursor.getString(5);
        guitarEntity.success_purchased = (cursor.getInt(6) == 1) ? true : false;
        guitarEntity.description = cursor.getString(7);
        guitarEntity.is_active = (cursor.getInt(8) == 1) ? true : false;
        cursor.close();
        db.close();
        return guitarEntity;
    }

    public static GuitarEntity GetGuitarEntityByArticle(Context context, String article) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "SELECT * FROM " + DB_GUITAR_TABLE + " WHERE " + ARTICLE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{article});
        if(cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        GuitarEntity guitarEntity = getGuitarEntity(cursor);
        cursor.close();
        db.close();
        return guitarEntity;
    }

    private static GuitarEntity getGuitarEntity(Cursor cursor) {
        if(cursor.getCount() == 0)
            return null;
        GuitarEntity guitarEntity = new GuitarEntity();
        guitarEntity.id = cursor.getInt(0);
        guitarEntity.name = cursor.getString(1);
        guitarEntity.article = cursor.getString(2);
        guitarEntity.purchase_id = cursor.getInt(3);
        guitarEntity.icon = cursor.getString(4);
        guitarEntity.sample_sound = cursor.getString(5);
        guitarEntity.success_purchased = (cursor.getInt(6) == 1) ? true : false;
        guitarEntity.description = cursor.getString(7);
        guitarEntity.is_active = (cursor.getInt(8) == 1) ? true : false;

        return guitarEntity;
    }

    public static void AddGuitarEntities(Context context, List<GuitarEntity> guitarEntities) {
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        for (GuitarEntity guitarEntity : guitarEntities) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, guitarEntity.name);
            contentValues.put(ARTICLE, guitarEntity.article);
            contentValues.put(PURCHASE_ID, guitarEntity.purchase_id);
            contentValues.put(ICON, guitarEntity.icon);
            contentValues.put(SAMPLE_SOUND,guitarEntity.sample_sound);
            contentValues.put(SUCCESS_PURCHASED, (guitarEntity.success_purchased == true) ? 1 : 0);
            contentValues.put(DESCRIPTION,guitarEntity.description);
            contentValues.put(IS_ACTIVE, (guitarEntity.is_active == true) ? 1 : 0);
            long ret = db.insert(DB_GUITAR_TABLE, null, contentValues);
            Log.i("info"," GUITAR TABLE INSERT : " + ret + " " + guitarEntity.toString());
        }
        db.close();
    }

    public static void SetActiveGuitar(Context context, long id) {
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        db.execSQL("UPDATE " + DB_GUITAR_TABLE + " SET " + IS_ACTIVE + " = 0" + " WHERE " + IS_ACTIVE + "=1" );
        db.execSQL("UPDATE " + DB_GUITAR_TABLE + " SET " + IS_ACTIVE + " = 1" + " WHERE " + ID + "=" + id);
        db.close();
    }

    public static String GetCurrentActivePackageName(Context context) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "SELECT " + ARTICLE + " FROM " + DB_GUITAR_TABLE + " WHERE " + IS_ACTIVE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{"1"});
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public static GuitarEntity GetCurrentActive(Context context) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "SELECT * FROM " + DB_GUITAR_TABLE + " WHERE " + IS_ACTIVE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{"1"});
        if(cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        GuitarEntity guitarEntity = getGuitarEntity(cursor);
        cursor.close();
        db.close();
        return guitarEntity;
    }

    public static boolean GuitarPackageAlreadyExists(Context context, String article) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "SELECT " + ID + " FROM " + DB_GUITAR_TABLE + " WHERE " + ARTICLE + "=" + "'" + article + "'" + " LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }
}
