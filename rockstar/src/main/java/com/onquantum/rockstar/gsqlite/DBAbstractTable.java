package com.onquantum.rockstar.gsqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Admin on 5/16/15.
 */
public abstract class DBAbstractTable/*<T extends  DBAbstractTable>*/ {

    public abstract void Create(SQLiteDatabase db);
    public abstract void Update(SQLiteDatabase db);

    public static long GetCountOfRows(Context context, String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName;
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();

        long count = DatabaseUtils.queryNumEntries(db,tableName);
        db.close();
        return count;
    }
}
