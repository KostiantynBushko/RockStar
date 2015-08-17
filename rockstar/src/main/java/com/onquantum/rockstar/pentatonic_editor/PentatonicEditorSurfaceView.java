package com.onquantum.rockstar.pentatonic_editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.onquantum.rockstar.svprimitive.DrawEngine;
import com.onquantum.rockstar.svprimitive.SCircle;
import com.onquantum.rockstar.svprimitive.SLayer;

/**
 * Created by Admin on 8/10/15.
 */
public class PentatonicEditorSurfaceView extends DrawEngine {

    SLayer touchLayer = new SLayer();

    public PentatonicEditorSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundColor(Color.WHITE);
        addLayer(touchLayer);
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
                Log.i("info", " TouchLayer : " + touchLayer.getShapeList().size());
                break;
            }
            default:break;
        }
        return true;
    }
}
