package com.onquantum.rockstar.gsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 5/16/15.
 */
public class DBGuitarTable extends DBAbstractTable{
    public  static String DB_GUITAR_TABLE = "guitar_tb";
    public static String ID = "_id";
    public static String NAME = "_name";
    public static String ICON = "_icon";
    public static String PURCHASE_ID = "_purchase_id";

    @Override
    public void Create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_GUITAR_TABLE + " ("
                        + ID + " integer primary key autoincrement,"
                        + NAME + " text,"
                        + ICON + " text,"
                        + PURCHASE_ID + " integer" + ");"
        );
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,"Clean");
        contentValues.put(ICON,"clean.png");
        contentValues.put(PURCHASE_ID,0);
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
            guitarEntity.icon = cursor.getString(2);
            guitarEntity.purchase_id = cursor.getInt(3);

            guitars.add(guitarEntity);
            Log.i("info"," GUITAR ITEM : " + cursor.getString(0) + " " + cursor.getString(1));
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
        guitarEntity.icon = cursor.getString(2);
        guitarEntity.purchase_id = cursor.getInt(3);
        cursor.close();
        db.close();
        return guitarEntity;
    }

    public static void AddGuitarEntities(Context context, List<GuitarEntity> guitarEntities) {
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        for (GuitarEntity guitarEntity : guitarEntities) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(NAME, guitarEntity.name);
            contentValues.put(ICON, guitarEntity.icon);
            contentValues.put(PURCHASE_ID, guitarEntity.purchase_id);
            long ret = db.insert(DB_GUITAR_TABLE, null, contentValues);
            Log.i("info"," INSERT : " + ret + " " + guitarEntity.toString());
        }
        db.close();
    }
}
