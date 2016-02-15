package com.onquantum.rockstar.gsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
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
    public static String HAS_PURCHASED = "_has_purchased";

    @Override
    public void Create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_PURCHASE_TB + " ("
                + ID + " integer primary key autoincrement, "
                + BUNDLE + " text,"
                + PRODUCT_NAME + " text,"
                + DESCRIPTION + " text,"
                + PRICE + " text,"
                + CURRENCY_CODE + " text,"
                + HAS_PURCHASED + " integer" + ");"
        );
    }

    @Override
    public void Update(SQLiteDatabase db) {

    }

    public static List<PurchaseEntity> GetAllPurchaseEntity(Context context) {
        List<PurchaseEntity>purchaseEntityList = new ArrayList<PurchaseEntity>();

        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        Cursor cursor = db.query(DB_PURCHASE_TB, null, null, null,null, null, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.id = cursor.getInt(0);
            purchaseEntity.bundle = cursor.getString(1);
            purchaseEntity.product_name = cursor.getString(2);
            purchaseEntity.description = cursor.getString(3);
            purchaseEntity.price = cursor.getString(4);
            purchaseEntity.currency_code = cursor.getString(5);
            purchaseEntity.has_purchased = (cursor.getInt(6) == 1) ? true : false;
            purchaseEntityList.add(purchaseEntity);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return purchaseEntityList;
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
        purchaseEntity.has_purchased = (cursor.getInt(6) == 1) ? true : false;
        cursor.close();
        db.close();
        return purchaseEntity;
    }

    public static PurchaseEntity GetPurchaseEntityByBundle(Context context, String bundle) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "SELECT * FROM " + DB_PURCHASE_TB + " WHERE " + BUNDLE + "=" + "'" + bundle + "'";
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
        purchaseEntity.has_purchased = (cursor.getInt(6) == 1) ? true : false;
        cursor.close();
        db.close();
        return purchaseEntity;
    }

    public static boolean PurchaseEntityAlreadyExists(Context context, String bundle) {
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "SELECT " + ID + " FROM " + DB_PURCHASE_TB + " WHERE " + BUNDLE + "=" + "'" + bundle + "'" + " LIMIT 1";
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

    public static void AddPurchaseEntities(Context context, List<PurchaseEntity> purchaseEntities) {
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        for (PurchaseEntity purchaseEntity : purchaseEntities) {
            if(!PurchaseEntityAlreadyExists(context,purchaseEntity.bundle)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBPurchaseTable.BUNDLE, purchaseEntity.bundle);
                contentValues.put(DBPurchaseTable.PRODUCT_NAME, purchaseEntity.product_name);
                contentValues.put(DBPurchaseTable.DESCRIPTION, purchaseEntity.description);
                contentValues.put(DBPurchaseTable.PRICE,purchaseEntity.price);
                contentValues.put(DBPurchaseTable.CURRENCY_CODE, purchaseEntity.currency_code);
                contentValues.put(DBPurchaseTable.HAS_PURCHASED, (purchaseEntity.has_purchased == true) ? 1 : 0);
                long id = db.insert(DBPurchaseTable.DB_PURCHASE_TB, null, contentValues);
                //Log.i("info", " PURCHASE TABLE INSERT : " + purchaseEntity.toString());
            } else {
                // Update
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBPurchaseTable.BUNDLE, purchaseEntity.bundle);
                contentValues.put(DBPurchaseTable.PRODUCT_NAME, purchaseEntity.product_name);
                contentValues.put(DBPurchaseTable.DESCRIPTION, purchaseEntity.description);
                contentValues.put(DBPurchaseTable.PRICE,purchaseEntity.price);
                contentValues.put(DBPurchaseTable.CURRENCY_CODE, purchaseEntity.currency_code);
                contentValues.put(DBPurchaseTable.HAS_PURCHASED, (purchaseEntity.has_purchased == true) ? 1 : 0);
                long id = db.update(DBPurchaseTable.DB_PURCHASE_TB,contentValues,BUNDLE +"=?",new String[]{purchaseEntity.bundle});
                //Log.i("info", " PURCHASE TABLE UPDATE : " + GetPurchaseEntityByBundle(context,purchaseEntity.bundle).toString());
            }
        }
        db.close();
    }
    public static void AddPurchaseEntity(Context context, PurchaseEntity purchaseEntity) {
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
        if(!PurchaseEntityAlreadyExists(context,purchaseEntity.bundle)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBPurchaseTable.BUNDLE, purchaseEntity.bundle);
            contentValues.put(DBPurchaseTable.PRODUCT_NAME, purchaseEntity.product_name);
            contentValues.put(DBPurchaseTable.DESCRIPTION, purchaseEntity.description);
            contentValues.put(DBPurchaseTable.PRICE,purchaseEntity.price);
            contentValues.put(DBPurchaseTable.CURRENCY_CODE, purchaseEntity.currency_code);
            contentValues.put(DBPurchaseTable.HAS_PURCHASED, (purchaseEntity.has_purchased == true) ? 1 : 0);
            long id = db.insert(DBPurchaseTable.DB_PURCHASE_TB, null, contentValues);
            //Log.i("info", " PURCHASE TABLE INSERT : " + purchaseEntity.toString());
        } else {
            // Update
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBPurchaseTable.BUNDLE, purchaseEntity.bundle);
            contentValues.put(DBPurchaseTable.PRODUCT_NAME, purchaseEntity.product_name);
            contentValues.put(DBPurchaseTable.DESCRIPTION, purchaseEntity.description);
            contentValues.put(DBPurchaseTable.PRICE,purchaseEntity.price);
            contentValues.put(DBPurchaseTable.CURRENCY_CODE, purchaseEntity.currency_code);
            contentValues.put(DBPurchaseTable.HAS_PURCHASED, (purchaseEntity.has_purchased == true) ? 1 : 0);
            long id = db.update(DBPurchaseTable.DB_PURCHASE_TB,contentValues,BUNDLE +"=?",new String[]{purchaseEntity.bundle});
            //Log.i("info", " PURCHASE TABLE UPDATE : " + GetPurchaseEntityByBundle(context,purchaseEntity.bundle).toString());
        }
        db.close();
    }
}
