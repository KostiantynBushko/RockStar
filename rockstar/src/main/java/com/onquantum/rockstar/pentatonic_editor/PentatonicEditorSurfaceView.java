package com.onquantum.rockstar.pentatonic_editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.onquantum.rockstar.svprimitive.DrawEngine;
import com.onquantum.rockstar.svprimitive.SCircle;
import com.onquantum.rockstar.svprimitive.SLayer;
import com.onquantum.rockstar.svprimitive.SLine;
import com.onquantum.rockstar.svprimitive.SShape;

import java.util.Iterator;

/**
 * Created by Admin on 8/10/15.
 */
public class PentatonicEditorSurfaceView extends DrawEngine {

    private SLayer touchLayer = null;
    private SLayer horizontalLineLayer = null;
    private SLayer verticalLine = null;

    private Paint paintString = null;
    private Paint paintQuartetDivider = null;
    private Paint paintDivider = null;

    private int quartetDivider = 100;
    private int slideStep = 1;
    private int quartetsCount = 10 * 4;
    private int slide = 0;

    private float touchX = 0, touchY = 0;


    public PentatonicEditorSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundColor(Color.WHITE);
    }

    @Override
    public void OnSurfaceChanged(int width, int height) {
        touchLayer = new SLayer();
        horizontalLineLayer = new SLayer();
        verticalLine = new SLayer();

        // Draw horizontal  line
        paintString = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintString.setStrokeWidth(2);
        int lineY = height / 7;
        for (int n = 0; n<6; n++) {
            SLine line = new SLine(0, lineY, this.width, lineY,paintString);
            line.setVisibleArea(new RectF(0.0f, height, width, 0.0f));
            lineY += height / 7;
            horizontalLineLayer.addShape(line);
        }

        // Draw vertical line quartet note
        paintDivider = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDivider.setColor(Color.LTGRAY);
        paintDivider.setAlpha(128);
        paintQuartetDivider = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintQuartetDivider.setColor(Color.GRAY);
        paintQuartetDivider.setStrokeWidth(8);


        int count = 0;
        Paint currentPaint = paintQuartetDivider;
        for (int i = 0; i < quartetDivider * (quartetsCount + 1); i += quartetDivider) {
            SLine line = new SLine(i, 40, i, height - 40, currentPaint);
            line.setVisibleArea(new RectF(0.0f, height, width, 0.0f));
            line.setKinematic(true);
            verticalLine.addShape(line);
            count++;
            if(count == 4) {
                count = 0;
                currentPaint = paintQuartetDivider;
            }else {
                currentPaint = paintDivider;
            }
        }

        addLayer(touchLayer);
        addLayer(horizontalLineLayer);
        addLayer(verticalLine);
    }

    public void movePentatonic(int move) {
        int length = quartetsCount * (quartetDivider);
        int outOfSlide = length - width;
        if((move > 0 && slide == 0) || move < 0 && ((Math.abs(slide) + width) == outOfSlide)) {
            return;
        }

        if(move  > 0 && move > Math.abs(slide)) {
            move = Math.abs(slide);
        } else if(move < 0 && (Math.abs(slide) + width + Math.abs(move)) >= length) {
            return;
        }

        slide += move;
        synchronized (verticalLine) {
            Iterator<SShape> iterator = verticalLine.getShapeList().iterator();
            while (iterator.hasNext()) {
                SShape shape = iterator.next();
                shape.move(move, 0);
            }
        }
    }

    boolean isTouchMove = false;
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int motionAction = motionEvent.getAction();
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN: {
                touchX = motionEvent.getX();
                touchY = motionEvent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if(!isTouchMove) {
                    /*SCircle circle = new SCircle((float)motionEvent.getX(), (float)motionEvent.getY(), 10.0f);
                    circle.setVisibleArea(new RectF(0f, getHeight(), getWidth(), 0));
                    circle.Remove(200);
                    touchLayer.addShape(circle);
                    Log.i("info", " TouchLayer : " + touchLayer.getShapeList().size());*/
                    //Log.i("info"," QUARTET X = " + (int)((Math.abs(slide) + touchX) / quartetDivider));
                }
                isTouchMove = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int direction = (int)(motionEvent.getX() - touchX);
                if(Math.abs(direction) > slideStep) {
                    isTouchMove = true;
                    int move = direction; //direction > 0 ? slideStep : -(slideStep);

                    movePentatonic(move);

                    touchX = motionEvent.getX();
                    touchY = motionEvent.getY();
                }
                break;
            }
            default:break;
        }
        return true;
    }

    private class Note {
        int x, y, bar;
    }
}
