package com.onquantum.rockstar.gsqlite;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Admin on 7/19/15.
 */
public class DBPurchaseTable extends DBAbstractTable {

    public static String DB_PURCHASE_TB = "purchase_tb";
    public static String ID = "_id";
    public static String BUNDLE = "_bundle";
    public static String PRODUCT_NAME = "_product_name";
    public static String DESCRIPTION = "_description";
    public static String PRICE = "_price";
    public static String CURRENCY_CODE = "_currency_code";

    @Override
    public void Create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_PURCHASE_TB + " ("
                + ID + " integer primary key autoincrement, "
                + BUNDLE + " text,"
                + PRODUCT_NAME + " text,"
                + DESCRIPTION + " text,"
                + PRICE + " text,"
                + CURRENCY_CODE + " text" + ");"
        );
    }

    @Override
    public void Update(SQLiteDatabase db) {

    }
}
