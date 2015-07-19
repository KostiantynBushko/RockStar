package com.onquantum.rockstar.gsqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 5/16/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "rockstar";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        new DBGuitarTable().Create(db);
        new DBFileTable().Create(db);
        new DBPurchaseTable().Create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
