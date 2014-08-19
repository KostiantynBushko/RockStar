package com.onquantum.rockstar.tools;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.Pentatonic;
import com.onquantum.rockstar.glprimitive.GLDGuitarString;
import com.onquantum.rockstar.glprimitive.GLDTexture;
import com.onquantum.rockstar.svprimitive.SBitmap;
import com.onquantum.rockstar.svprimitive.SCircle;
import com.onquantum.rockstar.svprimitive.SGuitarString;
import com.onquantum.rockstar.svprimitive.SShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Admin on 8/16/14.
 */
public class GuitarSurfaceRenderer implements SurfaceHolder.Callback {

    private int fretCount = 0;
    private float fretWidth = 0;

    private DrawFrame drawFrame;
    private float width, height;
    private Context context;

    private List<SShape> backGroundLayer = Collections.synchronizedList(new ArrayList<SShape>());
    private List<SShape> guitarString = Collections.synchronizedList(new ArrayList<SShape>());
    private List<SShape> pentatonicObjectsList = Collections.synchronizedList(new ArrayList<SShape>());


    private List<Pentatonic>pentatonicList = null;
    private Pentatonic currentPentatonic = null;
    private boolean isPentatonicLoaded = false;
    private int currentPentatonicStep = 0;
    private SShape current;

    private Paint circlePaint;

