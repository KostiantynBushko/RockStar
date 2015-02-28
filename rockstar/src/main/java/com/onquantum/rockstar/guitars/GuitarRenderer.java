package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.Pentatonic;
import com.onquantum.rockstar.svprimitive.SBitmap;
import com.onquantum.rockstar.svprimitive.SCircle;
import com.onquantum.rockstar.svprimitive.SGuitarString;
import com.onquantum.rockstar.svprimitive.SLine;
import com.onquantum.rockstar.svprimitive.SShape;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.SocketHandler;

/**
 * Created by Admin on 8/16/14.
 */
public class GuitarRenderer implements SurfaceHolder.Callback {

    private final int BACKGROUND_LAYER = 1000;
    private final int STRING_SHADOW_LAYER =2000;
    private final int STRING_LAYER = 3000;
    private final int PENTATONIC_LAYER = 4000;
    private final int BAR_NUMBER_LAYER = 20001;

    private int fretCount = 0;
    private float fretWidth = 0;

    private DrawFrame drawFrame;
    private float width, height;
    private Context context;

    private List<SShape> backGroundLayer = Collections.synchronizedList(new ArrayList<SShape>());
    private List<SShape> guitarString = Collections.synchronizedList(new ArrayList<SShape>());
    private List<SShape> pentatonicObjectsList = Collections.synchronizedList(new ArrayList<SShape>());
    private List<SShape> testLine = Collections.synchronizedList(new ArrayList<SShape>());
    private List<SShape> barNumberObjects = Collections.synchronizedList(new ArrayList<SShape>());
    private List<SShape> pentatonicMask = Collections.synchronizedList(new ArrayList<SShape>());

    private List<SShape> drawObjects = Collections.synchronizedList(new ArrayList<SShape>());


    private boolean loaded = false;
    private boolean enableRendering = false;

    private List<Pentatonic>pentatonicList = null;
    private Pentatonic currentPentatonic = null;
    private boolean isPentatonicLoaded = false;
    private int currentPentatonicStep = 0;
    private SShape current;

    private Paint circlePaint;
    private Paint touchPaint;

    private BitSet fretsMark = new BitSet(24);

    // Frets view
    private boolean fretsNumberVisible = false;
    private boolean touchVisible = false;
    private boolean sliderFretsVisible = false;

    //Slide
    private int Slide = 0;

    public GuitarRenderer(Context context) {
        fretCount = new Settings(context).getFretNumbers();
        this.context = context;

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.RED);
        circlePaint.setAlpha(200);

        touchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        touchPaint.setColor(Color.BLUE);
        touchPaint.setAlpha(128);

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

