package com.onquantum.rockstar.pentatonic_editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.onquantum.rockstar.R;
import com.onquantum.rockstar.svprimitive.DrawEngine;
import com.onquantum.rockstar.svprimitive.SBarCursor;
import com.onquantum.rockstar.svprimitive.SBitmap;
import com.onquantum.rockstar.svprimitive.SLayer;
import com.onquantum.rockstar.svprimitive.SShape;
import com.onquantum.rockstar.svprimitive.SText;

import java.util.BitSet;

/**
 * Created by Admin on 8/12/15.
 */
public class BarSelectView extends DrawEngine {

    private final int barCount = 24;
    private float barWidth;
    private SLayer layer = new SLayer();
    private SLayer barNumberLayer = new SLayer();
    private BitSet fretsMark = new BitSet(24);

    private SShape barCursor = null;

    // Interface
    public interface OnBarSelectListener {
        public void onBarSelect(int barSelected);
    }
    OnBarSelectListener onBarSelectListener;
    public void SetOnBarSelectListener(OnBarSelectListener onBarSelectListener) {
        this.onBarSelectListener = onBarSelectListener;
    }


    public BarSelectView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundColor(Color.DKGRAY);
        fretsMark.set(0);
        fretsMark.set(2);
        fretsMark.set(4);
        fretsMark.set(6);
        fretsMark.set(8);
        fretsMark.set(11);
        fretsMark.set(14);
        fretsMark.set(16);
        fretsMark.set(18);
        fretsMark.set(20);
        fretsMark.set(23);
    }

    public void OnSurfaceChanged(int width, int height) {
        barWidth = (float)width / 25;

        float barStartPosition = 0;
        for (int i = 0; i < 25; i ++) {
            SBitmap bar;
            if(i < 24) {
                if (fretsMark.get(i)) {
                    bar = new SBitmap(barStartPosition, height * 0.3f, barWidth, height * 0.7f, context ,R.drawable.b1);
                } else {
                    bar = new SBitmap(barStartPosition, height * 0.3f, barWidth, height * 0.7f, context ,R.drawable.b0);
                }
            } else {
                bar = new SBitmap(barStartPosition, height * 0.3f, barWidth, height * 0.7f, context ,R.drawable.sound_capture);
                bar.rotate(180);
            }

            bar.setVisibleArea(new RectF(0, height, width, 0));
            layer.addShape(bar);
            barStartPosition += barWidth;
        }

        barStartPosition = 0;
        for (int i = 0; i < 25; i++) {
            SBitmap bar = new SBitmap(barStartPosition, height * 0.3f, barWidth / 7, height * 0.7f, context ,R.drawable.lad);
            bar.setVisibleArea(new RectF(0, height, width, 0));
            barStartPosition += barWidth;
            layer.addShape(bar);
        }

        barStartPosition = 0;
        for (int i = 0; i < 25; i++) {
            SText number = new SText(barStartPosition + barWidth / 2, height * 0.12f, height * 0.2f);
            if(i == 24) {
                number.setText("0");
            } else {
                number.setText(Integer.toString(i + 1));
            }
            number.setVisibleArea(new RectF(0, height, width, 0));
            number.setColor(Color.WHITE);
            barStartPosition += barWidth;
            barNumberLayer.addShape(number);
        }

        barCursor = new SBarCursor(0, (int)(height * 0.3f), (int)(barWidth), height);
        barCursor.setVisibleArea(new RectF(0,height, width,0));
        barCursor.setColor(Color.YELLOW);
        layer.addShape(barCursor);
        
        SText currentSelected = barNumberLayer.getElementByIndex(lastSelectedBar, SText.class);
        currentSelected.setColor(Color.YELLOW);

        addLayer(layer);
        addLayer(barNumberLayer);
    }


    private int lastSelectedBar = 0;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int motionAction = motionEvent.getAction();
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN : {
                int bar = (int)(motionEvent.getX() / barWidth);
                if(this.onBarSelectListener != null)
                    onBarSelectListener.onBarSelect((int)(motionEvent.getX() / barWidth + 1));
                barCursor.move((int) (bar * barWidth), 0);
                SText preview = barNumberLayer.getElementByIndex(lastSelectedBar, SText.class);
                preview.setColor(Color.WHITE);
                lastSelectedBar = bar;
                SText currentSelected = barNumberLayer.getElementByIndex(lastSelectedBar, SText.class);
                currentSelected.setColor(Color.YELLOW);
                break;
            }
            default:break;
        }
        return true;
    }
}