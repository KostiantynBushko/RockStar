package com.onquantum.rockstar.gsqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Admin on 5/16/15.
 */
public class DBFileTable extends DBAbstractTable {
    private static String DB_TABLE_NAME = "file_tb";
    private static String ID            =   "_id";

    private static String PATH          = "_path";
    private static String FILE_NAME     = "_name";
    private static String FULL_PATH     = "_full_path";
    private static String FILE_SIZE     = "_size";
    private static String FILE_HASH     = "_hash";
    public static String GUITAR_ID = "_guitar_id";

    @Override
    public void Create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_TABLE_NAME + " ("
                        + ID + " integer primary key autoincrement,"
                        + PATH + " text,"
                        + FILE_NAME + " text,"
                        + FULL_PATH + " text,"
                        + FILE_SIZE + " long,"
                        + FILE_HASH + " long,"
                        + GUITAR_ID + " integer"
                        + ");"
        );
    }

    @Override
    public void Update(SQLiteDatabase db) {

    }
}
