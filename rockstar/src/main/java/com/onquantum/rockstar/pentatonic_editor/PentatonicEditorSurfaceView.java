package com.onquantum.rockstar.pentatonic_editor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.onquantum.rockstar.R;
import com.onquantum.rockstar.settings.Settings;
import com.onquantum.rockstar.svprimitive.DrawEngine;
import com.onquantum.rockstar.svprimitive.SBitmap;
import com.onquantum.rockstar.svprimitive.SLayer;
import com.onquantum.rockstar.svprimitive.SLine;
import com.onquantum.rockstar.svprimitive.SRect;
import com.onquantum.rockstar.svprimitive.SRoundRect;
import com.onquantum.rockstar.svprimitive.SShape;
import com.onquantum.rockstar.svprimitive.SText;
import com.onquantum.rockstar.tabulature.SimpleTab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Admin on 8/10/15.
 */
public class PentatonicEditorSurfaceView extends DrawEngine {

    private SLayer backLayer = null;
    private SLayer touchLayer = null;
    private SLayer horizontalLineLayer = null;
    private SLayer verticalLine = null;
    private SLayer timeLineLayer = null;
    private SLayer playProgressLayer = null;

    private Paint paintString = null;
    private Paint paintQuartetDivider = null;
    private Paint paintDivider = null;

    private int quartetDivider = 100;
    private int slideStep = 10;
    private int quartetsCount = 60 * 4;
    private int slide = 0;

    private float touchX = 0, touchY = 0;

    private Paint paint;

    private int quartetNote = 1;
    private int selectedBar = 0;

    private boolean isPlay = false;

    //private List<Tab>tabs = new ArrayList<Tab>();
    private Set<Tab> tabs = new LinkedHashSet<>();

    private SRect progress;

    private int BPM = 240;
    private long quartetTimeMS = 0;

    public boolean needSave = false;

    private PentatonicEditorInterface pentatonicEditorInterface = null;
    public void SetPentatonicEditorInterface(PentatonicEditorInterface pentatonicEditorInterface) {
        this.pentatonicEditorInterface = pentatonicEditorInterface;
    }

    public PentatonicEditorSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackgroundColor(Color.WHITE);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setAlpha(250);

