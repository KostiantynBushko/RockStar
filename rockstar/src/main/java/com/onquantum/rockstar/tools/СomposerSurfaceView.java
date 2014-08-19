package com.onquantum.rockstar.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.svprimitive.SBar;
import com.onquantum.rockstar.svprimitive.SBarCursor;
import com.onquantum.rockstar.svprimitive.SBitmap;
import com.onquantum.rockstar.svprimitive.SCircle;
import com.onquantum.rockstar.svprimitive.SLine;
import com.onquantum.rockstar.svprimitive.SShape;
import com.onquantum.rockstar.svprimitive.SText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;


/**
 * Created by Admin on 6/28/14.
 */
public class СomposerSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    DrawFrame drawFrame;
    List<SShape>drawDynamicObjects = Collections.synchronizedList(new ArrayList<SShape>());
    List<SShape>drawStaticObjects = Collections.synchronizedList(new ArrayList<SShape>());
    List<SShape>drawStaticString = Collections.synchronizedList(new ArrayList<SShape>());
    Set<SShape>drawTabulature = Collections.synchronizedSet(new LinkedHashSet<SShape>());
    List<SShape>barNumberLine = Collections.synchronizedList(new ArrayList<SShape>());

    private float touchX = 0, touchY = 0;
    private int shift = 0;
    private int width, height;
    private static final int RATIO = 32;
    private int dividerHeight = 0;
    private SLine lastLine = null;
    private int barWidth = 0;

    Paint paintString = null;
    Paint paintQuartetDivider = null;
    Paint paintDivider = null;

    private boolean isMoved = false;


    private final String[] notes = {"B3","F#3", "D3", "A2", "E2","B1"};

    Context context;

    SShape barCursor;


    public СomposerSurfaceView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        getHolder().addCallback(this);
        this.context = context;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawFrame = new DrawFrame(this.getHolder());
        drawFrame.setRunning(true);
        drawFrame.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        this.width = width;
        this.height = height;

        Log.i("info", " i = " + i);
        Log.i("info", " width = " + width + " | " + (width / RATIO));
        Log.i("info", " height = " + height + " | " + (height / RATIO));
        Log.i("info", String.valueOf((RATIO % 6)));

        dividerHeight = this.height / 8;
        barWidth = (this.width - RATIO * 2) / 24;


        paintDivider = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDivider.setColor(Color.GRAY);
        paintDivider.setAlpha(128);
        paintQuartetDivider = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintQuartetDivider.setColor(Color.DKGRAY);
        paintQuartetDivider.setStrokeWidth(5);

        Paint currentPaint = paintQuartetDivider;
        int count = 0;
        for (int j = RATIO * 2; j<RATIO * 2 * 50; j+= RATIO * 2) {
            SLine line = new SLine(j, dividerHeight,j,dividerHeight * 6,currentPaint);
            drawDynamicObjects.add(line);
            lastLine = line;
            count++;
            if(count == 4) {
                count = 0;
                currentPaint = paintQuartetDivider;
            }else {
                currentPaint = paintDivider;
            }

        }

        paintString = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintString.setStrokeWidth(2);
        int lineY = dividerHeight;
        for (int n = 0; n<6; n++) {
            SLine lineHeight = new SLine(0, lineY, this.width, lineY,paintString);
            lineY += dividerHeight;
            drawStaticString.add(lineHeight);
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                int s = RATIO * 2;
                for(int bar = 0; bar< 24; bar++) {
                    SBar barBitmap = new SBar(s,dividerHeight * 7,barWidth,dividerHeight,context,R.drawable.b0);
                    barBitmap.setNumber(bar+1);
                    barNumberLine.add(barBitmap);
                    s+=barWidth;
                }
                drawStaticObjects.addAll(barNumberLine);

                List<SShape>tmp = Collections.synchronizedList(new ArrayList<SShape>());
                s = RATIO * 2 + barWidth;
                for (int lad = 0; lad<24; lad++) {
                    SBitmap ladBitmap = new SBitmap(s,dividerHeight * 7,barWidth/10,dividerHeight, context, R.drawable.lad);
                    tmp.add(ladBitmap);
                    s += barWidth;
                }
                SBarCursor cursor = new SBarCursor(RATIO * 2,dividerHeight * 7, RATIO * 2 + barWidth,dividerHeight * 8);
                tmp.add(cursor);
                barCursor = cursor;
                drawStaticObjects.addAll(tmp);
            }
        }).start();
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

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMask = motionEvent.getAction();
        switch (actionMask) {
            case MotionEvent.ACTION_DOWN: {
                touchX = motionEvent.getX();
                touchY = motionEvent.getY();
                break;
            }
            case MotionEvent.ACTION_UP:{
                touchX = motionEvent.getX();
                touchY = motionEvent.getY();
                if (!isMoved) {
                    int x = (int)((touchX + Math.abs(shift)) / (RATIO * 2));
                    int _x = (int)(touchX / (RATIO * 2));
                    int y = (int)(touchY / dividerHeight);
                    Log.i("info","X = " + x + " Y = " + y);
                    if(x > 0 && y > 0 && y < 7) {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.BLUE);

                        Paint textInCirclr = new Paint(Paint.ANTI_ALIAS_FLAG);
                        textInCirclr.setColor(Color.WHITE);
                        SCircle circle = new SCircle(RATIO * 2 * _x + RATIO, dividerHeight * y, (int)(RATIO * 0.8), paint);
                        circle.drawNumber(((SBarCursor)barCursor).getNumber(),textInCirclr);
                        //drawDynamicObjects.add(circle);
                        drawTabulature.add(circle);
                    }else {
                        int barX = (int)((touchX - RATIO * 2) / barWidth);
                        Log.i("info"," bar cursor X = " + barX);
                        barCursor.move(barX * barWidth + RATIO * 2, 0);
                        ((SBar)barNumberLine.get(((SBarCursor)barCursor).getNumber() - 1)).setColor(Color.BLACK);
                        ((SBarCursor)barCursor).setNumber(barX + 1);
                        ((SBar)barNumberLine.get(barX)).setColor(Color.RED);
                    }
                }
                isMoved = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                //Log.i("info"," shift = " + shift + " | " + (Math.abs(shift) % RATIO));
                int direction = (int)(motionEvent.getX() - touchX);
                if(Math.abs(direction) > RATIO && (touchY < dividerHeight * 7)) {
                    isMoved = true;
                    int move = direction > 0 ? RATIO * 2 : -(RATIO * 2);
                    float lastLineX = lastLine.getX();
                    if((move > 0 && shift == 0) || (move < 0 && lastLineX < width))
                        break;
                    shift += move;
                    touchX = motionEvent.getX();
                    touchY = motionEvent.getY();

                    synchronized (drawDynamicObjects) {
                        ListIterator<SShape>iter = drawDynamicObjects.listIterator();
                        while (iter.hasNext()) {
                            SShape shape = iter.next();
                            shape.move(move, 0);
                        }
                    }
                    synchronized (drawTabulature) {
                        Iterator<SShape>iter = drawTabulature.iterator();
                        while (iter.hasNext()) {
                            SShape shape = iter.next();
                            shape.move(move, 0);
                        }
                    }
                }
                break;
            }
            default:
                break;
        }
        return true;
    }


    class DrawFrame extends Thread {
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
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    canvas.drawColor(Color.WHITE);

                    // Draw string
                    synchronized (drawStaticString) {
                        ListIterator<SShape>iter = drawStaticString.listIterator();
                        while (iter.hasNext()) {
                            SShape shape = iter.next();
                            shape.draw(canvas);
                        }
                    }
                    // Draw dynamic objects
                    synchronized (drawDynamicObjects) {
                        ListIterator<SShape> iter = drawDynamicObjects.listIterator();
                        while (iter.hasNext()) {
                            SShape shape = iter.next();
                            shape.draw(canvas);
                            if (shape.remove)
                                iter.remove();
                        }
                    }

                    synchronized (drawTabulature) {
                        Iterator<SShape> iter = drawTabulature.iterator();
                        int count  = 0;
                        while (iter.hasNext()) {
                            SShape shape = iter.next();
                            shape.draw(canvas);
                            if(shape.remove)
                                iter.remove();
                            count++;
                        }
                        Log.i("info"," darawTabulature count = " + count);
                    }
                    // Draw static objects
                    synchronized (drawStaticObjects) {
                        ListIterator<SShape>iter = drawStaticObjects.listIterator();
                        while (iter.hasNext()) {
                            SShape shape = iter.next();
                            shape.draw(canvas);
                        }
                    }

                    Paint notePanel = new Paint(Paint.ANTI_ALIAS_FLAG);
                    notePanel.setColor(Color.GRAY);
                    canvas.drawRect(0,0,RATIO * 2,height,notePanel);

                    int step = dividerHeight;
                    Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    textPaint.setTextSize(RATIO);
                    textPaint.setColor(Color.WHITE);
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    for(int i = 0; i<6; i++) {
                        canvas.drawText(notes[i], RATIO , step+RATIO/2,textPaint);
                        step += dividerHeight;
                    }

                }finally {
                    if(canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
}
