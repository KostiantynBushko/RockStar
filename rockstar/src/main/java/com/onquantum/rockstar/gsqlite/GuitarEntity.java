package com.onquantum.rockstar.gsqlite;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 7/12/15.
 */
public class GuitarEntity {
    public int id = 0;
    public String name = null;
    public String icon = null;
    public int purchase_id = 0;

    @Override
    public String toString() {
        return "id : " + id + ", name : " + name + ", icon : " + icon + ", purchase_id : " + purchase_id;
    }

    public static GuitarEntity GetGuitarEntity(JSONObject jsonObject) {
        GuitarEntity guitarEntity = new GuitarEntity();
        try {
            guitarEntity.id = jsonObject.getInt(DBGuitarTable.ID);
            guitarEntity.name = jsonObject.getString(DBGuitarTable.NAME);
            guitarEntity.icon = jsonObject.getString(DBGuitarTable.ICON);
            guitarEntity.purchase_id = jsonObject.getInt(DBGuitarTable.PURCHASE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return guitarEntity;
    }
}
