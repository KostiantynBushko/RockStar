package com.onquantum.rockstar.guitar;

import android.content.Context;
import android.graphics.Point;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.Constants;
import com.onquantum.rockstar.common.GuitarString;
import com.onquantum.rockstar.common.Pentatonic;
import com.onquantum.rockstar.file_system.FileSystem;
import com.onquantum.rockstar.sequencer.QSoundPool;
import com.onquantum.rockstar.tabulature.SimpleTab;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 8/16/14.
 */
public class GuitarSlideView extends GuitarAbstractView {

    private Context context;
    private SoundPool soundPool;
    private GuitarRenderer guitarRenderer;

    private final int frets;
    private int width, height, fretWidth;
    private int titleBarH = 0;
    private int Slide = 0;

    private int[][] touchMask = new int[25][7];
    private List<GuitarString>simpleTouchList = new ArrayList<GuitarString>(10);

    private boolean openString = false;

    public GuitarSlideView(final Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
        getHolder().addCallback(guitarRenderer = new GuitarRenderer(context));
        this.context = context;
        frets = new Settings(context).getFretNumbers();
        soundPool = QSoundPool.getInstance().getSoundPool();
        if (soundPool == null) {
            QSoundPool.getInstance().loadSound();
        }
        for(int i = 0; i<10; i++) {
            simpleTouchList.add(new GuitarString(0,0,0,context,soundPool));
        }

        openString = new Settings(context).getOpenStringStatus();
    }

    @Override
    public void Start() {
        QSoundPool.getInstance().setOnSoundPoolSuccessLoaded(new QSoundPool.OnSoundPoolSuccessLoaded() {
            @Override
            public void soundPoolSuccessLoaded() {
                isTouchEnable = true;
                if (guitarRenderer != null)
                    guitarRenderer.enableRendering(true);
                if (onSoundLoadedCompleteListener != null) {
                    Log.i("info"," SOUND LOAD COMPLETE ");
                    onSoundLoadedCompleteListener.onSoundLoadedComplete();
                }
            }
        });
        super.Start();
    }

    @Override
    public void onSizeChanged(int width, int height, int oldw, int oldh) {
        Log.i("info","GUITAR VIEW SIZE CHANGE  width = " + width + "  height = " + height);
        this.width = width;
        this.height = height;
        fretWidth = width / frets;
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        titleBarH = size.y - this.height;
        if(oldh != height)
            guitarRenderer.resetLoaded();
        super.onSizeChanged(width, height, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!isTouchEnable)
            return false;

        int actionMask = event.getActionMasked();
        int pointIndex = event.getActionIndex();
        final int pointID = event.getPointerId(pointIndex);

        int x,y;
        y = (int)((height - (event.getY(pointIndex))) / (height / 6));
        x = (int)(width - event.getX(pointIndex)) / fretWidth + 1;
        if (y > 5)
            return false;

        if(openString) {
            if(x > frets - 1){
                x = 0;
            } else {
                x+= Slide;
            }
        } else {
            x+= Slide;
        }

        switch(actionMask) {
            case MotionEvent.ACTION_POINTER_DOWN:{
                if(touchMask[x][y] == 1)
                    break;
                guitarRenderer.onTouchDown((int)x,(int)y);
                simpleTouchList.get(pointID).set((int)x,(int)y);
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if(touchMask[x][y] == 1)
                    break;
                guitarRenderer.onTouchDown((int)x,(int)y);
                simpleTouchList.get(pointID).set((int)x,(int)y);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:  {
                if(x > touchMask.length)
                    break;
                touchMask[x][y] = 0;
                guitarRenderer.onTouchUp((int)x,(int)y);
                simpleTouchList.get(pointID).stop();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if(x > touchMask.length)
                    break;
                touchMask[x][y] = 0;
                guitarRenderer.onTouchUp((int)x,(int)y);
                simpleTouchList.get(pointID).stop();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                guitarRenderer.onTouchMove(x,y);
                int _x = simpleTouchList.get(pointID).x;
                if(_x != x) {
                    simpleTouchList.get(pointID).move(x,y);
                }
            }
            default:break;
        }

        return true;
    }

    /***********************************************************************************************
     * Load pentatonic from file
     **********************************************************************************************/
    @Override
    public void LoadTabsFile(String fileName) {
        int BPM = new Settings(context).getBPM();
        File file = new File(FileSystem.GetTabsFilesPath() + "/" + fileName + Constants.TAB_FILE_EXTENSION);
        if(!file.exists())
            return;
        List<SimpleTab>simpleTabList = new ArrayList<SimpleTab>();
        simpleTabList = SimpleTab.LoadTabsFromXmlFile(file.toString());
        if(simpleTabList == null || simpleTabList.size() == 0)
            return;

        List<Pentatonic> pentatonics = new ArrayList<>();
        for(int i = 0; i < simpleTabList.size(); i++) {
            Pentatonic pentatonic = new Pentatonic();
            SimpleTab simpleTab = simpleTabList.get(i);
            pentatonic.bar = simpleTab.getGuitarBar();
            pentatonic.line = simpleTab.getGuitarString();
            pentatonic.delay = simpleTab.getDurationMS(BPM);
            pentatonic.playTime = simpleTab.getStartQuartetMS(BPM);
            pentatonic.position.set(pentatonic.bar, pentatonic.line);
            pentatonics.add(pentatonic);
        }
        if(guitarRenderer.LoadPentatonic(pentatonics)) {
            if (context instanceof GuitarInterface) {
                ((GuitarInterface)context).onPentatonicSuccessLoaded(fileName);
            }
        }
    }

    @Override
    public void ClosePlayPentatonic() {
        guitarRenderer.ClosePlayPentatonic();
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    // Neck view
    @Override
    public void SetFretsNumberVisible(boolean visibility){
        guitarRenderer.setFretNumberVisible(visibility);
    }
    @Override
    public void SetShowTouchesVisible(boolean visibility){
        guitarRenderer.setShowTouchEnable(visibility);
    }
    @Override
    public void SetFretsSliderVisible(boolean visibility){
        guitarRenderer.resetLoaded();
    }

    @Override
    public void slideChange(int slide) {
        Slide += slide;
        guitarRenderer.slide(slide);
        GuitarString.setSlide(slide);
    }
}