        Settings settings = new Settings(context);
        touchVisible = settings.isTouchesVisible();
        fretsNumberVisible = settings.isFretsNumberVisible();
        sliderFretsVisible = settings.isFretsSliderVisible();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawFrame = new DrawFrame(surfaceHolder);
        drawFrame.setRunning(true);
        drawFrame.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int var, int width, int height) {
        Log.i("info"," GuitarRenderer surfaceChanged with = " + width + " height = " + height + "  loaded = " + loaded);
        if (loaded)
            return;

        Slide = 0;
        backGroundLayer.clear();
        guitarString.clear();
        testLine.clear();
        drawObjects.clear();

        this.width = width;
        this.height = height;

        fretWidth = this.width / fretCount;
        float step = fretWidth;
        float heightDiv = this.height / 14;

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.RED);
        for (int i = 0; i<6; i++) {
            SLine line = new SLine(0,height - (height / 6f * i),width,height - (height / 6f * i),linePaint);
            line.setLayer(PENTATONIC_LAYER);
            testLine.add(line);
        }

        // Draw frets
        float fret = 1;
        for(int i = 0; i<24; i++) {
            SBitmap bitmap;
            if(fretsMark.get(i)){
                bitmap = new SBitmap(this.width - step,0,fretWidth,this.height,context, R.drawable.b1);
            }else{
                bitmap = new SBitmap(this.width - step,0,fretWidth,this.height,context, R.drawable.b0);
                //SCircle circle = new SCircle(this.width - step,this.height / 2, this.height / 18);
            }
            bitmap.setLayer(BACKGROUND_LAYER);
            bitmap.setKinematic(true);
            backGroundLayer.add(bitmap);
            step += fretWidth;
            fret++;
        }

        float stepLad = 0;
        float ladWidth = fretWidth * 0.16f;
        float ld = ladWidth / 2.0f;
        for (int i = 0; i < 24/*fretCount+1*/; i++) {
            SBitmap bitmap = new SBitmap(this.width - stepLad - ld,0,ladWidth,this.height,context, R.drawable.lad);
            bitmap.setLayer(BACKGROUND_LAYER);
            bitmap.setKinematic(true);
            backGroundLayer.add(bitmap);
            stepLad += fretWidth;
        }

        // Draw string
        float shadowStep = ladWidth/2;
        float shadowLadStep = -(ladWidth/2);
        float stringStep = height - heightDiv * 0.8f;
        int alpha = 60;
        float c1 = 0.12f;
        float c2 = 0.18f;

        for (int i = 0; i<6; i++) {
            for (int j = 0; j<fretCount+1; j++) {
                SBitmap shadow1 = new SBitmap(
                        shadowLadStep,
                        stringStep + heightDiv * c1,
                        ladWidth,
                        heightDiv * c2,
                        context,
                        R.drawable.shadow_p2
                );
                shadowLadStep += fretWidth;
                shadow1.setAlpha(alpha);
                shadow1.setLayer(STRING_SHADOW_LAYER);
                backGroundLayer.add(shadow1);

                SBitmap shadow = new SBitmap(
                        shadowStep,
                        stringStep + heightDiv * c2,
                        fretWidth - ladWidth,
                        heightDiv * c1,
                        context,
                        R.drawable.shadow_p1
                );
                shadow.setAlpha(alpha);
                shadow.setLayer(STRING_SHADOW_LAYER);
                backGroundLayer.add(shadow);
                shadowStep += fretWidth;
            }
            stringStep -= heightDiv * 2.48f;
            shadowStep = ladWidth/2;
            shadowLadStep = -(ladWidth/2);
            c1+=0.04f;
            c2+=0.04f;
        }

        float stringHeight = heightDiv * 0.12f;
        float inc = this.height - heightDiv;
        SGuitarString string1 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight, context,R.drawable.string_3);
        guitarString.add(string1);
        string1.setLayer(STRING_LAYER);

        stringHeight = heightDiv * 0.14f;
        inc -= heightDiv * 2.5f;
        SGuitarString string2 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_3);
        guitarString.add(string2);
        string2.setLayer(STRING_LAYER);

        stringHeight = heightDiv * 0.16f;
        inc -= heightDiv * 2.5f;
        SGuitarString string3 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_3);
        guitarString.add(string3);
        string3.setLayer(STRING_LAYER);

        stringHeight = heightDiv * 0.25f;
        inc -= heightDiv * 2.5f;
        SGuitarString string4 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_5);
        guitarString.add(string4);
        string4.setLayer(STRING_LAYER);

        stringHeight = heightDiv * 0.3f;
        inc -= heightDiv * 2.5f;
        SGuitarString string5 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_5);
        guitarString.add(string5);
        string5.setLayer(STRING_LAYER);

        stringHeight = heightDiv * 0.35f;
        inc -= heightDiv * 2.5f;
        SGuitarString string6 = new SGuitarString(0,inc + stringHeight / 2, width,stringHeight,context,R.drawable.string_6);
        string6.setLayer(STRING_LAYER);
        guitarString.add(string6);

        /*******************************************************************************************
         *
         *
         *
         ******************************************************************************************/
        //drawObjects.addAll(testLine);
        drawObjects.addAll(backGroundLayer);
        drawObjects.addAll(guitarString);
        drawObjects.addAll(pentatonicObjectsList);

        /*for (int i = drawObjects.size() - 1; i >= 0; i--) {
            for (int j = 0; j<i; j++) {
                if(drawObjects.get(j).getLayer() > drawObjects.get(j+1).getLayer()){
                    SShape temp = drawObjects.get(j);
                    drawObjects.set(j,drawObjects.get(j+1));
                    drawObjects.set(j+1,temp);
                }
            }
        }*/
        showFretsNumber(fretsNumberVisible);
        sortLayer();
        loaded = true;
    }

    /*
     * Call this method every time when new objects added to drawable list
     */
    private void sortLayer() {
        for (int i = drawObjects.size() - 1; i >= 0; i--) {
            for (int j = 0; j<i; j++) {
                if(drawObjects.get(j).getLayer() > drawObjects.get(j+1).getLayer()){
                    SShape temp = drawObjects.get(j);
                    drawObjects.set(j,drawObjects.get(j+1));
                    drawObjects.set(j+1,temp);
                }
            }
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        drawFrame.setRunning(false);
        while (retry) {
            try {
                drawFrame.join();
                retry = false;
            }catch (InterruptedException e){
                e.printStackTrace();
            }
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
                if (enableRendering) {
                    // FPS
                    //long now = System.currentTimeMillis();
                    long timestart = System.currentTimeMillis(); //Get time at start of loop for FPS calc
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas(null);
                        if (canvas == null)
                            continue;
                        canvas.drawColor(Color.BLACK);

                        synchronized (drawObjects) {
                            ListIterator<SShape> iterator = drawObjects.listIterator();
                            while (iterator.hasNext()) {
                                SShape object = iterator.next();
                                object.draw(canvas);
                                if (object.remove)
                                    iterator.remove();
                            }
                        }
                        // Update Logic
                        /*if (isPentatonicLoaded && currentPentatonic == null) {
                            if(currentPentatonicStep == pentatonicList.size())
                                currentPentatonicStep = 0;
                            int x = pentatonicList.get(currentPentatonicStep).bar + 1;
                            int y = pentatonicList.get(currentPentatonicStep).line;
                            currentPentatonic = pentatonicList.get(currentPentatonicStep);
                            int _y = (int) ((SGuitarString)guitarString.get(y)).getPosition().y;
                            int sh = (int)guitarString.get(y).getHeight();
                            current = new SCircle(width - (fretWidth * x) + (fretWidth / 2),  _y + sh / 2, height / 18,circlePaint);
                            pentatonicObjectsList.add(current);
                            current.setKinematic(true);
                            current.setLayer(PENTATONIC_LAYER);
                            drawObjects.add(current);
                            currentPentatonicStep++;
                        }*/
                    }finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }

                    /*System.out.println("FPS:" + fps);
                    fps++;
                    if(now > (mLastTime + 1000)) {
                        mLastTime = now;
                        fps = ifps;
                        fps = 0;
                    }*/

                    //Frame limiting
                    long timeend = System.currentTimeMillis();
                    long frametime = timeend-timestart;
                    int sleepfor = (int) ((1000/30)-frametime);
                    if (sleepfor>0){
                        try {
                            this.sleep(sleepfor);
                        } catch (InterruptedException e) {}
                    }
                    while (sleepfor<0) {
                        sleepfor+=33.33333333333333;
                    }
                }
            }
        }
    }

    /**********************************************************************************************/
    /* Touches */
    /**********************************************************************************************/
    public void onTouchDown(final int x, final int y) {
        if (y >= 6)
            return;
        if(isPentatonicLoaded && !playWillStart) {
            try{
                if(x == currentPentatonic.bar + Slide  && y == currentPentatonic.line) {
                    currentPentatonic = null;
                    current.Remove(0);
                }
            }catch (NullPointerException e){}
        }
        if (touchVisible){
            int _y = (int) ((SGuitarString)guitarString.get(y)).getPosition().y;
            int sh = (int)guitarString.get(y).getHeight();
            SCircle circle = new SCircle(width - (fretWidth * (x - Slide + 1)) + (fretWidth / 2),  _y + sh / 2, height / 18, touchPaint);
            circle.Remove(250);
            drawObjects.add(circle);
        }
        ((SGuitarString)guitarString.get(y)).setAnimate();
    }
    public void onTouchUp(int x, int y) {}
    public void onTouchMove(int x, int y) {}

    /***********************************************************************************************
     * Load selected pentatonic
     **********************************************************************************************/
    public boolean LoadPentatonic(List<Pentatonic>pentatonics) {
        ClosePlayPentatonic();
        while (!pentatonicIsStoped);
        pentatonicIsStoped = false;
        stopPlay = false;
        pentatonicList = pentatonics;
        isPentatonicLoaded = true;
        drawPentatonicMask();
        drawSimplePentatonic();
        startPlayPentatonic();
        return true;
    }
    public void ClosePlayPentatonic() {
        Log.i("info","GuitarRenderer : ClosePlayPentatonic");
        removePentatonicMask();
        stopPlay = true;
        playWillStart = false;
        pentatonicList = null;
        isPentatonicLoaded = false;
        currentPentatonic = null;
        pentatonicObjectsList.clear();
        currentPentatonicStep = 0;
        if(current != null)
            current.Remove(0);
    }
    private void drawSimplePentatonic() {
        int x = pentatonicList.get(currentPentatonicStep).bar + 1;
        int y = pentatonicList.get(currentPentatonicStep).line;
        currentPentatonic = pentatonicList.get(currentPentatonicStep);
        int _y = (int) ((SGuitarString)guitarString.get(y)).getPosition().y;
        int sh = (int)guitarString.get(y).getHeight();
        current = new SCircle(width - (fretWidth * x) + (fretWidth / 2),  _y + sh / 2, height / 18,circlePaint);
        pentatonicObjectsList.add(current);
        current.setKinematic(true);
        current.setLayer(PENTATONIC_LAYER);
        drawObjects.add(current);
        currentPentatonicStep++;
    }
    /***********************************************************************************************
     *  Play pentatonic engine
     **********************************************************************************************/
    private boolean playWillStart = false;
    boolean stopPlay = false;
    boolean pentatonicIsStoped = true;
    private void startPlayPentatonic() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Log.i("info","RENDERER Play Pentatonic");
                while (!stopPlay) {
                    if (isPentatonicLoaded && currentPentatonic == null) {
                        playWillStart = true;
                        Log.i("info"," pentatonicList size = " + pentatonicList.size());
                        if(currentPentatonicStep == pentatonicList.size())
                            currentPentatonicStep = 0;
                        int x = pentatonicList.get(currentPentatonicStep).bar + 1;
                        int y = pentatonicList.get(currentPentatonicStep).line;
                        currentPentatonic = pentatonicList.get(currentPentatonicStep);
                        int _y = (int) ((SGuitarString)guitarString.get(y)).getPosition().y;
                        int sh = (int)guitarString.get(y).getHeight();
                        current = new SCircle(width - (fretWidth * x) + (fretWidth / 2),  _y + sh / 2, height / 18,circlePaint);
                        current.RemoveMilliseconds(currentPentatonic.playTime);
                        pentatonicObjectsList.add(current);
                        current.setKinematic(true);
                        current.setLayer(PENTATONIC_LAYER);
                        drawObjects.add(current);
                        currentPentatonicStep++;
                        try {
                            TimeUnit.MILLISECONDS.sleep(currentPentatonic.delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        currentPentatonic = null;
                    }
                }
                pentatonicIsStoped = true;
                Log.i("info"," RENDERER : STOP PLAY PENTATONIC ");
            }
        }).start();
    }
    private void drawPentatonicMask() {
        if (pentatonicList != null && !pentatonicList.isEmpty()) {
            List<Pentatonic>pList = new ArrayList<Pentatonic>();
            for (Pentatonic item : pentatonicList) {
                for (Pentatonic pItem : pList) {
                    if(item.position.equals(pItem.position.x, pItem.position.y)) {
                        item = null;
                        break;
                    }
                }
                if (item != null)
                    pList.add(item);
            }
            Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            maskPaint.setColor(Color.YELLOW);
            maskPaint.setAlpha(160);

            for (Pentatonic p : pList) {
                int x = p.bar + 1;
                int y = p.line;
                int _y = (int) ((SGuitarString)guitarString.get(y)).getPosition().y;
                int sh = (int)guitarString.get(y).getHeight();
                SCircle circle = new SCircle(width - (fretWidth * x) + (fretWidth / 2),  _y + sh / 2, height / 18,maskPaint);
                circle.setKinematic(false);
                circle.setLayer(PENTATONIC_LAYER);
                pentatonicMask.add(circle);
            }
            drawObjects.addAll(pentatonicMask);
            sortLayer();
        }
    }
    private void removePentatonicMask() {
        if (pentatonicList != null && !pentatonicList.isEmpty()) {
            for (SShape shape : pentatonicMask) {
                shape.remove = true;
            }
            pentatonicMask.clear();
        }
    }
    /***********************************************************************************************
     *  Renderer state change
     **********************************************************************************************/
    public void enableRendering(boolean rendering) {
        enableRendering = rendering;
    }
    public void resetLoaded(){
        loaded = false;
        Slide = 0;
    }
    public void resetFretSlider() {
        Slide = 0;
    }
    /***********************************************************************************************
     *  Neck view
     **********************************************************************************************/
    public void setFretNumberVisible(boolean visible){
        fretsNumberVisible = visible;
    }
    public void setShowTouchEnable(boolean visible){
        touchVisible = visible;
    }

    public void slide(int slide) {
        Slide += slide;
        synchronized (drawObjects) {
            ListIterator<SShape> iterator = drawObjects.listIterator();
            while (iterator.hasNext()) {
                SShape object = (SShape)iterator.next();
                if (object.isKinematic()) {
                    object.move((int)(fretWidth * slide), 0);
                }
            }
        }
    }

    private void showFretsNumber(boolean visible) {
        if(visible) {
            Log.i("info"," GuitarRenderer Show fret number");
            float step = fretWidth;
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            circlePaint.setColor(Color.WHITE);
            circlePaint.setAlpha(200);
            for(int i = 0; i<24; i++) {
                SCircle circle;
                if(!fretsMark.get(i)){
                    float radius = 0;
                    if(fretWidth > (height / 6)) {
                        Log.i("info"," height / 18 " + (height / 18));
                        radius = height / 18;
                    } else {
                        Log.i("info"," fretWidth / 3 " + (fretWidth / 3));
                        radius = fretWidth / 3;
                    }
                    circle = new SCircle(this.width - step + fretWidth / 2, this.height / 2, radius, circlePaint);
                    circle.setLayer(BAR_NUMBER_LAYER);
                    circle.setKinematic(true);
                    circle.drawNumber(i+1, textPaint);
                    barNumberObjects.add(circle);
                }
                step += fretWidth;
            }
            drawObjects.addAll(barNumberObjects);
        } else {
            barNumberObjects.clear();
        }
    }
}

