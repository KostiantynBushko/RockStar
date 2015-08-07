package com.onquantum.rockstar.gsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Admin on 7/26/15.
 */
public class PurchaseEntity {
    public int id = 0;
    public String bundle = null;
    public String product_name = null;
    public String description = null;
    public String price = "0.0";
    public String currency_code = null;


    @Override
    public String toString() {
        return "id : " + id + ", bundle : " + bundle + ", product_name : " + product_name + ", description : " + description + ", price = " + price + ", currency_code = " + currency_code;
    }

    public static PurchaseEntity CreatePurchaseEntity(JSONObject jsonObject) {
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        try {
            purchaseEntity.id = jsonObject.getInt(DBPurchaseTable.ID);
            purchaseEntity.bundle = jsonObject.getString(DBPurchaseTable.BUNDLE);
            purchaseEntity.product_name = jsonObject.getString(DBGuitarTable.DESCRIPTION);
            purchaseEntity.price = jsonObject.getString(DBPurchaseTable.PRICE);
            purchaseEntity.currency_code = jsonObject.getString(DBPurchaseTable.CURRENCY_CODE);
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return purchaseEntity;
    }

    public float getPrice() {
        return Float.parseFloat(price);
    }
}
