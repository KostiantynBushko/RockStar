package com.onquantum.rockstar.tools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.onquantum.rockstar.svprimitive.DrawEngine;
import com.onquantum.rockstar.svprimitive.SCircle;
import com.onquantum.rockstar.svprimitive.SLayer;
import com.onquantum.rockstar.svprimitive.SShape;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Admin on 8/9/15.
 */
public class SurfaceViewEditor extends DrawEngine {

    SLayer touchLayer = new SLayer();
    SLayer pentatonicList = new SLayer();

    public SurfaceViewEditor(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundColor(Color.RED);
    }

    @Override
    public void OnSurfaceChanged(int width, int height) {
        addLayer(pentatonicList);
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int motionAction = motionEvent.getAction();
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN: {
                SCircle circle = new SCircle((float)motionEvent.getX(), (float)motionEvent.getY(), 10.0f);
                circle.setVisibleArea(new RectF(0f, getHeight(), getWidth(), 0));
                circle.Remove(200);
                touchLayer.addShape(circle);
                Log.i("info"," TouchLayer : " + touchLayer.getShapeList().size());
            }
            default:break;
        }
        return true;
    }
}
