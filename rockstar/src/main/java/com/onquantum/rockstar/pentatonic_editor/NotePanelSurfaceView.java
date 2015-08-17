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
import com.onquantum.rockstar.svprimitive.SText;

/**
 * Created by Admin on 8/10/15.
 */
public class NotePanelSurfaceView extends DrawEngine {

    SLayer layer = new SLayer();
    SLayer touchLayer = new SLayer();

    public NotePanelSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundColor(Color.LTGRAY);
        addLayer(touchLayer);
        addLayer(layer);
    }

    @Override
    public void OnSurfaceChanged(int width, int height) {
        super.OnSurfaceChanged(width, height);
        final String[] notes = {"B3","F#3", "D3", "A2", "E2","B1"};
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
