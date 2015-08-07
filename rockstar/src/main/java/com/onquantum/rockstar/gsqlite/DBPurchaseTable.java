package com.onquantum.rockstar.gsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

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

    public static PurchaseEntity GetPurchaseEntityByID(Context context, long id) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "SELECT * FROM " + DB_PURCHASE_TB + " WHERE " + ID + "=" + "'" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0)
            return null;
        cursor.moveToFirst();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.id = cursor.getInt(0);
        purchaseEntity.bundle = cursor.getString(1);
        purchaseEntity.product_name = cursor.getString(2);
        purchaseEntity.description = cursor.getString(3);
        purchaseEntity.price = cursor.getString(4);
        purchaseEntity.currency_code = cursor.getString(5);
        cursor.close();
        db.close();
        return purchaseEntity;
    }

    public static void AddPurchaseEntities(Context context, List<PurchaseEntity> purchaseEntities) {
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        for (PurchaseEntity purchaseEntity : purchaseEntities) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBPurchaseTable.BUNDLE, purchaseEntity.bundle);
            contentValues.put(DBPurchaseTable.PRODUCT_NAME, purchaseEntity.product_name);
            contentValues.put(DBPurchaseTable.DESCRIPTION, purchaseEntity.description);
            contentValues.put(DBPurchaseTable.PRICE,purchaseEntity.price);
            contentValues.put(DBPurchaseTable.CURRENCY_CODE, purchaseEntity.currency_code);
            long id = db.insert(DBPurchaseTable.DB_PURCHASE_TB, null, contentValues);
            Log.i("info", " PURCHASE TABLE INSERT : " + id + " " + purchaseEntity.toString());
        }
        db.close();
    }
}
