package com.onquantum.rockstar.pentatonic_editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.onquantum.rockstar.R;
import com.onquantum.rockstar.svprimitive.DrawEngine;
import com.onquantum.rockstar.svprimitive.SBarCursor;
import com.onquantum.rockstar.svprimitive.SBitmap;
import com.onquantum.rockstar.svprimitive.SCircle;
import com.onquantum.rockstar.svprimitive.SLayer;
import com.onquantum.rockstar.svprimitive.SShape;
import com.onquantum.rockstar.svprimitive.SText;

import java.util.BitSet;
import java.util.Iterator;

/**
 * Created by Admin on 8/12/15.
 */
public class BarSelectView extends DrawEngine {

    private final int barCount = 24;
    private float barWidth;
    private SLayer cursorLayer = new SLayer();
    private SLayer layer = new SLayer();
    private SLayer barNumberLayer = new SLayer();
    private BitSet fretsMark = new BitSet(24);

    // Interface
    public interface OnBarSelectListener {
        public void onBarSelect(int barSelected);
    }
    OnBarSelectListener onBarSelectListener;
    public void SetOnBarSelectListener(OnBarSelectListener onBarSelectListener) {
        this.onBarSelectListener = onBarSelectListener;
    }

    Paint selectCirclePaint = null;
    SCircle barCursor = null;

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

        selectCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectCirclePaint.setColor(Color.YELLOW);
        selectCirclePaint.setAlpha(230);

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


        barCursor = new SCircle(barWidth * (lastSelectedBar + 1) -  barWidth * 0.42f, height / 2 + height * 0.15f, barWidth * 0.4f, selectCirclePaint);
        barCursor.setVisibleArea(new RectF(-100,height, width,100));
        barCursor.setKinematic(true);

        SetCurrentBar(0);
        cursorLayer.addShape(barCursor);

        addLayer(layer);
        addLayer(cursorLayer);
        addLayer(barNumberLayer);
    }


    private int lastSelectedBar = 0;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int motionAction = motionEvent.getAction();
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN : {
                int bar = (int)(motionEvent.getX() / barWidth);
                if(this.onBarSelectListener != null) {
                    if(bar == 24) {
                        onBarSelectListener.onBarSelect(0);
                    } else {
                        int b = (int)(motionEvent.getX() / barWidth + 1);
                        onBarSelectListener.onBarSelect(b);
                    }
                }
                //barCursor.Remove(0);
                if(lastSelectedBar == bar)
                    return true;
                /*RemoveCursor();
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.YELLOW);
                paint.setAlpha(230);
                SCircle barCursor = new SCircle(barWidth * (bar + 1) -  barWidth * 0.42f, height / 2 + height * 0.15f, barWidth * 0.4f, paint);
                barCursor.setVisibleArea(new RectF(-100,height, width,100));
                barCursor.setKinematic(true);
                cursorLayer.addShape(barCursor);*/
                //barCursor.move((int)(barWidth * (bar + 1) -  barWidth * 0.42f), (int)(height / 2 + height * 0.15f));
                barCursor.SetPosition((barWidth * (bar + 1) -  barWidth * 0.42f), (height / 2 + height * 0.15f));

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

    public void SetCurrentBar(int bar) {
        if(this.onBarSelectListener != null) {
            onBarSelectListener.onBarSelect(bar);
        }
        if(bar == 0)
            bar = 25;
        bar-=1;
        if(lastSelectedBar == bar)
            return;
        /*RemoveCursor();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.YELLOW);
        paint.setAlpha(230);*/

        /*if(barCursor == null) {
            barCursor = new SCircle(barWidth * (bar + 1) -  barWidth * 0.42f, height / 2 + height * 0.15f, barWidth * 0.4f, selectCirclePaint);
            barCursor.setVisibleArea(new RectF(-100,height, width,100));
            barCursor.setKinematic(true);
        }*/
        //barCursor.move((int)(barWidth * (bar + 1) -  barWidth * 0.42f), (int)(height / 2 + height * 0.15f));
        barCursor.SetPosition((barWidth * (bar + 1) -  barWidth * 0.42f), (height / 2 + height * 0.15f));

        //cursorLayer.addShape(barCursor);
        SText preview = barNumberLayer.getElementByIndex(lastSelectedBar, SText.class);
        preview.setColor(Color.WHITE);
        lastSelectedBar = bar;
        SText currentSelected = barNumberLayer.getElementByIndex(lastSelectedBar, SText.class);
        currentSelected.setColor(Color.YELLOW);
    }

    /*private void RemoveCursor() {
        synchronized (cursorLayer) {
            Iterator<SShape>iterator = cursorLayer.getShapeList().iterator();
            while (iterator.hasNext()) {
                iterator.next().Remove(0);
            }
        }
    }*/
}