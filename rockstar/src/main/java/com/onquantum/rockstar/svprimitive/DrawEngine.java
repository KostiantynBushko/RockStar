package com.onquantum.rockstar.svprimitive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.onquantum.rockstar.guitar.GuitarRenderer;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Admin on 8/8/15.
 */
public abstract class DrawEngine extends SurfaceView implements SurfaceHolder.Callback{
    public SurfaceHolder surfaceHolder;
    protected Context context;
    DrawFrame drawFrame;
    public int width;
    public int height;
    private boolean running = false;

    private int backgroundColor = Color.GRAY;

    private List<SLayer>obj = null;

    private boolean isSuccessLoaded = false;

    protected DrawEngineInterface drawEngineInterface;
    public void SetOnDrawEngineInterface(DrawEngineInterface drawEngineInterface) {
        this.drawEngineInterface = drawEngineInterface;
    }

    public DrawEngine(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        getHolder().addCallback(this);
        this.context = context;
    }

    // SurfaceHolder.CallBack implementation
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawFrame = new DrawFrame(this.getHolder());
        drawFrame.setRunning(true);
        drawFrame.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(running && this.width == width && this.height == height)
            return;
        obj = null;
        obj = Collections.synchronizedList(new ArrayList<SLayer>());
        this.width = width;
        this.height = height;
        this.surfaceHolder = holder;
        running = true;
        OnSurfaceChanged(width, height);
        if(drawEngineInterface != null)
            drawEngineInterface.SurfaceSuccessCreated();
        isSuccessLoaded = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawFrame.setRunning(false);
        while (retry) {
            try {
                drawFrame.join();
                retry = false;
            }catch (InterruptedException e){}
        }
    }

    public abstract void OnSurfaceChanged(int width, int height);

    public void addLayer(SLayer layer) {
        obj.add(layer);
    }


    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }


    private class DrawFrame extends Thread {
        private boolean isRunning = false;
        private SurfaceHolder surfaceHolder;

        public DrawFrame(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }
        public void setRunning(boolean running) {
            isRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (isRunning) {
                long time_start = System.currentTimeMillis();
                canvas = null;
                try {
                    canvas = this.surfaceHolder.lockCanvas(null);
                    if(canvas == null)
                        continue;
                    canvas.drawColor(backgroundColor);
                    int drawObjectsCount = 0;
                    synchronized (obj) {
                        for(int i = 0; i < obj.size(); i++) {
                            synchronized (obj.get(i)) {
                                SLayer layer = obj.get(i);
                                Iterator<SShape> iterator = layer.getShapeList().iterator();
                                while (iterator.hasNext()) {
                                    SShape shape = iterator.next();
                                    shape.draw(canvas);
                                    if(shape.remove)
                                        iterator.remove();
                                    drawObjectsCount++;
                                }
                            }
                        }
                    }
                    //Log.i("info","DRAW OBJECTS = " + drawObjectsCount);
                    drawObjectsCount = 0;

                } finally {
                    if(canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                //Frame limiting
                long time_end = System.currentTimeMillis();
                long frame_time = time_end - time_start;
                int sleep = (int) ((1000/30) - frame_time);
                if (sleep > 0){
                    try {
                        this.sleep(sleep);
                    } catch (InterruptedException e) {
                        Log.i("info","DRAW ENGINE : InterruptExeption : " + e.toString());
                    }
                }
            }
        }
    }

    public boolean isSuccessLoaded() { return isSuccessLoaded; }
}
