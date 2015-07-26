package com.onquantum.rockstar.gsqlite;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 7/26/15.
 */
public class PurchaseEntity {
    public int id = 0;
    public String bundle = null;
    public String product_name = null;
    public String description = null;
    public String price = null;
    public String currency_code = null;


    @Override
    public String toString() {
        return "id : " + id + ", bundle : " + bundle + ", product_name : " + product_name + ", description : " + description + ", price = " + price + ", currency_code = " + currency_code;
    }

    public PurchaseEntity CreatePurchaseEntity(JSONObject jsonObject) {
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
}
