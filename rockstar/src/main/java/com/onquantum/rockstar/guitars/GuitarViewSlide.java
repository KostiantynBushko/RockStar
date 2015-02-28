package com.onquantum.rockstar.guitars;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.onquantum.rockstar.Settings;
import com.onquantum.rockstar.common.GuitarString;
import com.onquantum.rockstar.common.Pentatonic;
import com.onquantum.rockstar.sequencer.QSoundPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 8/16/14.
 */
public class GuitarViewSlide extends GuitarAbstract {

    private Context context;
    private SoundPool soundPool;
    private GuitarRenderer guitarRenderer;

    private final int frets;
    private int width, height;
    private int titleBarH = 0;
    private int Slide = 0;

    private int[][] touchMask = new int[25][7];
    private List<GuitarString>simpleTouchList = new ArrayList<GuitarString>(10);

    public GuitarViewSlide(final Context context, AttributeSet attributeSet) {
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
        x = (int)(width - event.getX(pointIndex)) / (width / frets);
        x+= Slide;
        if (y > 5)
            return false;

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
                touchMask[x][y] = 0;
                guitarRenderer.onTouchUp((int)x,(int)y);
                simpleTouchList.get(pointID).stop();
                break;
            }
            case MotionEvent.ACTION_UP: {
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
    public void LoadPentatonicFile(String fileName) {
        List<Pentatonic> pentatonics = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("pentatonic/" + fileName)));
            String str = reader.readLine();
            while (str != null) {
                Pentatonic pentatonic = new Pentatonic();
                String[] parce = str.split("\\s+");
                if (parce.length == 4){
                    pentatonic.bar = Integer.parseInt(parce[0]);
                    pentatonic.line = Integer.parseInt(parce[1]);
                    pentatonic.delay = Long.parseLong(parce[2]);
                    pentatonic.playTime = Long.parseLong(parce[3]);
                    pentatonic.position.set(pentatonic.bar, pentatonic.line);
                    pentatonics.add(pentatonic);
                }
                str = reader.readLine();
            }
            if(guitarRenderer.LoadPentatonic(pentatonics)) {
                if (context instanceof GuitarInterface) {
                    ((GuitarInterface)context).onPentatonicSuccessLoaded(fileName);
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ClosePlayPentatonic() {
        guitarRenderer.ClosePlayPentatonic();
    }

    @Override
    public void Stop() {
        //soundPool.release();
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