        BPM = new Settings(context).getBPM();
        quartetTimeMS = (long)(60f / BPM * 1000L);

    }

    @Override
    public void OnSurfaceChanged(int width, int height) {
        touchLayer = new SLayer();
        horizontalLineLayer = new SLayer();
        verticalLine = new SLayer();
        timeLineLayer = new SLayer();
        backLayer = new SLayer();
        playProgressLayer = new SLayer();

        SBitmap bitmapBack = new SBitmap(0, 0, width, height, this.context, R.drawable.white_back);
        bitmapBack.setVisibleArea(new RectF(0, height, width, 0));
        backLayer.addShape(bitmapBack);

        // Draw horizontal  line
        paintString = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintString.setStrokeWidth(2);
        paintString.setAlpha(160);
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
        paintDivider.setAlpha(180);
        paintQuartetDivider = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintQuartetDivider.setColor(Color.GRAY);
        paintQuartetDivider.setAlpha(255);
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

        DrawTimeLine();
        DrawProgress();

        addLayer(backLayer);
        addLayer(horizontalLineLayer);
        addLayer(verticalLine);
        addLayer(touchLayer);
        addLayer(timeLineLayer);
        addLayer(playProgressLayer);
    }

    private void DrawTimeLine() {
        movePentatonic(slide);
        synchronized (timeLineLayer) {
            Iterator<SShape> iterator = timeLineLayer.getShapeList().iterator();
            while(iterator.hasNext()) {
                iterator.next().Remove(0);
                iterator.remove();
            }
        }
        /******************************************************************************************/
        // Draw time line layer
        /******************************************************************************************/
        Paint timeMarkerBig = new Paint(Paint.ANTI_ALIAS_FLAG);
        timeMarkerBig.setColor(Color.DKGRAY);
        timeMarkerBig.setStrokeWidth(3f);
        for (int i = 0; i < quartetsCount * 4; i+=4) {
            SLine line = new SLine(i  * quartetDivider + slide, 0, i * quartetDivider + slide, 30, timeMarkerBig);
            line.setKinematic(true);
            timeLineLayer.addShape(line);
        }
        Paint timeMarkerBigSmall = new Paint(Paint.ANTI_ALIAS_FLAG);
        timeMarkerBigSmall.setColor(Color.DKGRAY);
        timeMarkerBigSmall.setStrokeWidth(2f);
        for (int i = 0; i < quartetsCount * 4; i++) {
            SLine line = new SLine(i  * quartetDivider + slide, 0, i * quartetDivider + slide, 20, timeMarkerBigSmall);
            line.setKinematic(true);
            timeLineLayer.addShape(line);
        }
        Paint timeMarkerBigSmaller = new Paint(Paint.ANTI_ALIAS_FLAG);
        timeMarkerBigSmaller.setColor(Color.DKGRAY);
        timeMarkerBigSmaller.setStrokeWidth(1f);
        for (int i = 0; i < quartetsCount * 4; i++) {
            SLine line = new SLine(i  * (quartetDivider / 4) + slide, 0, i * (quartetDivider / 4) + slide, 10, timeMarkerBigSmaller);
            line.setKinematic(true);
            timeLineLayer.addShape(line);
        }
        for (int i = 0; i < quartetsCount / 4; i++) {
            double t = quartetTimeMS * 4 * i / 1000f;
            int shiftText = t < 10 ? 20 : 30;
            String postfix = " s";
            if(t > 60) {
                t/=60;
                postfix = " m";
            }
            SText time = new SText(i * (quartetDivider * 4) + shiftText + slide, 20, 15);
            time.setText(String.format("%.1f",t) + postfix);
            timeLineLayer.addShape(time);
        }
    }

    private void DrawProgress() {
        synchronized (playProgressLayer) {
            Iterator<SShape> iterator = playProgressLayer.getShapeList().iterator();
            while(iterator.hasNext()) {
                iterator.next().Remove(0);
                iterator.remove();
            }
        }
        /******************************************************************************************/
        // Play progress
        /******************************************************************************************/
        Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.GREEN);
        progressPaint.setAlpha(50);
        progress = new SRect(slide, 0, 0, height,progressPaint);
        progress.setVisibleArea(new RectF(slide, getHeight(), getWidth(),0));
        progress.setKinematic(true);
        timeLineLayer.addShape(progress);
    }

    public boolean movePentatonic(int move) {
        int length = quartetsCount * quartetDivider;
        int outOfSlide = length + width;
        if((move > 0 && slide == 0) || move < 0 && ((Math.abs(slide) + width) == outOfSlide)) {
            return false;
        }

        if(move  > 0 && move > Math.abs(slide)) {
            move = Math.abs(slide);
        } else if(move < 0 && (Math.abs(slide) + width + Math.abs(move)) >= length) {
            return false;
        }

        slide += move;

        synchronized (verticalLine) {
            Iterator<SShape> iterator = verticalLine.getShapeList().iterator();
            while (iterator.hasNext()) {
                SShape shape = iterator.next();
                shape.move(move, 0);
            }
        }
        synchronized (touchLayer) {
            Iterator<SShape>iterator = touchLayer.getShapeList().iterator();
            while (iterator.hasNext()) {
                SShape shape = iterator.next();
                shape.move(move, 0);
            }
        }
        synchronized (timeLineLayer) {
            Iterator<SShape>iterator = timeLineLayer.getShapeList().iterator();
            while (iterator.hasNext()) {
                SShape shape = iterator.next();
                shape.move(move, 0);
            }
        }
        synchronized (playProgressLayer) {
            Iterator<SShape>iterator = playProgressLayer.getShapeList().iterator();
            while (iterator.hasNext()) {
                SShape shape = iterator.next();
                shape.move(move, 0);
            }
        }
        return true;
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
                if(!isTouchMove && !isPlay) {
                    if(touchY < 40 || touchY > height - 40)
                        break;
                    touchY -= 40;
                    int x = (int)((touchX + Math.abs(slide)) / quartetDivider);
                    int y = (int)(touchY / ((height - 80) / 6));
                    if(y > 6)
                        break;
                    long startTime = System.currentTimeMillis();
                    boolean tabExist = false;
                    for (Tab tab : tabs) {
                        if(tab.simpleTab.getGuitarString() == y && tab.simpleTab.getStartQuartet() == x) {
                            long endTime = System.currentTimeMillis() - startTime;
                            //Log.i("info","Exist : time = " + endTime);
                            if (pentatonicEditorInterface != null) {
                                pentatonicEditorInterface.OnSelectTab(tab);
                            }
                            tabExist = true;
                            break;
                        }
                    }
                    if(!tabExist) {
                        this.needSave = true;
                        AddTab(x, y, selectedBar, quartetNote);
                    }
                }
                isTouchMove = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int direction = (int)(motionEvent.getX() - touchX);
                if(Math.abs(direction) > slideStep) {
                    direction = slideStep * (direction / slideStep);
                    isTouchMove = true;
                    int move = direction;
                    movePentatonic(move);
                    touchX = motionEvent.getX();
                    touchY = motionEvent.getY();
                }
                break;
            }
            default:break;
        }
        if (pentatonicEditorInterface != null) {
            pentatonicEditorInterface.OnPentatonicEditorClickListener(motionEvent);
        }
        return true;
    }

    private void AddTab(int quartet, int guitarString, int guitarBar, int quartetNote) {
        int x = quartet - (Math.abs(slide) / quartetDivider);
        Paint p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        p1.setColor(Color.RED);
        p1.setAlpha(110);
        SRoundRect roundRect = new SRoundRect((quartetDivider * (x)) + (slide % quartetDivider), (height / 7) * (guitarString + 1) - 25,quartetDivider * 4 / quartetNote,50, p1);
        roundRect.setVisibleArea(new RectF(-(quartetDivider * 4), getHeight(), getWidth() + quartetDivider, 0));
        roundRect.setKinematic(true);
        touchLayer.addShape(roundRect);
        // Draw text
        Paint barTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barTextPaint.setColor(Color.WHITE);
        barTextPaint.setTextSize(20);
        barTextPaint.setTextAlign(Paint.Align.CENTER);
        SText bar = new SText(roundRect.getX() + roundRect.getWidth() / 2, roundRect.getY() + roundRect.getHeight() / 2, 10, barTextPaint);
        bar.setText(Integer.toString(guitarBar));
        touchLayer.addShape(bar);

        Tab tab = new Tab();
        tab.shape = roundRect;
        tab.text = bar;
        tab.simpleTab = new SimpleTab(guitarString, guitarBar, quartet, quartetNote);
        tab.index = tabs.size();
        tabs.add(tab);
        if (pentatonicEditorInterface != null) {
            pentatonicEditorInterface.OnAddTab(tab);
        }
    }

    public void LoadTabs(List<SimpleTab>simpleTabList) {
        if(simpleTabList == null)
            return;
        ClearAll();
        for (int i = 0; i < simpleTabList.size(); i++) {
            SimpleTab simpleTab = simpleTabList.get(i);
            AddTab((int)simpleTab.getStartQuartet(), simpleTab.getGuitarString(), simpleTab.getGuitarBar(),(int)simpleTab.getDuration());
        }
        this.needSave = false;
    }

    public void SetQuartetNote(int quartetNote) {
        this.quartetNote = quartetNote;
    }
    public void SetSelectedBar(int selectedBar) { this.selectedBar = selectedBar; }

    public List<SimpleTab> GetSimpleTabList() {
        List<SimpleTab>simpleTabs = new ArrayList<>();
        /*for (int i = 0; i < tabs.size(); i++)
            simpleTabs.add(tabs.get(i).simpleTab);*/
        Iterator<Tab>iterator = this.tabs.iterator();
        while (iterator.hasNext()) {
            simpleTabs.add(iterator.next().simpleTab);
        }
        return simpleTabs;
    }

    // Play
    Timer timer = null;
    float timeElapsed = 0f;
    float shift = 0;
    long del = 5;
    float pixPerDel = 2f;

    public void Play() {
        movePentatonic(-slide);
        shift = 0;
        isPlay = true;
        switch (BPM) {
            case 240:{
                del = 5;
                pixPerDel = 2f;
                break;
            }
            case 120 : {
                del = 5;
                pixPerDel = 1f;
                break;
            }
            case 80 : {
                del = 5;
                pixPerDel = 0.67f;
                break;
            }
            case 60 : {
                del = 10;
                pixPerDel = 1f;
                break;
            }
            case 30 : {
                del = 20;
                pixPerDel = 1f;
                break;
            }
            default:break;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeElapsed += pixPerDel;
                progress.setWidth(timeElapsed);
                if((progress.getWidth() - Math.abs(slide)) >= width / 2 && shift >= slideStep) {
                    movePentatonic(-slideStep);
                    shift = 0;
                }
                shift += pixPerDel;
            }
        }, 0, del);
    }

    public void Stop() {
        isPlay = false;
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        timeElapsed = 0;
        progress.setWidth(0);
    }

    // Fast forward and rewind
    private boolean isFastForward = false;
    private boolean isFastRewind = false;
    public void FastForward(boolean fastForward) {
        if(isFastForward) {
            isFastForward = false;
            return;
        }
        isFastForward = fastForward;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isFastForward && movePentatonic(-slideStep)) {
                    SystemClock.sleep(10);
                }
            }
        }).start();
    }
    public void FastRewind(boolean fastRewind) {
        if(isFastRewind) {
            isFastRewind = false;
            return;
        }
        isFastRewind = fastRewind;
        new Thread(new Runnable() {
            long length = quartetsCount * quartetDivider;
            @Override
            public void run() {
                while (isFastRewind && movePentatonic(slideStep)) {
                    SystemClock.sleep(10);
                }
            }
        }).start();
    }

    // Edit
    public void Undo() {
        if(isPlay)
            return;
        if(tabs != null && tabs.size() > 0) {
            /*tabs.get(tabs.size() - 1).shape.Remove(0);
            tabs.get(tabs.size() - 1).text.Remove(0);
            tabs.remove(tabs.size() - 1);*/
            Tab tab = (Tab)tabs.toArray()[tabs.size() - 1];
            tabs.remove(tab);
            tab.shape.Remove(0);
            tab.text.Remove(0);
            this.needSave = true;
        }
    }
    public void ClearAll() {
        if (isPlay)
            return;
        synchronized (this) {
            Iterator<Tab>iterator = tabs.iterator();
            while (iterator.hasNext()) {
                Tab tab = iterator.next();
                tab.shape.Remove(0);
                tab.text.Remove(0);
                iterator.remove();
            }
        }
    }

    public void RemoveTab(Tab tab) {
        tabs.remove(tab);
        tab.shape.Remove(0);
        tab.text.Remove(0);
    }

    public void SetBPM(int bpm) {
        this.BPM = bpm;
        quartetTimeMS = (long)(60f / BPM * 1000L);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DrawTimeLine();
                DrawProgress();
                if(pentatonicEditorInterface != null) {
                    pentatonicEditorInterface.OnBPMChange();
                }
            }
        }).start();
    }

    public class Tab {
        public int index = -1;
        public SShape shape;
        public SShape text;
        public SimpleTab simpleTab;
    }
}
