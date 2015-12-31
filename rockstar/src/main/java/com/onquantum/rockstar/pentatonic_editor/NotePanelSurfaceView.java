package com.onquantum.rockstar.pentatonic_editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.onquantum.rockstar.svprimitive.DrawEngine;
import com.onquantum.rockstar.svprimitive.SCircle;
import com.onquantum.rockstar.svprimitive.SLayer;
import com.onquantum.rockstar.svprimitive.SShape;
import com.onquantum.rockstar.svprimitive.SText;

import java.util.Iterator;

/**
 * Created by Admin on 8/10/15.
 */
public class NotePanelSurfaceView extends DrawEngine {

    SLayer layer = null;
    //SLayer touchLayer = null;

    public NotePanelSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundColor(Color.LTGRAY);
        layer = new SLayer();
    }

    @Override
    public void OnSurfaceChanged(int width, int height) {
        //touchLayer = new SLayer();
        addLayer(layer);
        final String[] notes = {"B3","F#3", "D3", "A2", "E2","B1"};
        DrawNote(notes);
        /*float step = height / 7;
        for(int i = 0; i < 6; i++) {
            SText text = new SText((float)(width / 2), step, width * 0.4f);
            text.setVisibleArea(new RectF(-getWidth(), getHeight(), getWidth() * 2, 0));
            text.setText(notes[i]);
            layer.addShape(text);
            step += height / 7;
        }*/

        //addLayer(touchLayer);
    }

    public void DrawNote(String[] notes) {
        synchronized (layer) {
            Iterator<SShape>iterator = layer.getShapeList().iterator();
            while (iterator.hasNext()) {
                iterator.next().Remove(0);
            }
        }
        float step = height / 7;
        for(int i = 0; i < 6; i++) {
            SText text = new SText((float)(width / 2), step, width * 0.4f);
            text.setVisibleArea(new RectF(-getWidth(), getHeight(), getWidth() * 2, 0));
            text.setText(notes[i]);
            layer.addShape(text);
            step += height / 7;
        }
    }
}