    public GuitarSurfaceRenderer(Context context) {
        fretCount = new Settings(context).getFretNumbers();
        this.context = context;
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawFrame = new DrawFrame(surfaceHolder);
        drawFrame.setRunning(true);
        drawFrame.start();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.RED);
        circlePaint.setAlpha(128);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int var, int width, int height) {
        Log.i("info"," SurfaceRendering  with = " + width + " height = " + height);
        this.width = width;
        this.height = height;

        fretWidth = this.width / fretCount;
        float step = fretWidth;
        float fret = 1;
        for(int i = 0; i<fretCount; i++) {
            SBitmap bitmap;
            if(((fret/2) % 1) > 0) { // Check if decimal value > 0
                bitmap = new SBitmap(this.width - step,0,fretWidth,this.height,context, R.drawable.b1);
            }else {
                bitmap = new SBitmap(this.width - step,0,fretWidth,this.height,context, R.drawable.b0);
            }
            backGroundLayer.add(bitmap);
            step += fretWidth;
            fret++;
        }

        float stepLad = 0;
        float ladWidth = fretWidth * 0.16f;
        float ld = ladWidth / 2.0f;
        for (int i = 0; i<fretCount+1; i++) {
            SBitmap bitmap = new SBitmap(this.width - stepLad - ld,0,ladWidth,this.height,context, R.drawable.lad);
            backGroundLayer.add(bitmap);
            stepLad += fretWidth;
        }

        // Draw string

        float shadowStep = ladWidth/2;
        float shadowLadStep = -(ladWidth/2);
        float heightDiv = this.height / 14;
        float stringStep = height - heightDiv * 0.8f;
        int alpha = 180;
        float c1 = 0.14f;
        float c2 = 0.2f;

        for (int i = 0; i<5; i++) {
            for (int j = 0; j<fretCount+1; j++) {
                SBitmap shadow1 = new SBitmap(
                        shadowLadStep,
                        stringStep/*height - heightDiv*/ + heightDiv * c1,
                        ladWidth,
                        heightDiv * c2,
                        context,
                        R.drawable.shadow_p2
                );
                shadowLadStep += fretWidth;
                shadow1.setAlpha(alpha);
                backGroundLayer.add(shadow1);

                SBitmap shadow = new SBitmap(
                        shadowStep,
                        stringStep/*height - heightDiv*/ + heightDiv * c2,
                        fretWidth - ladWidth,
                        heightDiv * c1,
                        context,
                        R.drawable.shadow_p1
                );
                shadow.setAlpha(alpha);
                backGroundLayer.add(shadow);
                shadowStep += fretWidth;
            }
            stringStep -= heightDiv * 2.4f;
            shadowStep = ladWidth/2;
            shadowLadStep = -(ladWidth/2);
            if(i<3) {
                c1+=0.02f;
                c2+=0.04f;
            }
        }

        float stringHeight = heightDiv * 0.12f;
        float inc = this.height - heightDiv;
        SGuitarString string1 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight, context,R.drawable.string_3);
        guitarString.add(string1);

        stringHeight = heightDiv * 0.14f;
        inc -= heightDiv * 2.5f;
        SGuitarString string2 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_3);
        guitarString.add(string2);

        stringHeight = heightDiv * 0.16f;
        inc -= heightDiv * 2.5f;
        SGuitarString string3 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_3);
        guitarString.add(string3);

        stringHeight = heightDiv * 0.25f;
        inc -= heightDiv * 2.5f;
        SGuitarString string4 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_5);
        guitarString.add(string4);

        stringHeight = heightDiv * 0.3f;
        inc -= heightDiv * 2.5f;
        SGuitarString string5 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_5);
        guitarString.add(string5);

        stringHeight = heightDiv * 0.35f;
        inc -= heightDiv * 2.5f;
        SGuitarString string6 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_6);
        guitarString.add(string6);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        drawFrame.setRunning(false);
        while (retry) {
            try {
                drawFrame.join();
                retry = false;
            }catch (InterruptedException e){}
        }
    }

    /* Draw Frame */
    class DrawFrame extends Thread {
        private boolean isRunning = false;
        private SurfaceHolder surfaceHolder;
        public DrawFrame(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }
        public void setRunning(boolean isRunning) {
            this.isRunning = isRunning;
        }
        @Override
        public void run() {
            Canvas canvas;
            while (isRunning) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;

                    canvas.drawColor(Color.WHITE);

                    synchronized (backGroundLayer) {
                        ListIterator<SShape> iterator = backGroundLayer.listIterator();
                        while (iterator.hasNext()) {
                            SShape object = (SShape)iterator.next();
                            object.draw(canvas);
                            if (object.remove)
                                iterator.remove();
                        }
                    }
                    synchronized (guitarString) {
                        ListIterator<SShape> iterator = guitarString.listIterator();
                        while (iterator.hasNext()) {
                            SShape object = (SShape)iterator.next();
                            object.draw(canvas);
                            if (object.remove)
                                iterator.remove();
                        }
                    }

                    synchronized (pentatonicObjectsList) {
                        Iterator<SShape> iterator = pentatonicObjectsList.iterator();
                        while (iterator.hasNext()) {
                            SShape object = (SShape)iterator.next();
                            object.draw(canvas);
                            if (object.remove)
                                iterator.remove();
                        }
                    }
                    // Update Logic
                    if (isPentatonicLoaded && currentPentatonic == null) {
                        if(currentPentatonicStep == pentatonicList.size())
                            currentPentatonicStep = 0;
                        int x = pentatonicList.get(currentPentatonicStep).bar + 1;
                        int y = pentatonicList.get(currentPentatonicStep).line;
                        currentPentatonic = pentatonicList.get(currentPentatonicStep);
                        int _y = (int) ((SGuitarString)guitarString.get(y)).getPosition().y;
                        int sh = (int)guitarString.get(y).getHeight();
                        current = new SCircle(width - (fretWidth * x) + (fretWidth / 2),  _y + sh / 2, height / 18,circlePaint);
                        pentatonicObjectsList.add(current);
                        currentPentatonicStep++;
                    }
                }finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    // Touch Down
    public void onTouchDown(final int x, final int y) {
        if (y >= 6)
            return;
        if(isPentatonicLoaded) {
            try{
                if(x == currentPentatonic.bar && y == currentPentatonic.line) {
                    currentPentatonic = null;
                    current.Remove(0);
                }
            }catch (NullPointerException e){}
        }

        ((SGuitarString)guitarString.get(y)).setAnimate();
    }
    public void onTouchUp(int x, int y) {
    }
    public void onTouchMove(int x, int y) {}

    /**********************************************************************************************/
    /* Load selected pentatonic */
    /**********************************************************************************************/
    public boolean LoadPentatonic(List<Pentatonic>pentatonics) {
        pentatonicList = pentatonics;
        isPentatonicLoaded = true;
        currentPentatonic = null;
        pentatonicObjectsList.clear();
        currentPentatonicStep = 0;
        return true;
    }
    public void ClosePlayPentatonic() {
        pentatonicList = null;
        isPentatonicLoaded = false;
        currentPentatonic = null;
        pentatonicObjectsList.clear();
        currentPentatonicStep = 0;
    }
}
